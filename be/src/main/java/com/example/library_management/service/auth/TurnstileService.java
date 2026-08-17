package com.example.library_management.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TurnstileService {

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    @Value("${turnstile.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verify(String token) {
        System.out.println("SECRET KEY: " + secretKey);
        if (token == null || token.isBlank()) {
            return false;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", token);

        try {
            TurnstileVerifyResponse response = restTemplate.postForObject(VERIFY_URL, form, TurnstileVerifyResponse.class);
            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    public static class TurnstileVerifyResponse {
        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}