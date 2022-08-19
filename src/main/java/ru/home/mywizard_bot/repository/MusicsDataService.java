package ru.home.mywizard_bot.repository;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.stereotype.Service;
import ru.home.mywizard_bot.repository.model.MusicData;
import ru.home.mywizard_bot.repository.model.MusicPullsData;
import ru.home.mywizard_bot.repository.model.UserProfileData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сохраняет, удаляет, ищет анкеты пользователя.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class MusicsDataService {

    private MusicsMongoRepository musicMongoRepository;


    public MusicsDataService(MusicsMongoRepository musicMongoRepository) {
        this.musicMongoRepository = musicMongoRepository;
    }

    public void saveMusicsData(MusicData musicData) {
        musicMongoRepository.save(musicData);
    }

    public List<MusicData> getMusicsByPullId(String pullId) {
        return musicMongoRepository.findByOwnerPullId(pullId);
    }

    public MusicData getMusicsByMusicId(String musicId) {
        return musicMongoRepository.findByMusicId(musicId);
    }

    public void deleteMusicByMusicId(String musicId) {
        musicMongoRepository.deleteByMusicId(musicId);
    }


    public boolean addSuperMusic (long ownerChatId, String name, String path, String ownerPullId,
                                  String type){

        List<MusicData> listMusic = getMusicsByPullId(ownerPullId);
        String oldMusicId;
        boolean chekNew = true;
        for (MusicData music:listMusic){
            if(music.getOwnerChatId() == ownerChatId & music.getType().equals("Super")){
                chekNew = false;
                oldMusicId = music.getMusicId();
                deleteMusicByMusicId(oldMusicId);
            }
        }
        addNewMusic(ownerChatId, name, path, ownerPullId, type);

        return chekNew;

    }
    public void addNewMusic(long ownerChatId, String name, String path, String ownerPullId,
                            String type){
        MusicData newMusic = new MusicData();
        String uniqueID = UUID.randomUUID().toString();

        newMusic.setMusicId(uniqueID);
        newMusic.setOwnerChatId(ownerChatId);
        newMusic.setName(name);
        newMusic.setPath(path);
        newMusic.setOwnerPullId(ownerPullId);
        newMusic.setType(type);
        newMusic.setVotes(0);
        List<UserProfileData> votedUsers = new ArrayList<>();
        newMusic.setVotedUsers(votedUsers);

        saveMusicsData(newMusic);
    }

    public void likeMusic(MusicData music, UserProfileData user){
        List<UserProfileData> votedUsers = music.getVotedUsers();
        votedUsers.add(user);
        music.setVotedUsers(votedUsers);
        int votes = music.getVotes();
        votes +=1;
        music.setVotes(votes);
        saveMusicsData(music);
    }
    public void passMusic(MusicData music, UserProfileData user){
        List<UserProfileData> votedUsers = music.getVotedUsers();
        votedUsers.add(user);
        music.setVotedUsers(votedUsers);
        saveMusicsData(music);
    }
}
