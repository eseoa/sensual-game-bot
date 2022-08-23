package com.github.eseoa.sensualgamebot.repository;

import com.github.eseoa.sensualgamebot.model.BotUserGroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotUserGroupChatRepo extends JpaRepository<BotUserGroupChat, Long> {
    Optional<BotUserGroupChat> findByBotUserIdAndGroupChatId(long botUserId, long groupChatId);
}
