package ru.home.mywizard_bot.botapi;

/**Возможные состояния бота
 */

public enum BotState {
    ASK_DESTINY,
    ASK_CREATE_PULL_NAME,
    ASK_AGE,
    ASK_GENDER,
    ASK_COLOR,
    ASK_NUMBER,
    ASK_MOVIE,
    ASK_SONG,
    FILLING_PROFILE,
    SHOW_USER_PROFILE,
    SHOW_MAIN_MENU,
    SHOW_HELP_MENU,
    START_PAGE,
    LIST_MY_PULL,
    PULL_CREATED,
    MUSIC_PULL,
    PULLING,
    ADD_MUSIC,
    ENTER_PULL_ID,
    SELECTING_PULL,
    ADD_SUPER_MUSIC, GET_RESULT;
}
