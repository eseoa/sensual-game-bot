package com.github.eseoa.sensualgamebot.repository;

import com.github.eseoa.sensualgamebot.model.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatRepo extends JpaRepository<GroupChat, Long> {
}
