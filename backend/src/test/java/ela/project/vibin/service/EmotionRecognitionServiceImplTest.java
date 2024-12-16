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
        HttpEntity<String> mockRequest = new HttpEntity<>(body, headers);

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        String emotion = emotionRecognitionService.analyze(input);

        // Assert
        assertEquals(expectedEmotion, emotion);
    }

    @Test
    public void analyze_emptyInput_throwsIllegalArgumentException() {
        // Arrange
        String input = ""; // Empty input

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emotionRecognitionService.analyze(input);
        });

        assertEquals("Input cannot be empty", exception.getMessage());
    }

    @Test
    public void analyze_nullInput_throwsIllegalArgumentException() {
        // Arrange
        String input = null; // Null input

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emotionRecognitionService.analyze(input);
        });

        assertEquals("Input cannot be empty", exception.getMessage());
    }

    @Test
    public void analyze_apiErrorResponse_throwsRuntimeException() {
        // Arrange
        String input = "test input";
        String mockErrorResponse = """
            {
                "error": "Model mrm8488/t5-base-finetuned-emotion is currently loading",
                "estimated_time": 35.6677131652832
            }
            """;

        when(hfPropertiesConfig.getApiUrl()).thenReturn("mockApiUrl");
        when(hfPropertiesConfig.getApiToken()).thenReturn("mockApiToken");

        when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockErrorResponse, HttpStatus.OK));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emotionRecognitionService.analyze(input);
        });

        assertTrue(exception.getMessage().contains("Model mrm8488/t5-base-finetuned-emotion is currently loading"));
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
            emotionRecognitionService.analyze(input);
        });
    }

}