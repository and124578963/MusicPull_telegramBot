package ru.home.mywizard_bot.botapi.handlers.fillingprofile;

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
import ru.home.mywizard_bot.repository.model.UserProfileData;
import ru.home.mywizard_bot.repository.MusicPullDataService;
import ru.home.mywizard_bot.service.PredictionService;
import ru.home.mywizard_bot.service.ReplyMessagesService;
import ru.home.mywizard_bot.repository.UsersProfileDataService;
import ru.home.mywizard_bot.utils.Emojis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


/**
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private PredictionService predictionService;
    private UsersProfileDataService profileDataService;
    private MusicPullDataService musicPullDataService;


    public FillingProfileHandler(UserDataCache userDataCache, ReplyMessagesService messagesService,
                                 PredictionService predictionService, UsersProfileDataService profileDataService,
                                 MusicPullDataService musicPullDataService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.predictionService = predictionService;
        this.profileDataService = profileDataService;
        this.musicPullDataService = musicPullDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_CREATE_PULL_NAME);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        long userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserProfileData profileData = profileDataService.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        MusicPullsData musicPullData = userDataCache.getMusicPullData("Create new");
        SendMessage replyToUser = null;


        if (botState.equals(BotState.ASK_CREATE_PULL_NAME)) {
//          ДОБАВИТЬ ФУНКЦИЮ СОЗДАНИЯ ОПРОСА В БАЗЕ
            String pullName = usersAnswer;
            String uniqueID = UUID.randomUUID().toString().substring(24);

            musicPullData.setPullId(uniqueID);
            musicPullData.setPullName(pullName);
            musicPullData.setOwnerChatId(chatId);
            musicPullDataService.saveMusicPullData(musicPullData);

            HashSet<MusicPullsData> newListPulls =  profileData.getMusicPulls();
            if (newListPulls == null){
                newListPulls = new HashSet<>();
            }
            newListPulls.add(musicPullData);
            profileData.setMusicPulls(newListPulls);
            profileData.setChatId(chatId);
            profileDataService.saveUserProfileData(profileData);

            userDataCache.setUserSelectedPull(userId, uniqueID);
            userDataCache.setUsersCurrentBotState(userId, BotState.MUSIC_PULL);

            String profileFilledMessage = messagesService.getReplyText("reply.pullCreated",pullName,
                    Emojis.SPARKLES, uniqueID);
//            String predictionMessage = predictionService.getPrediction();
//             Перенос срок через %n
//            replyToUser = new SendMessage(Long.toString(chatId), String.format("%s%n%n%s", profileFilledMessage, Emojis.SCROLL));
            replyToUser = new SendMessage(Long.toString(chatId), profileFilledMessage);

            replyToUser.setReplyMarkup(getInlinePullButtons());

            replyToUser.setParseMode("HTML");

        }

        userDataCache.saveUserProfileData(userId, profileData);

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



