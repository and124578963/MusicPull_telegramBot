package ru.home.mywizard_bot.cache;

import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.repository.model.MusicData;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.model.UserProfileData;


public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

    String getUserSelectedPull(long userId);

    void setUserSelectedPull(long userId, String pullId);

    UserProfileData getUserProfileData(long userId);

    void saveUserProfileData(long userId, UserProfileData userProfileData);

    MusicPullsData getMusicPullData(String  pullId);

    void setUsersSelectedMusic(long userId, MusicData music);

    MusicData getUsersSelectedMusic(long userId);

}
