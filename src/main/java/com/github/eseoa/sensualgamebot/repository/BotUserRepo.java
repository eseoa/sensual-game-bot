package com.github.eseoa.sensualgamebot.repository;

import com.github.eseoa.sensualgamebot.model.BotUser;
import com.github.eseoa.sensualgamebot.model.BotUserGroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotUserRepo extends JpaRepository<BotUser, Long> {

}
