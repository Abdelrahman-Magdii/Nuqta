package com.spring.nuqta.chatPot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    @Autowired
    private RestTemplate restTemplate;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String generateContent(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Escape double quotes in the prompt
        String escapedPrompt = prompt.replace("\"", "\\\"");

        // Construct the JSON payload
        String requestBody = String.format("{\"contents\": [{\"parts\":[{\"text\": \"%s\"}]}]}", escapedPrompt);


        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = GEMINI_API_URL + "?key=" + geminiApiKey;

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return responseEntity.getBody();
    }
}