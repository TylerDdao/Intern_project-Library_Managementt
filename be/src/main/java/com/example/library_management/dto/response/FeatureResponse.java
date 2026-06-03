package com.example.library_management.dto.response;

import com.example.library_management.model.Feature;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeatureResponse {
    private long id;
    private String name;

    public  FeatureResponse(Feature feature){
        this.id = feature.getId();
        this.name = feature.getName();
    }
}
