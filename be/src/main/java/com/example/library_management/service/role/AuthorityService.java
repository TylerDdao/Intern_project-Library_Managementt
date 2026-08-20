package com.example.library_management.service.role;

import com.example.library_management.dto.request.role.AuthorityRequest;
import com.example.library_management.dto.response.auth.AuthorityResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import com.example.library_management.repository.FeatureRepository;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public AuthorityResponse assignAuthority(AuthorityRequest request){
        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ROLE-NOT-FOUND", messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));

        List<String> featureNames = request.getFeatures();
        List<Feature> features = new ArrayList<>();
        featureNames.forEach(name->{featureRepository.findByName(name).ifPresent(features::add);});

        features.forEach(feature -> {
            if(!role.getFeatures().contains(feature)){
                role.getFeatures().add(feature);
            }
        });
        Role savedRole = roleRepository.save(role);
        logger.log("Assigned {} to role {}", featureNames, savedRole.getName());
        return new AuthorityResponse(role.getName(), featureNames, "Assigned");
    }

    public AuthorityResponse unassignAuthority(AuthorityRequest request){
        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));

        List<String> featureNames = request.getFeatures();
        List<Feature> features = new ArrayList<>();
        featureNames.forEach(name -> featureRepository.findByName(name).ifPresent(features::add));

        role.getFeatures().removeAll(features);

        List<String> featureNamesToLog = features.stream().map(Feature::getName).toList();

        Role savedRole = roleRepository.save(role);
        logger.log("Unassigned {} from role {}", featureNamesToLog, savedRole.getName());
        return new AuthorityResponse(role.getName(), featureNames, "Unassigned");
    }
}
