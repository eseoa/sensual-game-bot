package com.github.eseoa.sensualgamebot.keyboard;

import com.github.eseoa.sensualgamebot.command.Command;
import com.github.eseoa.sensualgamebot.model.Preference;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {

    public ReplyKeyboard createKeyboard (Command command) {
        ReplyKeyboard replyKeyboard;
        switch (command) {
            case ADD_CUSTOM -> replyKeyboard = createKeyboardToAddCustom();
            case ADD_NAME -> replyKeyboard = createKeyboardToBackHome();
            case ADD_DESCRIPTION -> replyKeyboard = createKeyboardToBackHome();
            case ADD_PHOTO -> replyKeyboard = createKeyboardToBackHome();
            case SHOW -> replyKeyboard = createKeyboardToShow();
            case SHOW_MY -> replyKeyboard = createKeyboardToShowMy();
            case SHOW_POPULAR -> replyKeyboard = createKeyboardToShowPopular();
            case START_GAME -> replyKeyboard = createKeyboardToStartGame();
            case MAIN_MENU -> replyKeyboard = createKeyboardToMainMenu();
            default -> replyKeyboard = createKeyboardToMainMenu();
        }
        return replyKeyboard;
    }

    private ReplyKeyboard createKeyboardToStartGame() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("/Случайное из избранного")
                .addRow("/Случайное из популярного")
                .addRow("/Случайное из пользовательского")
                .addRow("/Случайные сценарий")
                .addRow("В начало");
        return markupBuilder.buildReplyMarkup();
    }

    public ReplyKeyboard createKeyboard (Command command, Preference preference) {
        ReplyKeyboard replyKeyboard;
        switch (command) {
            case ADD_POPULAR -> replyKeyboard = createInlineKeyboard(command, preference);
            case DELETE -> replyKeyboard = createInlineKeyboard(command, preference);
            case START_GAME -> replyKeyboard = createKeyboardToStartGame();
            default -> replyKeyboard = createKeyboardToMainMenu();
        }
        return replyKeyboard;
    }
    private ReplyKeyboard createInlineKeyboard (Command command, Preference preference) {
        String preferenceName = preference.getName();
        long preferenceId = preference.getId();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        switch (command) {
            case ADD_POPULAR -> inlineKeyboardButton.setText("Добавить " + preferenceName);
            case DELETE -> inlineKeyboardButton.setText("Удалить " + preferenceName);
        }
        inlineKeyboardButton.setCallbackData(command + "=" + preferenceId);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton);
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        keyboardMarkup.setKeyboard(rowList);
        return keyboardMarkup;
    }
    private ReplyKeyboard createKeyboardToAddCustom() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("Добавить свою позу")
                .addRow("Добавить свою прелюдию")
                .addRow("Добавить свое место")
                .addRow("Добавить свой фетиш")
                .addRow("Добавить свою игрушку")
                .addRow("В начало");
        return markupBuilder.buildReplyMarkup();
    }
    private ReplyKeyboard createKeyboardToBackHome() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("В начало");
        return markupBuilder.buildReplyMarkup();
    }
    private ReplyKeyboard createKeyboardToShowMy() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("Показать избранные позы")
                .addRow("Показать избранные прелюдии")
                .addRow("Показать избранные места")
                .addRow("Показать избранные фетиши")
                .addRow("Показать избранные игрушки")
                .addRow("В начало");
        return markupBuilder.buildReplyMarkup();
    }
    private ReplyKeyboard createKeyboardToShowPopular() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("Показать популярные позы")
                .addRow("Показать популярные прелюдии")
                .addRow("Показать популярные места")
                .addRow("Показать популярные фетиши")
                .addRow("Показать популярные игрушки")
                .addRow("В начало");
        return markupBuilder.buildReplyMarkup();
    }
    private ReplyKeyboard createKeyboardToShow() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("Показать избранное")
                .addRow("Показать популярное")
                .addRow("В начало");
        return markupBuilder.buildReplyMarkup();
    }
    private ReplyKeyboardMarkup createKeyboardToMainMenu() {
        ReplyMarkupBuilder markupBuilder = new ReplyMarkupBuilder();
        markupBuilder.addRow("Добавить свое")
                .addRow("Показать")
                .addRow("Начать играть");
        return markupBuilder.buildReplyMarkup();
    }
}
