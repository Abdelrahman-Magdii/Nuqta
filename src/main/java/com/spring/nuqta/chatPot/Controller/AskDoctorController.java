package com.spring.nuqta.chatPot.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ask Doctor Controller")
@RestController
@RequestMapping("/api/ask-doctor")
public class AskDoctorController {

    private final ChatClient chat;
    
    @Autowired
    public AskDoctorController(ChatClient.Builder chat) {
        this.chat = chat.build();
    }

    @PostMapping("/question")
    public String askQuestion(@RequestBody String question) {
        return chat.prompt().user(question).call().content();
    }
}
