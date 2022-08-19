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
import ru.home.mywizard_bot.repository.MusicPullDataService;
import ru.home.mywizard_bot.repository.MusicsDataService;
import ru.home.mywizard_bot.repository.model.MusicData;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.model.UserProfileData;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс опроса
 */

@Slf4j
@Component
public class AddMusicHandled implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private UserDataCache userDataCache;
    private MusicPullDataService musicPullDataService;
    private MusicsDataService musicsDataService;


    public AddMusicHandled(ReplyMessagesService messagesService, UserDataCache userDataCache, MusicPullDataService musicPullDataService,
                           MusicsDataService musicsDataService) {
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.musicPullDataService = musicPullDataService;
        this.musicsDataService = musicsDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ADD_MUSIC;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();
        String musicName = inputMsg.getText();
        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.error");
        String pullId = userDataCache.getUserSelectedPull(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);


        if (botState == BotState.ADD_MUSIC) {
            musicsDataService.addNewMusic(chatId, musicName, "Путь до файла", pullId, "Normal");
            replyToUser = messagesService.getReplyMessage(chatId, "reply.musicAdded", musicName);
        } else if (botState == BotState.ADD_SUPER_MUSIC) {
            boolean _new = musicsDataService.addSuperMusic(chatId, musicName, "Путь до файла", pullId, "Super");
            if (_new){
                replyToUser = messagesService.getReplyMessage(chatId, "reply.superMusicAdded", musicName);
            }  else {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.superMusicChanged", musicName);

            }
        }

        replyToUser.setReplyMarkup(getInlineMessageButtons());
        replyToUser.enableHtml(true);

        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonStartPull = new InlineKeyboardButton();
        buttonStartPull.setText("Вернуться в голосование");
        buttonStartPull.setCallbackData("buttonBackToPull");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonStartPull);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


}



