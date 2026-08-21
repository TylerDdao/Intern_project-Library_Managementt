package com.example.library_management.dto.response.policy;

import com.example.library_management.model.Policy;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class PolicyResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String key;
    private String value;

    public PolicyResponse(Policy policy){
        this.key = policy.getKey();
        this.value = policy.getValue();
    }
}
