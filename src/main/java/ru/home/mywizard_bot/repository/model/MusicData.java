package ru.home.mywizard_bot.repository.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "MusicData")
public class MusicData implements Serializable {
    @Id
    String musicId;
    long ownerChatId;
    int votes;
    String name;
    String path;
    String ownerPullId;
    String type;
    @DBRef
    List<UserProfileData> votedUsers;

    @Override
    public String toString() {
        return String.format("musicId: %s%n" +
                "name %s%n" +
                        "votes %d%n" +
                "path: %s%n" +
                        "ownerChatId: %d%n" +
                        "ownerPullId: %s%n" +
                        "type: %s%n",
                getMusicId(), getName(), getVotes() , getPath(), getOwnerChatId() , getOwnerPullId(), getType());
    }
}
