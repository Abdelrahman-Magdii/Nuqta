package com.spring.nuqta.chatPot.Controller;

import com.spring.nuqta.chatPot.Dto.ChatResponse;
import com.spring.nuqta.chatPot.Services.AskDoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ask Doctor Controller")
@RestController
@RequestMapping("/api/ask-doctor")
public class AskDoctorController {

    private final AskDoctorService askDoctorService;

    @Autowired
    public AskDoctorController(AskDoctorService askDoctorService) {
        this.askDoctorService = askDoctorService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody String message) {
        return new ChatResponse(this.askDoctorService.chat(message));
    }


}
