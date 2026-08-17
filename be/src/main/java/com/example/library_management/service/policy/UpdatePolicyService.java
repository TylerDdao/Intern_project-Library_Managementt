package com.example.library_management.service.policy;

import com.example.library_management.dto.request.policy.PolicyRequest;
import com.example.library_management.dto.response.policy.PolicyResponse;
import com.example.library_management.model.Policy;
import com.example.library_management.repository.PolicyRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UpdatePolicyService {
    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public PolicyResponse updatePolicy(PolicyRequest request) {
        Policy policy = policyRepository.findByKey(request.getKey())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.policy.not.found", null, LocaleContextHolder.getLocale())));
        String oldValue = policy.getValue();
        policy.setValue(request.getValue());
        policyRepository.save(policy);
        logger.log("Updated policy {}, {} -> {} ", policy.getKey(), oldValue, request.getValue());
        return new PolicyResponse(policy);
    }
}
