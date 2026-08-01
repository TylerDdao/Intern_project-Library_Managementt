package com.example.library_management.dto.request.policy;

import com.example.library_management.model.Policy;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyRequest {
    private String key =null;
    private String value =null;

    public PolicyRequest(){

    }

    public PolicyRequest(Policy policy){
        this.key = policy.getKey();
        this.value = policy.getValue();
    }
}
