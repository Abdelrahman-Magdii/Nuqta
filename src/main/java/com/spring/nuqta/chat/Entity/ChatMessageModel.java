package com.spring.nuqta.chat.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Chat_message")
public class ChatMessageModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    private String sender;

    private String recipient;

    private String content;

    private LocalDateTime timestamp;
}
