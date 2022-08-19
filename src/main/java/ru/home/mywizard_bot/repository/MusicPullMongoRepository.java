package ru.home.mywizard_bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.home.mywizard_bot.repository.model.MusicPullsData;

import java.util.List;


@Repository
public interface MusicPullMongoRepository extends MongoRepository<MusicPullsData, String> {
    List<MusicPullsData> findByOwnerChatId(long ownerChatId);
    MusicPullsData findByPullId(String pullId);
    void deleteByPullId(String pullId);
}
