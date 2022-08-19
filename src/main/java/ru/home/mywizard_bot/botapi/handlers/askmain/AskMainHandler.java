package ru.home.mywizard_bot.botapi.handlers.askmain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.util.ArrayList;
import java.util.List;


/**
 * Спрашивает создать или подключиться к имеющему опросу.
 */

@Slf4j
@Component
public class AskMainHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;

    public AskMainHandler(ReplyMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START_PAGE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.askMain");
        replyToUser.setReplyMarkup(getInlineMessageButtons());

        return replyToUser;
    }



    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonCreatePull = new InlineKeyboardButton();
        buttonCreatePull.setText("Создать голосование");
        InlineKeyboardButton buttonSelectPull = new InlineKeyboardButton();
        buttonSelectPull.setText("Подключиться к голосованию");
        InlineKeyboardButton buttonListMyPull = new InlineKeyboardButton();
        buttonListMyPull.setText("Мои голосования");


        //Every button must have callBackData, or else not work !
        buttonCreatePull.setCallbackData("buttonCreatePull");
        buttonSelectPull.setCallbackData("buttonSelectPull");
        buttonListMyPull.setCallbackData("buttonListMyPull");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonCreatePull);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonSelectPull);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonListMyPull);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}



