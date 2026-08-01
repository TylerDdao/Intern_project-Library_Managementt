package com.example.library_management.dto.response.policy;

import com.example.library_management.model.Policy;
import lombok.Data;

@Data
public class PolicyResponse {
    private String key;
    private String value;

    public PolicyResponse(Policy policy){
        this.key = policy.getKey();
        this.value = policy.getValue();
    }
}
