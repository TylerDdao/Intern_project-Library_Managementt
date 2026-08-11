package com.example.library_management.service.auth;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.model.EmailVerification;
import com.example.library_management.model.ResetPasswordCode;
import com.example.library_management.model.User;
import com.example.library_management.repository.ResetPasswordCodeRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.repository.VerificationRepository;
import com.example.library_management.service.MailService;
import com.example.library_management.service.mail.VerificationMailService;
import com.example.library_management.util.AuditLogger;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private VerificationMailService verificationMailService;

    @Autowired
    private VerificationRepository verificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResetPasswordCodeRepository resetPasswordCodeRepository;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private MessageSource messageSource;

    @Value("${frontend.url}")
    private String frontendUrl;

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
    public void cleanUpCode() {
        verificationRepository.deleteByExpiresAtBeforeAndVerifiedFalse(LocalDateTime.now());
        resetPasswordCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    public String sendResetPasswordEmail(String email){
        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));
        if(resetPasswordCodeRepository.existsByUser_EmailAndIsResetFalseAndExpiresAtAfter(email, LocalDateTime.now())){
            return "error.Code.is.already.sent";
        }
        String code;
        do {
            code = generateRandomDigits(CODE_LENGTH);
        }
        while (resetPasswordCodeRepository.existsByUser_EmailAndCodeAndIsResetFalseAndExpiresAtAfter(email, code, LocalDateTime.now()));

        String resetLink = frontendUrl
                + "/reset-password/"
                + code
                + "/"
                + email;

        ResetPasswordCode resetPasswordCode = new ResetPasswordCode();
        resetPasswordCode.setCode(code);
        resetPasswordCode.setUser(user);
        LocalDateTime now = LocalDateTime.now();
        resetPasswordCode.setExpiresAt(now.plusMinutes(EXPIRY_MINUTES));

        try {
            verificationMailService.sendResetPasswordEmail(email, resetLink, EXPIRY_MINUTES);
            resetPasswordCodeRepository.save(resetPasswordCode);
            return "reset.password.Link.is.sent";
        }
        catch (Exception e){
            return e.toString();
        }
    }

    public String sendVerificationEmail(UserRequest request){
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())){
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
            verificationMailService.sentVerificationEmail(request.getEmail(), code, EXPIRY_MINUTES);
            verificationRepository.save(verification);
            return "verification.Code.is.sent";
        }
        catch (Exception e){
            return e.toString();
        }
    }

    public boolean verifyResetPasswordCode(String email, String inputCode) {
        Optional<ResetPasswordCode> latest = resetPasswordCodeRepository.findTopByUser_EmailOrderByCreatedAtDesc(email);

        if (latest.isEmpty()) {
            return false;
        }

        ResetPasswordCode vc = latest.get();
        if (vc.isReset()) return false;
        if (!vc.getIsActive()) return false;
        if (vc.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        return vc.getCode().equals(inputCode);
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
        logger.log("Verified email {}", email);
        return true;
    }
}
