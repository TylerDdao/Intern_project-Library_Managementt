package com.example.library_management.service.policy;

import com.example.library_management.dto.request.policy.PolicyRequest;
import com.example.library_management.dto.response.policy.PolicyResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Policy;
import com.example.library_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GetPolicyService {
    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    MessageSource messageSource;

    public PolicyResponse getPolicy(String key){
        Policy policy = policyRepository.findByKey(key).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "POLICY-NOT-FOUND", messageSource.getMessage("error.policy.not.found",null, LocaleContextHolder.getLocale())));
        return new PolicyResponse(policy);
    }
}
