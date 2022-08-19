package ru.home.mywizard_bot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines message handlers for each state.
 */
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        System.out.println(currentState);
        return messageHandlers.get(switchState(currentState));
    }

    private BotState switchState(BotState currentState) {
        switch (currentState) {
            case ASK_CREATE_PULL_NAME:
            case FILLING_PROFILE:
            case PULL_CREATED:
                return BotState.FILLING_PROFILE;

            case ADD_SUPER_MUSIC:
                return BotState.ADD_MUSIC;

            default:
                return currentState;
        }
    }


}





