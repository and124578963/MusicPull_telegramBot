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
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс опроса
 */

@Slf4j
@Component
public class GetResultHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private UserDataCache userDataCache;
    private MusicPullDataService musicPullDataService;
    private MusicsDataService musicsDataService;


    public GetResultHandler(ReplyMessagesService messagesService, UserDataCache userDataCache, MusicPullDataService musicPullDataService,
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
        return BotState.GET_RESULT;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();
//        String musicName = inputMsg.getText();
        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.error");
        String pullId = userDataCache.getUserSelectedPull(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        if (botState == BotState.GET_RESULT) {
           MusicPullsData musicPullsData = musicPullDataService.getMusicPullsByPullId(pullId);
           long ownerChatId = musicPullsData.getOwnerChatId();
           List<MusicData> listMusic = musicsDataService.getMusicsByPullId(pullId);

           if(ownerChatId != chatId){
               listMusic.removeIf(music -> music.getType().equals("Super"));
           }

           StringBuilder reply = new StringBuilder();
           String name;
           String vote;
           for (MusicData music:listMusic){
               name = music.getName();
               vote = Integer.toString(music.getVotes());
               if (music.getType().equals("Super")){
                   reply.append(name).append(" : ").append("super music").append("\n");
               } else {
                   reply.append(name).append(" : ").append(vote).append("\n");
               }
           }
           replyToUser = new SendMessage(Long.toString(chatId), reply.toString());
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



