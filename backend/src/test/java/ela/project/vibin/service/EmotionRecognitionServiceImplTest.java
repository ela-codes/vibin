package ela.project.vibin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ela.project.vibin.config.HuggingFaceApiPropertiesConfig;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmotionRecognitionServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HuggingFaceApiPropertiesConfig hfPropertiesConfig;

    @InjectMocks
    private EmotionRecognitionServiceImpl emotionRecognitionService;

    @Test
    public void analyze_validInput_returnsEmotion() throws JsonProcessingException {
        // Arrange
        String input = "i miss my grandma";
        String expectedEmotion = "sadness";

        when(hfPropertiesConfig.getApiUrl()).thenReturn("mockApiUrl");
        when(hfPropertiesConfig.getApiToken()).thenReturn("mockApiToken");

        // Mock RestTemplate response
        String mockResponse = """
                [
                    {
                        "generated_text": "sadness"
                    }
                ]
                """;

        String body = " {\"inputs\": \"" + input + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "mockApiToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.eq(entity),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        String emotion = emotionRecognitionService.analyze(input, false);

        // Assert
        assertEquals(expectedEmotion, emotion);
    }


    @Test
    public void analyze_waitForModelIsTrue_returnsEmotion() throws JsonProcessingException {
        // Arrange
        String input = "i miss my grandma";
        String expectedEmotion = "sadness";

        when(hfPropertiesConfig.getApiUrl()).thenReturn("mockApiUrl");
        when(hfPropertiesConfig.getApiToken()).thenReturn("mockApiToken");

        // Mock RestTemplate response
        String mockResponse = """
                [
                    {
                        "generated_text": "sadness"
                    }
                ]
                """;

        String body = " {\"inputs\": \"" + input + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "mockApiToken");
        headers.set("x-wait-for-model", "true");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.eq(entity),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        String emotion = emotionRecognitionService.analyze(input, true);

        // Assert
        assertEquals(expectedEmotion, emotion);
    }

    @Test
    public void analyze_emptyInput_throwsIllegalArgumentException() {
        // Arrange
        String input = ""; // Empty input

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emotionRecognitionService.analyze(input, false);
        });

        assertEquals("Input cannot be empty", exception.getMessage());
    }

    @Test
    public void analyze_nullInput_throwsIllegalArgumentException() {
        // Arrange
        String input = null; // Null input

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emotionRecognitionService.analyze(input, false);
        });

        assertEquals("Input cannot be empty", exception.getMessage());
    }

    @Test
    public void analyze_nonJsonResponse_throwsJsonProcessingException() {
        // Arrange
        String input = "test input";
        String nonJsonResponse = "Unexpected HTML or plain text response";

        when(hfPropertiesConfig.getApiUrl()).thenReturn("mockApiUrl");
        when(hfPropertiesConfig.getApiToken()).thenReturn("mockApiToken");

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(nonJsonResponse, HttpStatus.OK));

        // Act & Assert
        assertThrows(JsonProcessingException.class, () -> {
            emotionRecognitionService.analyze(input, false);
        });
    }

    @Test
    public void isModelReady_modelIsReady_returnsTrue() {
        // Assert
        when(hfPropertiesConfig.getApiUrl()).thenReturn("mockApiUrl");
        when(hfPropertiesConfig.getApiToken()).thenReturn("mockApiToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer mockApiToken");
        String body = " {\"inputs\": \"" + "I feel happy today" + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.eq(entity),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        // Act
        boolean result = emotionRecognitionService.isModelReady();

        // Assert
        assertTrue(result);
    }

    @Test
    public void isModelReady_modelIsNotReady_returnsFalse() {
        // Arrange
        when(hfPropertiesConfig.getApiUrl()).thenReturn("mockApiUrl");
        when(hfPropertiesConfig.getApiToken()).thenReturn("mockApiToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer mockApiToken");
        String body = " {\"inputs\": \"" + "I feel happy today" + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.eq(entity),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>("{}", HttpStatus.SERVICE_UNAVAILABLE));

        // Act
        boolean result = emotionRecognitionService.isModelReady();

        // Assert
        assertFalse(result);
    }

}