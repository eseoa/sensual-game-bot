package com.github.eseoa.sensualgamebot.service;

import com.github.eseoa.sensualgamebot.command.Command;
import com.github.eseoa.sensualgamebot.model.*;
import com.github.eseoa.sensualgamebot.model.enums.PreferenceType;
import com.github.eseoa.sensualgamebot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatabaseService {
    private BotUserRepo botUserRepo;
    private PreferenceRepo preferenceRepo;
    private ScenarioRepo scenarioRepo;
    private GroupChatRepo groupChatRepo;
    private BotUserGroupChatRepo botUserGroupChatRepo;
    private BotUserPreferenceRepo botUserPreferenceRepo;
    public BotUser getBotUser (long id) {
        Optional<BotUser> userOptional = botUserRepo.findById(id);
        return userOptional.orElseGet(() -> register(id));
    }
    public List<Preference> getModeratedPreferences (PreferenceType preferenceType) {
        return preferenceRepo.findByModeratedAndPreferenceType(true, preferenceType);
    }
    public List<Preference> getPreferences (PreferenceType preferenceType) {
        return preferenceRepo.findByPreferenceType(preferenceType);
    }
    public List<Preference> getUserPreferences (BotUser botUser, PreferenceType preferenceType) {
        List<Preference> preferences = botUser.getPreferences();
        if(preferences != null) {
            return botUser.getPreferences()
                    .stream()
                    .filter(preference -> preference.getPreferenceType().equals(preferenceType))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Preference getRandomModeratedPreference (PreferenceType preferenceType) {
        List<Preference> preferences = getModeratedPreferences(preferenceType);
        return preferences.get(new Random().nextInt(preferences.size()));
    }

    public Preference getRandomPreference (PreferenceType preferenceType) {
        List<Preference> preferences = getPreferences(preferenceType);
        return preferences.get(new Random().nextInt(preferences.size()));
    }

    public Preference getUserPreference (PreferenceType preferenceType, List<BotUser> botUsers) {
        List<Preference> preferences = new ArrayList<>();
        for(BotUser botUser : botUsers) {
            preferences.addAll(botUser.getPreferences());
        }
        preferences = preferences.stream().filter(preference -> preference.getPreferenceType().equals(preferenceType)).toList();
        if(preferences.isEmpty()) {
            return null;
        }
        return preferences.get(new Random().nextInt(preferences.size()));
    }

    public Scenario getRandomScenario () {
        List<Scenario> scenarios = scenarioRepo.findAll();
        return scenarios.get(new Random().nextInt(scenarios.size()));
    }

    public boolean unpinPreference (BotUser botUser, Preference preference) {
        Optional<BotUserPreference> botUserPreferenceOptional =
                botUserPreferenceRepo.findByBotUserIdAndPreferenceId(botUser.getId(), preference.getId());
        if(botUserPreferenceOptional.isEmpty()) {
            return false;
        }
        botUserPreferenceRepo.deleteById(botUserPreferenceOptional.get().getId());
        return true;
    }
    public Preference getPreference(long preferenceId) {
        Optional<Preference> preferenceOptional = preferenceRepo.findById(preferenceId);
        if(preferenceOptional.isEmpty()) {
            return null;
        }
        return preferenceOptional.get();
    }

    public boolean pinPreference (BotUser botUser, Preference preference) {
        Optional<BotUserPreference> botUserPreferenceOptional =
                botUserPreferenceRepo.findByBotUserIdAndPreferenceId(botUser.getId(), preference.getId());
        if(botUserPreferenceOptional.isPresent()) {
            return false;
        }
        botUserPreferenceRepo.save(new BotUserPreference(botUser, preference, true));
        return true;
    }

    public boolean addPreference (BotUser botUser, PreferenceType preferenceType, String name) {
        Optional<Preference> preferenceOptional =
                preferenceRepo.findByPreferenceTypeAndName(preferenceType, name);
        if(preferenceOptional.isPresent()) {
            return false;
        }
        savePreference(new Preference(name, "", preferenceType, false));
        return true;
    }
    public GroupChat createGroupChat (long id) {
        GroupChat groupChat = new GroupChat(id);
        groupChatRepo.save(groupChat);
        return groupChat;
    }
    public void saveBotUser(BotUser botUser) {
        botUserRepo.save(botUser);
    }
    public void savePreference (Preference preference) {
        preferenceRepo.save(preference);
    }
    private BotUser register(long id) {
        BotUser botUser = new BotUser(id, Command.MAIN_MENU, false);
        botUserRepo.save(botUser);
        return botUser;
    }

    @Autowired
    public void setBotUserRepo(BotUserRepo botUserRepo) {
        this.botUserRepo = botUserRepo;
    }

    @Autowired
    public void setPreferenceRepo(PreferenceRepo preferenceRepo) {
        this.preferenceRepo = preferenceRepo;
    }

    @Autowired
    public void setScenarioRepo(ScenarioRepo scenarioRepo) {
        this.scenarioRepo = scenarioRepo;
    }

    @Autowired
    public void setGroupChatRepo(GroupChatRepo groupChatRepo) {
        this.groupChatRepo = groupChatRepo;
    }

    @Autowired
    public void setBotUserGroupChatRepo(BotUserGroupChatRepo botUserGroupChatRepo) {
        this.botUserGroupChatRepo = botUserGroupChatRepo;
    }

    @Autowired
    public void setBotUserPreferenceRepo(BotUserPreferenceRepo botUserPreferenceRepo) {
        this.botUserPreferenceRepo = botUserPreferenceRepo;
    }

    public void deletePreference(Preference preference) {
        preferenceRepo.deleteById(preference.getId());
    }

    public GroupChat getGroupChat(long id) {
        Optional<GroupChat> groupChatOptional = groupChatRepo.findById(id);
        return groupChatOptional.orElseGet(() -> createGroupChat(id));
    }

    public BotUserGroupChat getBotUserGroupChat(long botUserId, long chatId) {
        Optional<BotUserGroupChat> botUserGroupChatOptional = botUserGroupChatRepo.findByBotUserIdAndGroupChatId(botUserId, chatId);
        return botUserGroupChatOptional.orElseGet(() -> addUserToGroupChat(botUserId, chatId));
    }

    private BotUserGroupChat addUserToGroupChat(long botUserId, long chatId) {
        BotUser botUser = getBotUser(botUserId);
        GroupChat groupChat = getGroupChat(chatId);
        BotUserGroupChat botUserGroupChat = new BotUserGroupChat(botUser, groupChat);
        botUserGroupChatRepo.save(botUserGroupChat);
        return botUserGroupChat;
    }

    public void deleteBotUserGroupChat(long id) {
        botUserGroupChatRepo.deleteById(id);
    }
}
