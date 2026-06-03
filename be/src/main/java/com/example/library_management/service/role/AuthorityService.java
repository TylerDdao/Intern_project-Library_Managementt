package com.example.library_management.service.role;

import com.example.library_management.dto.request.AuthorityRequest;
import com.example.library_management.dto.response.AuthorityResponse;
import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import com.example.library_management.repository.FeatureRepository;
import com.example.library_management.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuthorityService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeatureRepository featureRepository;

    public AuthorityResponse assignAuthority(AuthorityRequest request){
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<String> featureNames = request.getFeatures();
        List<Feature> features = new ArrayList<>();
        featureNames.forEach(name->{
            Feature feature = featureRepository.findByName(name).orElseGet(()->{
                Feature newFeature = new Feature();
                newFeature.setName(name);
                return featureRepository.save(newFeature);
            });
            features.add(feature);
        });

        features.forEach(feature -> {
            if(!role.getFeatures().contains(feature)){
                role.getFeatures().add(feature);
            }
        });
        Role savedRole = roleRepository.save(role);
        log.info("Assigning {} to role {}", featureNames, savedRole.getName());
        return new AuthorityResponse(role.getName(), featureNames, "Assigned");
    }

    public AuthorityResponse unassignAuthority(AuthorityRequest request){
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<String> featureNames = request.getFeatures();
        List<Feature> features = new ArrayList<>();
        featureNames.forEach(name ->
                featureRepository.findByName(name).ifPresent(features::add)
        );

        role.getFeatures().removeAll(features);

        List<String> featureNamesToLog = features.stream()
                .map(Feature::getName)
                .toList();

        Role savedRole = roleRepository.save(role);
        log.info("Unassigning {} from role {}", featureNamesToLog, savedRole.getName());
        return new AuthorityResponse(role.getName(), featureNames, "Unassigned");
    }
}
