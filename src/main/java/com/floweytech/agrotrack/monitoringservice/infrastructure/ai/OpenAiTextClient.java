package com.floweytech.agrotrack.monitoringservice.infrastructure.ai;

import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.RecommendationResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiTextClient implements AiTextClient {

    private final String apiKey;
    private final String model;
    private final RestClient restClient;

    public OpenAiTextClient(
            @Value("${openai.api.key:}") String apiKey,
            @Value("${openai.api.url:https://api.openai.com/v1}") String openAiApiUrl,
            @Value("${openai.text.model:gpt-4.1-mini}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(openAiApiUrl)
                .build();
    }

    @Override
    public String summarizeHealth(String context, String fallback) {
        if (!StringUtils.hasText(apiKey)) return fallback;
        var prompt = """
                Eres un asistente agrícola. Explica en máximo 2 frases el estado de salud de una parcela.
                Usa solo los datos entregados. Si faltan datos, dilo con prudencia.

                Datos:
                %s
                """.formatted(context);
        return requestText(prompt, fallback);
    }

    @Override
    public String summarizeRecommendations(String context, List<RecommendationResource> recommendations, String fallback) {
        if (!StringUtils.hasText(apiKey)) return fallback;
        var prompt = """
                Eres un asistente agrícola. Redacta un resumen breve y accionable para el agricultor.
                No inventes datos ni prometas diagnósticos definitivos.

                Datos:
                %s

                Recomendaciones detectadas por reglas:
                %s
                """.formatted(context, recommendations);
        return requestText(prompt, fallback);
    }

    @SuppressWarnings("unchecked")
    private String requestText(String prompt, String fallback) {
        try {
            var body = Map.of(
                    "model", model,
                    "input", List.of(Map.of(
                            "role", "user",
                            "content", List.of(Map.of(
                                    "type", "input_text",
                                    "text", prompt
                            ))
                    ))
            );

            Map<String, Object> response = restClient.post()
                    .uri("/responses")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return extractOutputText(response, fallback);
        } catch (Exception ignored) {
            return fallback;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractOutputText(Map<String, Object> response, String fallback) {
        if (response == null) return fallback;
        var outputText = response.get("output_text");
        if (outputText instanceof String text && StringUtils.hasText(text)) {
            return text;
        }
        var output = response.get("output");
        if (!(output instanceof List<?> outputItems)) return fallback;
        for (var item : outputItems) {
            if (!(item instanceof Map<?, ?> outputItem)) continue;
            var content = outputItem.get("content");
            if (!(content instanceof List<?> contentItems)) continue;
            for (var contentItem : contentItems) {
                if (contentItem instanceof Map<?, ?> contentMap) {
                    var text = contentMap.get("text");
                    if (text instanceof String textValue && StringUtils.hasText(textValue)) {
                        return textValue;
                    }
                }
            }
        }
        return fallback;
    }
}
