package ru.home.mywizard_bot.cache;

import org.springframework.stereotype.Component;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.repository.model.MusicData;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.model.UserProfileData;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * usersProfileData: user_id  and user's profile data.
 */

@Component
public class UserDataCache implements DataCache {
    private Map<Long, BotState> usersBotStates = new HashMap<>();
    private Map<Long, String> usersSelectedPull = new HashMap<>();
    private Map<Long, MusicData> userSelectedMusic = new HashMap<>();
    private Map<Long, UserProfileData> usersProfileData = new HashMap<>();
    private Map<String, MusicPullsData> musicPullsData = new HashMap<>();


    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START_PAGE;
        }

        return botState;
    }

    @Override
    public String getUserSelectedPull(long userId) {
        return usersSelectedPull.get(userId);
    }

    @Override
    public void setUserSelectedPull(long userId, String pullId) {
        usersSelectedPull.put(userId, pullId);
    }

    @Override
    public UserProfileData getUserProfileData(long userId) {
        UserProfileData userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
        }
        return userProfileData;
    }

    @Override
    public void saveUserProfileData(long userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }

    @Override
    public MusicPullsData getMusicPullData(String pullId) {
        MusicPullsData musicPullData = musicPullsData.get(pullId);
        if (musicPullData == null) {
            musicPullData = new MusicPullsData();
        }
        return musicPullData;
    }

    @Override
    public void setUsersSelectedMusic(long userId, MusicData music) {
        userSelectedMusic.put(userId, music);
    }

    @Override
    public MusicData getUsersSelectedMusic(long userId) {
        return userSelectedMusic.get(userId);
    }

}
