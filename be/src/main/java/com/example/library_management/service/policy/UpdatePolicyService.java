package com.example.library_management.service.policy;

import com.example.library_management.dto.request.policy.PolicyRequest;
import com.example.library_management.dto.response.policy.PolicyResponse;
import com.example.library_management.model.Policy;
import com.example.library_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UpdatePolicyService {
    @Autowired
    PolicyRepository policyRepository;

    @Autowired
    MessageSource messageSource;

    public PolicyResponse updatePolicy(PolicyRequest request) {
        Policy policy = policyRepository.findByKey(request.getKey())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.policy.not.found", null, LocaleContextHolder.getLocale())));

        policy.setValue(request.getValue());
        policyRepository.save(policy);
        return new PolicyResponse(policy);
    }
}
