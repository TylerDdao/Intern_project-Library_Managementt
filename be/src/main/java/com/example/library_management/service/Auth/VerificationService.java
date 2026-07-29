package com.example.library_management.service.Auth;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.model.EmailVerification;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.repository.VerificationRepository;
import com.example.library_management.service.MailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class VerificationService {
    @Autowired
    MailService mailService;

    @Autowired
    VerificationRepository verificationRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageSource messageSource;

    private static final int CODE_LENGTH = 5;
    private static final int EXPIRY_MINUTES = 10;

    private String generateRandomDigits(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Scheduled(cron = "0 0 0 * * *") // every day
    public void cleanupExpiredAndUnverifiedCodes() {
        verificationRepository.deleteByExpiresAtBeforeAndVerifiedFalse(LocalDateTime.now());
    }

    public String sendVerificationEmail(UserRequest request, Locale locale){
        if (userRepository.existsByEmail(request.getEmail())){
            return "error.Email.has.been.used";
        }
        if(verificationRepository.existsByEmailAndVerifiedFalseAndExpiresAtAfter(request.getEmail(), LocalDateTime.now())){
            return "error.Code.is.already.sent";
        }
        String code;
        do {
            code = generateRandomDigits(CODE_LENGTH);
        }
        while (verificationRepository.existsByEmailAndCodeAndVerifiedFalseAndExpiresAtAfter(request.getEmail(), code, LocalDateTime.now()));

        EmailVerification verification = new EmailVerification();
        verification.setCode(code);
        verification.setEmail(request.getEmail());
        LocalDateTime now = LocalDateTime.now();
        verification.setExpiresAt(now.plusMinutes(EXPIRY_MINUTES));

        try {
            mailService.sentVerificationEmail(request.getEmail(), request.getFullName(), code, locale);
            verificationRepository.save(verification);
            return "verification.Code.is.sent";
        }
        catch (MessagingException e){
            return e.toString();
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        Optional<EmailVerification> latest = verificationRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (latest.isEmpty()) return false;

        EmailVerification vc = latest.get();

        if (vc.isVerified()) return false;
        if(!vc.getIsActive()) return false;
        if (vc.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!vc.getCode().equals(inputCode)) return false;

        vc.setVerified(true);
        vc.setIsActive(false);
        verificationRepository.save(vc);
        return true;
    }
}
