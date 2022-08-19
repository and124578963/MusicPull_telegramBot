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
import ru.home.mywizard_bot.repository.UsersProfileDataService;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.MusicPullDataService;
import ru.home.mywizard_bot.repository.model.UserProfileData;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Класс опроса
 */

@Slf4j
@Component
public class ConnectToOtherPullHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private MusicPullDataService musicPullDataService;
    private UserDataCache userDataCache;
    private UsersProfileDataService profileDataService;
    private MusicPullHandler musicPullHandler;


    public ConnectToOtherPullHandler(ReplyMessagesService messagesService, MusicPullDataService musicPullDataService,
                                     UserDataCache userDataCache,MusicPullHandler musicPullHandler, UsersProfileDataService profileDataService) {
        this.messagesService = messagesService;
        this.musicPullDataService = musicPullDataService;
        this.userDataCache = userDataCache;
        this.musicPullHandler = musicPullHandler;
        this.profileDataService = profileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ENTER_PULL_ID;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        SendMessage replyToUser = new SendMessage();
        long chatId = inputMsg.getChatId();
        String usersAnswer = inputMsg.getText();


        MusicPullsData musicPullData = musicPullDataService.getMusicPullsByPullId(usersAnswer);
        if (musicPullData != null){

            userDataCache.setUsersCurrentBotState(chatId, BotState.MUSIC_PULL);
            userDataCache.setUserSelectedPull(chatId, usersAnswer);
            UserProfileData user =  profileDataService.getUserProfileData(chatId);

            HashSet<MusicPullsData> newListPulls =  user.getMusicPulls();
            if (newListPulls == null){
                newListPulls = new HashSet<>();
            }
            newListPulls.add(musicPullData);
            user.setMusicPulls(newListPulls);
            user.setChatId(chatId);

            profileDataService.saveUserProfileData(user);

            System.out.println(newListPulls);

            replyToUser = musicPullHandler.handle(inputMsg);



        } else {

            replyToUser = messagesService.getReplyMessage(chatId, "reply.errorSecretCode");
            replyToUser.setReplyMarkup(getInlinePullButtons());
        }



        return replyToUser;
    }

    private InlineKeyboardMarkup getInlinePullButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonMainPage = new InlineKeyboardButton();
        buttonMainPage.setText("На главную");
        buttonMainPage.setCallbackData("buttonMainPage");

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonMainPage);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}



