package ela.project.vibin.service.abstraction;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface EmotionRecognitionService {
    public String analyze(String input, boolean waitForModel) throws JsonProcessingException;
    public boolean isModelReady();

}
