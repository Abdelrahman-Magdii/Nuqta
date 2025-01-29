package com.spring.nuqta.chatPot.Controller;

import com.spring.nuqta.chatPot.Service.GeminiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ask Doctor Controller", description = "Endpoints for interacting with the Gemini chat model.")
@RestController
@RequestMapping("/api")
public class ChatController {


    private final GeminiService geminiService;

    @Autowired
    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/chat")
    public String generateContent(@RequestParam String message) {
        return geminiService.generateContent(message);
    }
}