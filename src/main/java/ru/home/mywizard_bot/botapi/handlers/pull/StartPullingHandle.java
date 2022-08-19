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
import ru.home.mywizard_bot.utils.Emojis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Класс опроса
 */

@Slf4j
@Component
public class StartPullingHandle implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private UserDataCache userDataCache;
    private MusicPullDataService musicPullDataService;

    private MusicsDataService musicsDataService;


    public StartPullingHandle(ReplyMessagesService messagesService, UserDataCache userDataCache, MusicPullDataService musicPullDataService,
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
        return BotState.PULLING;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        boolean ckeck_music = false;
        long chatId = inputMsg.getChatId();
        String pullId = userDataCache.getUserSelectedPull(chatId);
        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.noMusic");
        List<MusicData> listMusic = musicsDataService.getMusicsByPullId(pullId);
        List<MusicData> notVotedListMusic = new ArrayList<>();

        for (MusicData music:listMusic){

            List<UserProfileData> users =  music.getVotedUsers();
            boolean check = true;
            for (UserProfileData user: users){
                if (user.getChatId() == chatId) {
                    check = false;
                    break;
                }
            }
            if (check){
                notVotedListMusic.add(music);
            }

        }
        if (notVotedListMusic.size() != 0) {
            ckeck_music = true;
            Random generator = new Random();
            int randomIndex = generator.nextInt(notVotedListMusic.size());
            MusicData selectedMusic = notVotedListMusic.get(randomIndex);

            userDataCache.setUsersSelectedMusic(chatId, selectedMusic);

            replyToUser = messagesService.getReplyMessage(chatId, "reply.vote", selectedMusic.getName());

        }
        replyToUser.setReplyMarkup(getInlineMessageButtons(ckeck_music));
        replyToUser.enableHtml(true);
        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(boolean check_music) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonStartPull = new InlineKeyboardButton();
        buttonStartPull.setText("Вернуться назад");
        buttonStartPull.setCallbackData("buttonBackToPull");

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonStartPull);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);

        if (check_music) {
            InlineKeyboardButton buttonLike = new InlineKeyboardButton();
            buttonLike.setText(String.valueOf(Emojis.LIKE));
            buttonLike.setCallbackData("buttonLike");

            InlineKeyboardButton buttonPass = new InlineKeyboardButton();
            buttonPass.setText("Пропустить");
            buttonPass.setCallbackData("buttonPass");

            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(buttonLike);
            keyboardButtonsRow1.add(buttonPass);

            rowList.add(keyboardButtonsRow1);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


}



