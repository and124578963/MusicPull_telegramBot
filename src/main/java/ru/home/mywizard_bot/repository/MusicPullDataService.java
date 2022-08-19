package ru.home.mywizard_bot.repository;

import org.springframework.stereotype.Service;
import ru.home.mywizard_bot.repository.model.MusicPullsData;

import java.util.List;

/**
 * Сохраняет, удаляет, ищет анкеты пользователя.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class MusicPullDataService {

    private MusicPullMongoRepository profileMongoRepository;

    public MusicPullDataService(MusicPullMongoRepository profileMongoRepository) {
        this.profileMongoRepository = profileMongoRepository;
    }

    public void saveMusicPullData(MusicPullsData musicPullsData) {
        profileMongoRepository.save(musicPullsData);
    }

    public List<MusicPullsData> getMusicPullsByChatId(long ownerChatId) {
        return profileMongoRepository.findByOwnerChatId(ownerChatId);
    }

    public MusicPullsData getMusicPullsByPullId(String pullId) {
        return profileMongoRepository.findByPullId(pullId);
    }

    public void deleteMusicPullsByPullId(String pullId) {
        profileMongoRepository.deleteByPullId(pullId);
    }

}
