package ru.home.mywizard_bot.repository.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "userProfileData")
public class UserProfileData implements Serializable {

    String id;
    @Id
    long chatId;
    @DBRef
    HashSet<MusicPullsData> musicPulls;

    @Override
    public String toString() {
        return String.format("ID: %s%n ChatID %d%n SelectedPull: %s%n", getId(), getChatId());
    }
}
