package com.example.library_management.dto.response.policy;

import com.example.library_management.model.Policy;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PolicyResponse {
    private String key;
    private String value;

    public PolicyResponse(Policy policy){
        this.key = policy.getKey();
        this.value = policy.getValue();
    }
}
