package ru.home.mywizard_bot.botapi.handlers.pull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.MusicPullDataService;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс опроса
 */

@Slf4j
@Component
public class MusicPullHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private UserDataCache userDataCache;
    private MusicPullDataService musicPullDataService;


    public MusicPullHandler(ReplyMessagesService messagesService, UserDataCache userDataCache, MusicPullDataService musicPullDataService) {
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.musicPullDataService = musicPullDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.MUSIC_PULL;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();
        String pullId = userDataCache.getUserSelectedPull(chatId);
        MusicPullsData musicPullData = musicPullDataService.getMusicPullsByPullId(pullId);



        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.startPull", musicPullData.getPullName(), musicPullData.getPullId());
        replyToUser.setReplyMarkup(getInlinePullButtons());
        replyToUser.enableHtml(true);

        return replyToUser;
    }

    private InlineKeyboardMarkup getInlinePullButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonStartPull = new InlineKeyboardButton();
        buttonStartPull.setText("Голосовать");
        InlineKeyboardButton buttonAddMusic = new InlineKeyboardButton();
        buttonAddMusic.setText("Добавить песню");

        InlineKeyboardButton buttonAddSuperMusic = new InlineKeyboardButton();
        buttonAddSuperMusic.setText("Добавить супер песню");
        InlineKeyboardButton buttonGetResult = new InlineKeyboardButton();
        buttonGetResult.setText("Получить результат");

        InlineKeyboardButton buttonMainPage = new InlineKeyboardButton();
        buttonMainPage.setText("На главную");


        buttonStartPull.setCallbackData("buttonStartPull");
        buttonAddMusic.setCallbackData("buttonAddMusic");
        buttonAddSuperMusic.setCallbackData("buttonAddSuperMusic");
        buttonGetResult.setCallbackData("buttonGetResult");
        buttonMainPage.setCallbackData("buttonMainPage");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonStartPull);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonAddMusic);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonAddSuperMusic);

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(buttonGetResult);

        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        keyboardButtonsRow5.add(buttonMainPage);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}



