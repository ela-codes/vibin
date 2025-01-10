package ela.project.vibin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ela.project.vibin.config.HuggingFaceApiPropertiesConfig;
import ela.project.vibin.service.abstraction.EmotionRecognitionService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Service
public class EmotionRecognitionServiceImpl implements EmotionRecognitionService {

    private final HuggingFaceApiPropertiesConfig hfPropertiesConfig;
    private final RestTemplate restTemplate;

    public EmotionRecognitionServiceImpl(HuggingFaceApiPropertiesConfig hfPropertiesConfig,
                                         RestTemplate restTemplate) {
        this.hfPropertiesConfig = hfPropertiesConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isModelReady() {
        final String HF_API_URL = hfPropertiesConfig.getApiUrl();
        final String HF_API_TOKEN = hfPropertiesConfig.getApiToken();
        final String RANDOM_INPUT = "I feel happy today";

        // build HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + HF_API_TOKEN);

        // pass the input as part of the body
        String body = " {\"inputs\": \"" + RANDOM_INPUT + "\"}";

        // complete building the HTTP request
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            // make the API call
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(HF_API_URL), HttpMethod.POST, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                return false;
            } else {
                throw new RuntimeException("API returned an unhandled non-OK status: " + e.getMessage());
            }
        }
    }

    @Override
    public String analyze(String input, boolean waitForModel) throws JsonProcessingException {

        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        final String HF_API_URL = hfPropertiesConfig.getApiUrl();
        final String HF_API_TOKEN = hfPropertiesConfig.getApiToken();

        // build HTTP request header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + HF_API_TOKEN);

        if (waitForModel) {
            headers.set("x-wait-for-model", "true");
        }

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
        } catch (RuntimeException e) {
            throw new RuntimeException("API returned non-OK status: " + e.getMessage());
        }
    }

    // helper function extracts the emotion from the JSON response
    private String extractEmotion(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode generatedTextNode = rootNode.findValue("generated_text");

        if (generatedTextNode != null) {
            return generatedTextNode.asText();
        } else {
            throw new JsonProcessingException("generated_text node not found") {};
        }
    }
}
