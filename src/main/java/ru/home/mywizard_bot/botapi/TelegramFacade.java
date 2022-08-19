package ru.home.mywizard_bot.botapi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.mywizard_bot.MyWizardTelegramBot;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.repository.MusicsDataService;
import ru.home.mywizard_bot.repository.UsersProfileDataService;
import ru.home.mywizard_bot.repository.model.MusicData;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.model.UserProfileData;
import ru.home.mywizard_bot.repository.MusicPullDataService;
import ru.home.mywizard_bot.service.MainMenuService;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.util.HashSet;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;
    private MyWizardTelegramBot myWizardBot;
    private ReplyMessagesService messagesService;
    private MusicPullDataService musicPullDataService;
    private UsersProfileDataService usersProfileDataService;

    private MusicsDataService musicsDataService;


    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy MyWizardTelegramBot myWizardBot, ReplyMessagesService messagesService,
                          MusicPullDataService musicPullDataService, UsersProfileDataService usersProfileDataService,
                          MusicsDataService musicsDataService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myWizardBot = myWizardBot;
        this.messagesService = messagesService;
        this.musicPullDataService = musicPullDataService;
        this.usersProfileDataService = usersProfileDataService;
        this.musicsDataService = musicsDataService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.START_PAGE;
                myWizardBot.sendPhoto(chatId, messagesService.getReplyText("reply.hello"), messagesService.getReplyText("reply.mainImgPath"));
                break;
//            case "Получить предсказание":
//                botState = BotState.FILLING_PROFILE;
//                break;
//            case "Моя анкета":
//                botState = BotState.SHOW_USER_PROFILE;
//                break;
//            case "Скачать анкету":
//                myWizardBot.sendDocument(chatId, "Ваша анкета", getUsersProfile(userId));
//                botState = BotState.SHOW_USER_PROFILE;
//                break;
//            case "Помощь":
//                botState = BotState.SHOW_HELP_MENU;
//                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();
        final BotState currentState = userDataCache.getUsersCurrentBotState(userId);

//        Вызывает главное меню
//        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");
        BotApiMethod<?> callBackAnswer = new SendMessage();

        //From Destiny choose buttons
        if (buttonQuery.getData().equals("buttonYes")) {
            callBackAnswer = new SendMessage(Long.toString(chatId), "Как тебя зовут ?");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_AGE);
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайся, когда будешь готов", false, buttonQuery);
        } else if (buttonQuery.getData().equals("buttonIwillThink")) {
            callBackAnswer = sendAnswerCallbackQuery("Данная кнопка не поддерживается", true, buttonQuery);
        }

        //From AskMain choose buttons
        else if (buttonQuery.getData().equals("buttonCreatePull")) {
            callBackAnswer = new SendMessage(Long.toString(chatId), messagesService.getReplyText("reply.askCreatePullName"));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CREATE_PULL_NAME);
        } else if (buttonQuery.getData().equals("buttonSelectPull")) {
            callBackAnswer = new SendMessage(Long.toString(chatId), messagesService.getReplyText("reply.requestPullId"));
            userDataCache.setUsersCurrentBotState(userId, BotState.ENTER_PULL_ID);
        } else if (buttonQuery.getData().equals("buttonListMyPull")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.SELECTING_PULL);
            callBackAnswer = botStateContext.processInputMessage(BotState.LIST_MY_PULL, buttonQuery.getMessage());

        }

        //From Filling New Pull to Music pull choose buttons
        else if (buttonQuery.getData().equals("buttonStartPull")) {
            callBackAnswer = new SendMessage(Long.toString(chatId), messagesService.getReplyText("reply.pulling"));
            try {
                myWizardBot.execute(callBackAnswer);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            userDataCache.setUsersCurrentBotState(userId, BotState.PULLING);
            callBackAnswer = botStateContext.processInputMessage(BotState.PULLING, buttonQuery.getMessage());
        } else if (buttonQuery.getData().equals("buttonAddMusic")) {
            callBackAnswer = new SendMessage(Long.toString(chatId), messagesService.getReplyText("reply.addMusic"));
            userDataCache.setUsersCurrentBotState(userId, BotState.ADD_MUSIC);
        } else if (buttonQuery.getData().equals("buttonMainPage")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.START_PAGE);
            callBackAnswer = botStateContext.processInputMessage(BotState.START_PAGE, buttonQuery.getMessage());
        } else if (buttonQuery.getData().equals("buttonBackToPull")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.MUSIC_PULL);
            callBackAnswer = botStateContext.processInputMessage(BotState.MUSIC_PULL, buttonQuery.getMessage());
        } else if (buttonQuery.getData().equals("buttonLike")) {
            UserProfileData user = usersProfileDataService.getUserProfileData(chatId);
            MusicData music = userDataCache.getUsersSelectedMusic(chatId);
            musicsDataService.likeMusic(music, user);
            userDataCache.setUsersCurrentBotState(userId, BotState.PULLING);
            callBackAnswer = botStateContext.processInputMessage(BotState.PULLING, buttonQuery.getMessage());
        } else if (buttonQuery.getData().equals("buttonPass")) {
            UserProfileData user = usersProfileDataService.getUserProfileData(chatId);
            MusicData music = userDataCache.getUsersSelectedMusic(chatId);
            musicsDataService.passMusic(music, user);
            userDataCache.setUsersCurrentBotState(userId, BotState.PULLING);
            callBackAnswer = botStateContext.processInputMessage(BotState.PULLING, buttonQuery.getMessage());

        } else if (buttonQuery.getData().equals("buttonAddSuperMusic")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.ADD_SUPER_MUSIC);
            callBackAnswer = new SendMessage(Long.toString(chatId), messagesService.getReplyText("reply.addSuperMusic"));

        } else if (buttonQuery.getData().equals("buttonGetResult")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.GET_RESULT);
            callBackAnswer = botStateContext.processInputMessage(BotState.GET_RESULT, buttonQuery.getMessage());

//        //From Gender choose buttons
//        else if (buttonQuery.getData().equals("buttonMan")) {
//            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
//
//            userDataCache.saveUserProfileData(userId, userProfileData);
//            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COLOR);
//            callBackAnswer = new SendMessage(Long.toString(chatId), "Твоя любимая цифра");
//        } else if (buttonQuery.getData().equals("buttonWoman")) {
//            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
////            userProfileData.setGender("Ж");
//            userDataCache.saveUserProfileData(userId, userProfileData);
//            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COLOR);
//            callBackAnswer = new SendMessage(Long.toString(chatId), "Твоя любимая цифра");

        }else if(currentState.equals(BotState.SELECTING_PULL)){
           String recive = buttonQuery.getData();
           if (recive.startsWith("del_")){
               MusicPullsData musicPullsData = musicPullDataService.getMusicPullsByPullId(recive.substring(4));
//               if(musicPullsData.getOwnerChatId() == chatId){
//                   musicPullDataService.deleteMusicPullsByPullId(recive.substring(4));
//               }
               UserProfileData user = usersProfileDataService.getUserProfileData(chatId);
               HashSet<MusicPullsData> hashSet = user.getMusicPulls();
               hashSet.remove(musicPullsData);
               user.setMusicPulls(hashSet);
               usersProfileDataService.saveUserProfileData(user);

               callBackAnswer = botStateContext.processInputMessage(BotState.LIST_MY_PULL, buttonQuery.getMessage());

           } else if (! buttonQuery.getData().equals("buttonListMyPull")) {
               userDataCache.setUserSelectedPull(userId ,buttonQuery.getData());
               callBackAnswer = botStateContext.processInputMessage(BotState.MUSIC_PULL, buttonQuery.getMessage());

           }

        }else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        }


        return callBackAnswer;


    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

    @SneakyThrows
    public InputFile getUsersProfile(long userId) {
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);

        InputFile profileFile = new InputFile();
        profileFile.setMedia("classpath:static/docs/users_profile.txt");

//        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
//             BufferedWriter bw = new BufferedWriter(fw)) {
//            bw.write(userProfileData.toString());
//        }


        return profileFile;

    }


}
