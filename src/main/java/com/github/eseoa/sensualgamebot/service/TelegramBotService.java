package com.github.eseoa.sensualgamebot.service;

import com.github.eseoa.sensualgamebot.command.Command;
import com.github.eseoa.sensualgamebot.config.BotConfig;
import com.github.eseoa.sensualgamebot.keyboard.KeyboardFactory;
import com.github.eseoa.sensualgamebot.model.*;
import com.github.eseoa.sensualgamebot.model.enums.PreferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Service
public class TelegramBotService extends TelegramLongPollingBot {
    private static final String HELLO_TEXT = "старт текст";
    private static final String SHOW_TEXT = "показать что - то из избранного или из популярного?";
    private static final String SHOW_CHOOSE_TYPE_TEXT = "какой тип увлечений показать?";
    private static final String EMPTY_LIST_TEXT = "список пока пуст";
    private static final String SHOW_PREFERENCES_START_TEXT = "показываю увлечения";
    private static final String SHOW_PREFERENCES_END_TEXT = "закончил показ";
    private static final String UNKNOWN_COMMAND_TEXT = "я не знаю такой команды, вовзращаюсь в главное меню";
    private static final String BACK_HOME_TEXT = "возврашаюсь в главное меню";
    private static final String ERROR_TEXT = "произошла ошибка на сервере, возврашаюсь в главное меню";
    private static final String UNPIN_TEXT = "убрал из избранного";
    private static final String UNPIN_ERROR_TEXT = "данное увлечение уже не находится в избарнном";
    private static final String PIN_TEXT = "добавил в избранное";
    private static final String PIN_ERROR_TEXT = "данное увлечение уже  находится в избранном";
    private static final String ADD_CHOOSE_TYPE_TEXT = "какой тип увлечений добавить?";
    private static final String START_GAME_TEXT = "что интересует?";
    private final DatabaseService databaseService;
    private final KeyboardFactory keyboardFactory;
    private final BotConfig botConfig;

    @Autowired
    public TelegramBotService(DatabaseService databaseService, KeyboardFactory keyboardFactory, BotConfig botConfig) {
        this.databaseService = databaseService;
        this.keyboardFactory = keyboardFactory;
        this.botConfig = botConfig;
    }
    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            long chatId = callbackQuery.getMessage().getChatId();
            String data = callbackQuery.getData();
            long botUserId = callbackQuery.getFrom().getId();
            doForCallback(chatId, data, botUserId);
            return;
        }
        if(update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            long botUserId = message.getFrom().getId();
            BotUser botUser = databaseService.getBotUser(botUserId);
            boolean isPrivateChat = message.getChat().getType().equals("private");
            if (isPrivateChat) {
                if(botUser.isAddCommandCheck() && (message.hasText() || message.hasPhoto())) {
                    addCustomPreferenceMenu(chatId, message, botUserId);
                    return;
                }
                if(message.hasText()) {
                    privateChatMenu(message.getText(), chatId, botUserId);
                    return;
                }
                return;
            }
            if(message.hasText()) {
                groupChatMenu(message, chatId, botUserId);
            }
        }
    }
    private void groupChatMenu(Message message, long chatId, long botUserId) {
        databaseService.getGroupChat(chatId);
        BotUserGroupChat botUserGroupChat = databaseService.getBotUserGroupChat(botUserId,chatId);
        GroupChat groupChat = databaseService.getGroupChat(chatId);
        String text = message.getText();
        if(message.getLeftChatMember() != null) {
            databaseService.deleteBotUserGroupChat(botUserGroupChat.getId());
        }
        List<Long> botUsersId = groupChat.getBotUsers().stream().map(BotUser::getId).toList();
        if(botUsersId.size() < 2) {
            sendMessage(chatId, "Чтобы играть со всеми пользователями, нужно чтобы каждый в этом чате отдал мне по команде");
        }
        switch (text) {
            case "/start" -> sendMessage(chatId, "start", keyboardFactory.createKeyboard(Command.START_GAME));
            case "/Случайное из избранного" -> showRandomFavoritePreferences(chatId, botUsersId);
            case "/Случайное из популярного" -> showRandomPopularPreferences(chatId);
            case "/Случайное из пользовательского" -> showRandomPreferences(chatId);
            case "/Случайный сценарий" -> ShowRandomScenario(chatId);
        }


    }
    private void addCustomPreferenceMenu(long chatId, Message message, long botUserId) {
        BotUser botUser = databaseService.getBotUser(botUserId);
        List<Preference> preferences = botUser.getPreferences();
        Preference preference = preferences.get(preferences.size() - 1);
        if(message.hasText() && (message.getText().equals("/start") ||  message.getText().equals("В начало"))) {
            databaseService.unpinPreference(botUser,preference);
            databaseService.deletePreference(preference);
            sendHomePage(BACK_HOME_TEXT, chatId, botUserId);
            return;
        }
        switch (botUser.getCommand()) {
            case ADD_NAME -> addName(chatId, message, botUser, preference);
            case ADD_DESCRIPTION -> addDescription(chatId, message, botUser, preference);
            case ADD_PHOTO -> addPhoto(chatId, message, botUser, preference);
            default -> sendHomePage(ERROR_TEXT, chatId, botUserId);
        }
    }
    private void addPhoto(long chatId, Message message, BotUser botUser, Preference preference) {
        if(message.hasText()) {
            botUser.setCommand(Command.MAIN_MENU);
            botUser.setAddCommandCheck(false);
            databaseService.saveBotUser(botUser);
            sendMessage(chatId, "Закрепил за тобой предпочтение", keyboardFactory.createKeyboard(Command.ADD_CUSTOM));
            return;
        }
        if(message.hasPhoto()) {
            preference.setFileId(message.getPhoto().get(0).getFileId());
            databaseService.savePreference(preference);
            botUser.setCommand(Command.MAIN_MENU);
            botUser.setAddCommandCheck(false);
            databaseService.saveBotUser(botUser);
            sendMessage(chatId, "Закрепил за тобой предпочтение", keyboardFactory.createKeyboard(Command.ADD_CUSTOM));
            return;
        }
        sendMessage(chatId, "Отправь фото или поставь -", keyboardFactory.createKeyboard(Command.ADD_PHOTO));
    }
    private void addDescription(long chatId, Message message, BotUser botUser, Preference preference) {
        if(!message.hasText()) {
            sendMessage(chatId, "Введи описание или поставь -", keyboardFactory.createKeyboard(Command.ADD_DESCRIPTION));
            return;
        }
        preference.setDescription(message.getText());
        botUser.setCommand(Command.ADD_PHOTO);
        databaseService.saveBotUser(botUser);
        databaseService.savePreference(preference);
        sendMessage(chatId, "Отправь фото или поставь -", keyboardFactory.createKeyboard(Command.ADD_PHOTO));
    }
    private void addName(long chatId, Message message, BotUser botUser, Preference preference) {
        if(!message.hasText()) {
            sendMessage(chatId, "Введи название", keyboardFactory.createKeyboard(Command.ADD_NAME));
            return;
        }
        preference.setName(message.getText());
        botUser.setCommand(Command.ADD_DESCRIPTION);
        databaseService.saveBotUser(botUser);
        databaseService.savePreference(preference);
        sendMessage(chatId, "Введи описание или поставь -", keyboardFactory.createKeyboard(Command.ADD_DESCRIPTION));
    }
    private void doForCallback(long chatId, String text, long botUserId) {
        Command command = Command.valueOf(text.split("=")[0]);
        long preferenceId = Long.parseLong(text.split("=")[1]);
        Preference preference = databaseService.getPreference (preferenceId);
        switch (command) {
            case DELETE -> unpinPreference(chatId, botUserId, preference);
            case ADD_POPULAR -> pinPreference (chatId, botUserId, preference);
            default -> sendHomePage(ERROR_TEXT, chatId, botUserId);
        }
    }
    private void pinPreference(long chatId, long botUserId, Preference preference) {
        BotUser botUser = databaseService.getBotUser(botUserId);
        if (databaseService.pinPreference(botUser, preference)) {
            sendMessage(chatId, PIN_TEXT);
        }
        else {
            sendMessage(chatId, PIN_ERROR_TEXT);
        }
    }
    private void unpinPreference(long chatId, long botUserId, Preference preference) {
        BotUser botUser = databaseService.getBotUser(botUserId);
        if (databaseService.unpinPreference(botUser, preference)) {
            sendMessage(chatId, UNPIN_TEXT);
        }
        else {
            sendMessage(chatId, UNPIN_ERROR_TEXT);
        }
    }
    public void privateChatMenu (String messageText, long chatId, long botUserId) {
        switch (messageText) {
            case "/start" -> sendHomePage(HELLO_TEXT, chatId, botUserId);
            case "Добавить свое" -> sendPreferenceTypesAddPage(chatId);
            case "Добавить свой фетиш" -> sendAddCustomPreference(chatId, botUserId, PreferenceType.FETISH);
            case "Добавить свою прелюдию" -> sendAddCustomPreference(chatId, botUserId, PreferenceType.FOREPLAY);
            case "Добавить свое место" -> sendAddCustomPreference(chatId, botUserId, PreferenceType.PLACE);
            case "Добавить свою позу" -> sendAddCustomPreference(chatId, botUserId, PreferenceType.POSE);
            case "Добавить свою игрушку" -> sendAddCustomPreference(chatId, botUserId, PreferenceType.TOY);
            case "Показать" -> sendShowPage(chatId);
            case "Показать избранное" -> sendEroTypesShowPage(chatId, Command.SHOW_MY);
            case "Показать популярное" -> sendEroTypesShowPage(chatId, Command.SHOW_POPULAR);
            case "Показать избранные фетиши" -> showFavoritePreferences(chatId, botUserId, PreferenceType.FETISH);
            case "Показать избранные прелюдии" -> showFavoritePreferences(chatId, botUserId, PreferenceType.FOREPLAY);
            case "Показать избранные места" -> showFavoritePreferences(chatId, botUserId, PreferenceType.PLACE);
            case "Показать избранные позы" -> showFavoritePreferences(chatId, botUserId, PreferenceType.POSE);
            case "Показать избранные игрушки" -> showFavoritePreferences(chatId, botUserId, PreferenceType.TOY);
            case "Показать популярные фетиши" -> showPopularPreferences(chatId, PreferenceType.FETISH);
            case "Показать популярные прелюдии" -> showPopularPreferences(chatId, PreferenceType.FOREPLAY);
            case "Показать популярные места" -> showPopularPreferences(chatId, PreferenceType.PLACE);
            case "Показать популярные позы" -> showPopularPreferences(chatId, PreferenceType.POSE);
            case "Показать популярные игрушки" -> showPopularPreferences(chatId, PreferenceType.TOY);
            case "Начать играть" -> showStartGameMenu(chatId);
            case "/Случайное из избранного" -> showRandomFavoritePreferences(chatId, Collections.singletonList(botUserId));
            case "/Случайное из популярного" -> showRandomPopularPreferences(chatId);
            case "/Случайное из пользовательского" -> showRandomPreferences(chatId);
            case "/Случайный сценарий" -> ShowRandomScenario(chatId);
            case "В начало" -> sendHomePage(BACK_HOME_TEXT, chatId, botUserId);
            default -> sendHomePage(UNKNOWN_COMMAND_TEXT, chatId, botUserId);
        }
    }
    private void showRandomFavoritePreferences(long chatId, List<Long> botUsersId) {
        List<Preference> preferences = new ArrayList<>();
        List<BotUser> botUsers = new ArrayList<>();
        for(long botUserId : botUsersId) {
            botUsers.add(databaseService.getBotUser(botUserId));
        }
        preferences.add(databaseService.getUserPreference(PreferenceType.FOREPLAY, botUsers));
        preferences.add(databaseService.getUserPreference(PreferenceType.PLACE, botUsers));
        preferences.add(databaseService.getUserPreference(PreferenceType.POSE, botUsers));
        preferences.add(databaseService.getUserPreference(PreferenceType.FETISH, botUsers));
        preferences.add(databaseService.getUserPreference(PreferenceType.TOY, botUsers));
        preferences.removeIf(Objects::isNull);
        if(preferences.isEmpty()) {
            sendMessage(chatId, EMPTY_LIST_TEXT, keyboardFactory.createKeyboard(Command.START_GAME));
            return;
        }
        showPreferences(chatId, preferences, Command.START_GAME);
    }
    private void showRandomPopularPreferences(long chatId) {
        List<Preference> preferences = new ArrayList<>();
        preferences.add(databaseService.getRandomModeratedPreference(PreferenceType.FOREPLAY));
        preferences.add(databaseService.getRandomModeratedPreference(PreferenceType.PLACE));
        preferences.add(databaseService.getRandomModeratedPreference(PreferenceType.POSE));
        preferences.add(databaseService.getRandomModeratedPreference(PreferenceType.FETISH));
        preferences.add(databaseService.getRandomModeratedPreference(PreferenceType.TOY));
        showPreferences(chatId, preferences, Command.START_GAME);
    }
    private void showRandomPreferences(long chatId) {
        List<Preference> preferences = new ArrayList<>();
        preferences.add(databaseService.getRandomPreference(PreferenceType.FOREPLAY));
        preferences.add(databaseService.getRandomPreference(PreferenceType.PLACE));
        preferences.add(databaseService.getRandomPreference(PreferenceType.POSE));
        preferences.add(databaseService.getRandomPreference(PreferenceType.FETISH));
        preferences.add(databaseService.getRandomPreference(PreferenceType.TOY));
        showPreferences(chatId, preferences, Command.START_GAME);
    }
    private void ShowRandomScenario(long chatId) {
        Scenario scenario = databaseService.getRandomScenario();
        sendMessage(chatId, "Показываю случайный сценарий" + "\n" + scenario.getDescription());
    }
    private void showStartGameMenu(long chatId) {
        sendMessage(chatId, START_GAME_TEXT, keyboardFactory.createKeyboard(Command.START_GAME));
    }
    private void sendPreferenceTypesAddPage(long chatId) {
        sendMessage(chatId, ADD_CHOOSE_TYPE_TEXT, keyboardFactory.createKeyboard(Command.ADD_CUSTOM));
    }
    private void sendAddCustomPreference(long chatId, long botUserId, PreferenceType preferenceType) {
        BotUser botUser = databaseService.getBotUser(botUserId);
        botUser.setAddCommandCheck(true);
        botUser.setCommand(Command.ADD_NAME);
        databaseService.saveBotUser(botUser);
        Preference preference = new Preference("", "", preferenceType, false);
        databaseService.savePreference(preference);
        databaseService.pinPreference(botUser, preference);
        sendMessage(chatId, "Введите название", keyboardFactory.createKeyboard(Command.ADD_NAME));
    }
    private void sendHomePage(String text, long chatId, long botUserId) {
        BotUser botUser = databaseService.getBotUser(botUserId);
        botUser.setAddCommandCheck(false);
        botUser.setCommand(Command.MAIN_MENU);
        databaseService.saveBotUser(botUser);
        sendMessage(chatId, text, keyboardFactory.createKeyboard(Command.MAIN_MENU));
    }
    private void showPopularPreferences(long chatId, PreferenceType preferenceType) {
        List<Preference> preferences = databaseService.getModeratedPreferences(preferenceType);
        if (preferences.isEmpty()) {
            sendMessage(chatId, EMPTY_LIST_TEXT, keyboardFactory.createKeyboard(Command.SHOW_POPULAR));
            return;
        }
        showPreferences(chatId, preferences, Command.ADD_POPULAR);
    }
    private void showFavoritePreferences (long chatId, long botUserId, PreferenceType preferenceType) {
        BotUser botUser = databaseService.getBotUser(botUserId);
        List<Preference> preferences = databaseService.getUserPreferences(botUser, preferenceType);
        if (preferences.isEmpty()) {
            sendMessage(chatId, EMPTY_LIST_TEXT, keyboardFactory.createKeyboard(Command.SHOW_MY));
            return;
        }
        showPreferences(chatId, preferences, Command.DELETE);
    }
    private void showPreferences (long chatId, List<Preference> preferences, Command command) {
        sendMessage(chatId, SHOW_PREFERENCES_START_TEXT);
        for(Preference preference : preferences) {
            StringBuilder sb = new StringBuilder();
            if(preference.getFileId() != null) {
                sendPhoto(chatId, preference.getFileId());
            }
            sb.append("Название: ")
                    .append(preference.getName())
                    .append("\nОписание: ")
                    .append(preference.getDescription());
            if(preference.getOzonUrl() != null) {
                sb.append("\nТовар на озон: ").append(preference.getOzonUrl());
            }
            if(preference.getPinkRabbitUrl() != null) {
                sb.append("\nТовар в pink rabbit: ").append(preference.getPinkRabbitUrl());
            }
            if(preference.getIntimShopUrl() != null) {
                sb.append("\nТовар в intim shop: ").append(preference.getIntimShopUrl());
            }
            sendMessage(chatId, sb.toString(), keyboardFactory.createKeyboard(command, preference));
        }
        sendMessage(chatId, SHOW_PREFERENCES_END_TEXT);
    }
    private void sendEroTypesShowPage(long chatId, Command command) {
        sendMessage(chatId, SHOW_CHOOSE_TYPE_TEXT, keyboardFactory.createKeyboard(command));
    }
    private void sendShowPage(long chatId) {
        sendMessage(chatId, SHOW_TEXT, keyboardFactory.createKeyboard(Command.SHOW));
    }
    public void sendMessage (long chatId, String text, ReplyKeyboard keyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage (long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendPhoto (long chatId, String imageId) {
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile();
        inputFile.setMedia(imageId);
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(chatId);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
