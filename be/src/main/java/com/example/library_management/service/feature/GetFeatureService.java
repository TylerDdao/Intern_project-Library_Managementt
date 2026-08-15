package com.example.library_management.service.feature;

import com.example.library_management.dto.response.feature.FeatureResponse;
import com.example.library_management.model.Feature;
import com.example.library_management.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GetFeatureService {
    @Autowired
    private FeatureRepository featureRepository;

    public Page<FeatureResponse> getFeature(int page, int limit, String sortBy, String sortDir, String name){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Feature> features;

        if(name != null) features = featureRepository.findByNameContaining(name, pageable);
        else features = featureRepository.findAll(pageable);

        return features.map(FeatureResponse::new);
    }
}
