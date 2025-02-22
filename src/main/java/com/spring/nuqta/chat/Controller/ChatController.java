package com.spring.nuqta.chat.Controller;


import com.spring.nuqta.chat.Entity.ChatMessageModel;
import com.spring.nuqta.chat.Repo.ChatRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@CrossOrigin("*")
@Controller
public class ChatController {


    final ChatRepo chatRepo;
    @Autowired
    public ChatController(ChatRepo chatRepo) {
        this.chatRepo = chatRepo;
    }

    @MessageMapping("/ws")
    @SendTo("/topic/message")
    public ChatMessageModel sendMessage(@Payload ChatMessageModel chatMessageModel) {
        return chatRepo.save(chatMessageModel);
    }


}
