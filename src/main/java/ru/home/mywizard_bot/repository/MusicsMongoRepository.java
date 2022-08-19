package ru.home.mywizard_bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.home.mywizard_bot.repository.model.MusicData;

import java.util.List;


@Repository
public interface MusicsMongoRepository extends MongoRepository<MusicData, String> {
    List<MusicData> findByOwnerPullId(String ownerPullId);
    MusicData findByMusicId(String musicId);
    void deleteByMusicId(String musicId);
}
