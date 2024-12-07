package ela.project.vibin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ela.project.vibin.config.HuggingFaceApiPropertiesConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Service
public class EmotionRecognitionService {

    private final HuggingFaceApiPropertiesConfig hfPropertiesConfig;

    public EmotionRecognitionService(HuggingFaceApiPropertiesConfig hfPropertiesConfig) {
        this.hfPropertiesConfig = hfPropertiesConfig;
    }

    public String analyze(String input) throws JsonProcessingException {

        final String HF_API_URL = hfPropertiesConfig.getApiUrl();
        final String HF_API_TOKEN = hfPropertiesConfig.getApiToken();

        RestTemplate restTemplate = new RestTemplate();

        // build HTTP request header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + HF_API_TOKEN);

        // pass the input as part of the body
        String body = " {\"inputs\": \"" + input + "\"}";

        // complete building the HTTP request
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // make the API call
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(HF_API_URL), HttpMethod.POST, entity, String.class
            );
            String responseBody = response.getBody();
            return extractEmotion(responseBody);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    // helper function extracts the emotion from the JSON response
    private String extractEmotion(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String generatedValue = rootNode.get(0).path("generated_text").asText();
        return generatedValue;
    }
}
