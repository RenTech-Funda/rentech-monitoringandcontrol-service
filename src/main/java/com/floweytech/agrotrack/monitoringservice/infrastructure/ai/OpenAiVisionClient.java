package com.floweytech.agrotrack.monitoringservice.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiVisionClient implements AiVisionClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiVisionClient.class);

    private final String apiKey;
    private final String model;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OpenAiVisionClient(
            @Value("${openai.api.key:}") String apiKey,
            @Value("${openai.api.url:https://api.openai.com/v1}") String openAiApiUrl,
            @Value("${openai.vision.model:gpt-4.1-mini}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl(openAiApiUrl)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PlantPhotoAnalysisResult analyzePlantPhoto(String imageUrl) {
        if (!StringUtils.hasText(apiKey)) {
            LOGGER.warn("OpenAI vision analysis skipped because OPENAI_API_KEY is not configured");
            return PlantPhotoAnalysisResult.fallback();
        }

        try {
            var prompt = """
                    Analiza la foto de esta planta para apoyar una inspección agrícola.
                    Devuelve únicamente JSON válido con estas claves:
                    detectedIssue, diagnosis, recommendation, confidence.
                    Usa lenguaje prudente: posible/probable. No afirmes diagnósticos definitivos.
                    confidence debe estar entre 0 y 1.
                    """;

            var body = Map.of(
                    "model", model,
                    "input", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "input_text", "text", prompt),
                                    Map.of("type", "input_image", "image_url", imageUrl)
                            )
                    ))
            );

            Map<String, Object> response = restClient.post()
                    .uri("/responses")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return parseAnalysis(extractOutputText(response));
        } catch (Exception exception) {
            LOGGER.warn("OpenAI vision analysis failed for imageUrl {}: {}", imageUrl, exception.getMessage(), exception);
            return PlantPhotoAnalysisResult.fallback();
        }
    }

    private PlantPhotoAnalysisResult parseAnalysis(String text) {
        if (!StringUtils.hasText(text)) return PlantPhotoAnalysisResult.fallback();
        try {
            var json = extractJson(text);
            JsonNode node = objectMapper.readTree(json);
            return new PlantPhotoAnalysisResult(
                    node.path("detectedIssue").asText("UNKNOWN"),
                    node.path("diagnosis").asText("No se pudo interpretar el diagnóstico."),
                    node.path("recommendation").asText("Realizar seguimiento manual."),
                    node.path("confidence").asDouble(0.0)
            );
        } catch (Exception ignored) {
            return new PlantPhotoAnalysisResult(
                    "AI_TEXT_RESPONSE",
                    text,
                    "Revisar la observación y validar en campo.",
                    0.5
            );
        }
    }

    private String extractJson(String text) {
        var start = text.indexOf('{');
        var end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String extractOutputText(Map<String, Object> response) {
        if (response == null) return null;
        var outputText = response.get("output_text");
        if (outputText instanceof String text) return text;
        var output = response.get("output");
        if (!(output instanceof List<?> outputItems)) return null;
        for (var item : outputItems) {
            if (!(item instanceof Map<?, ?> outputItem)) continue;
            var content = outputItem.get("content");
            if (!(content instanceof List<?> contentItems)) continue;
            for (var contentItem : contentItems) {
                if (contentItem instanceof Map<?, ?> contentMap) {
                    var text = contentMap.get("text");
                    if (text instanceof String textValue) {
                        return textValue;
                    }
                }
            }
        }
        return null;
    }
}
