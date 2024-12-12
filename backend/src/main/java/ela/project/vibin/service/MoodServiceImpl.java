package ela.project.vibin.service;

import ela.project.vibin.model.Mood;
import ela.project.vibin.repository.MoodRepository;
import ela.project.vibin.service.abstraction.MoodService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MoodServiceImpl implements MoodService {
    private final MoodRepository moodRepository;

    public MoodServiceImpl(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    @Override
    public List<UUID> getAllMoodIds(UUID emotionId) {
        List<Mood> moodsList  = moodRepository.findMoodsBy(emotionId);
        // for every mood object in the list, get their UUIDs

        List<UUID> moodIdList = new ArrayList<UUID>();

        for (Mood mood : moodsList) {
            moodIdList.add(mood.getId());
        }

        return moodIdList;
    }

}
