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
@Document(collection = "MusicPullData")
public class MusicPullsData implements Serializable {
    @Id
    String pullId;
    String pullName;
    long ownerChatId;

    @DBRef
    private List<UserProfileData> users;



    @Override
    public String toString() {
        return String.format("PullId: %s%n OwnerChatId %d%n PullName: %s%n", getPullId(), getOwnerChatId(), getPullName() );
    }
}
