package com.github.eseoa.sensualgamebot.repository;

import com.github.eseoa.sensualgamebot.model.BotUser;
import com.github.eseoa.sensualgamebot.model.BotUserPreference;
import com.github.eseoa.sensualgamebot.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotUserPreferenceRepo extends JpaRepository<BotUserPreference, Long> {
    Optional<BotUserPreference> findByBotUserIdAndPreferenceId (long botUserId, long preferenceId);
}
