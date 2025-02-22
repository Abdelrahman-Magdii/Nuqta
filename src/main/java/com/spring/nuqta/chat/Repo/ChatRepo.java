package com.spring.nuqta.chat.Repo;

import com.spring.nuqta.chat.Entity.ChatMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends JpaRepository<ChatMessageModel, Long> {
}
