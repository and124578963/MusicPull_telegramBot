package ru.home.mywizard_bot.botapi.handlers.pull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.repository.UsersProfileDataService;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.MusicPullDataService;
import ru.home.mywizard_bot.service.ReplyMessagesService;
import ru.home.mywizard_bot.utils.Emojis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Класс опроса
 */

@Slf4j
@Component
public class MyPullHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private UsersProfileDataService profileDataService;
    private MusicPullDataService musicPullDataService;


    public MyPullHandler(ReplyMessagesService messagesService, MusicPullDataService musicPullDataService,
                         UsersProfileDataService profileDataService) {
        this.messagesService = messagesService;
        this.musicPullDataService = musicPullDataService;
        this.profileDataService= profileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LIST_MY_PULL;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();
        HashSet<MusicPullsData>  hashPulls = profileDataService.getUserProfileData(chatId).getMusicPulls();
        List<MusicPullsData> listPulls = new ArrayList<>(hashPulls);
        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.selectYoursPull");
        replyToUser.setReplyMarkup(getInlineMessageButtons(listPulls));

        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(List<MusicPullsData> listPulls) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (MusicPullsData pull : listPulls){
            InlineKeyboardButton buttonOfListPull = new InlineKeyboardButton();
            buttonOfListPull.setText(pull.getPullName());
            buttonOfListPull.setCallbackData(pull.getPullId());

            InlineKeyboardButton delButtonOfListPull = new InlineKeyboardButton();
            delButtonOfListPull.setText(String.valueOf(Emojis.DEL));
            delButtonOfListPull.setCallbackData("del_"+ pull.getPullId());


            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(buttonOfListPull);
            keyboardButtonsRow1.add(delButtonOfListPull);
            rowList.add(keyboardButtonsRow1);
        }

        InlineKeyboardButton buttonMainPage = new InlineKeyboardButton();
        buttonMainPage.setText("На главную");
        buttonMainPage.setCallbackData("buttonMainPage");
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(buttonMainPage);
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);


        return inlineKeyboardMarkup;
    }


}



