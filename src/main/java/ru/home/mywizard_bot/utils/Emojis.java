package ru.home.mywizard_bot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Emojis {
    SPARKLES(EmojiParser.parseToUnicode(":sparkles:")),
    SCROLL(EmojiParser.parseToUnicode(":scroll:")),
    MAGE(EmojiParser.parseToUnicode(":mage:")),
    DEL(EmojiParser.parseToUnicode(":x:")),
    LIKE(EmojiParser.parseToUnicode(":heart:")),

    ;



    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
