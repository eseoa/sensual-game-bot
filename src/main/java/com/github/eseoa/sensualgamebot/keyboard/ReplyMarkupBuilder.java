package com.github.eseoa.sensualgamebot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyMarkupBuilder {
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow keyboardRow = null;

    public ReplyMarkupBuilder addRow() {
        addRowToList();
        keyboardRow = new KeyboardRow();
        return this;
    }

    public ReplyMarkupBuilder addRow (String... buttons) {
        addRowToList();
        keyboardRow = new KeyboardRow();
        for(String button : buttons) {
            keyboardRow.add(button);
        }
        return this;
    }

    public ReplyMarkupBuilder addButton(String text) {
        keyboardRow.add(text);
        return this;
    }

    public ReplyKeyboardMarkup buildReplyMarkup() {
        if(keyboardRow != null && !keyboardRow.isEmpty() && keyboardRows.size() == 0) {
            keyboardRows.add(keyboardRow);
        }
        if(keyboardRow != null && !keyboardRow.isEmpty() && !keyboardRows.get(keyboardRows.size() - 1).equals(keyboardRow)) {
            keyboardRows.add(keyboardRow);
        }
         replyKeyboardMarkup.setKeyboard(keyboardRows);
         return replyKeyboardMarkup;
    }

    private void addRowToList () {
        if(keyboardRow != null) {
            keyboardRows.add(keyboardRow);
        }
    }
}
