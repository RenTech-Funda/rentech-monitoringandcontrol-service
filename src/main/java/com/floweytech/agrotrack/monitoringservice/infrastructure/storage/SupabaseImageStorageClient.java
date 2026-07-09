package com.floweytech.agrotrack.monitoringservice.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class SupabaseImageStorageClient implements ImageStorageClient {

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;
    private final RestClient restClient;

    public SupabaseImageStorageClient(
            @Value("${supabase.url:}") String supabaseUrl,
            @Value("${supabase.service-role-key:}") String serviceRoleKey,
            @Value("${supabase.storage.bucket:plant-observations}") String bucket) {
        this.supabaseUrl = removeTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.restClient = RestClient.builder()
                .baseUrl(this.supabaseUrl)
                .build();
    }

    @Override
    public String uploadPlantObservationImage(Long sessionId, Long observationId, MultipartFile file) {
        if (!StringUtils.hasText(supabaseUrl) || !StringUtils.hasText(serviceRoleKey)) {
            throw new IllegalStateException("Supabase storage is not configured");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        try {
            var extension = resolveExtension(file.getOriginalFilename());
            var path = "plant-sampling-sessions/%d/observations/%d/%s%s"
                    .formatted(sessionId, observationId, UUID.randomUUID(), extension);

            restClient.put()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, path)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("x-upsert", "true")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType() == null ? "image/jpeg" : file.getContentType())
                    .body(file.getBytes())
                    .retrieve()
                    .toBodilessEntity();

            return "%s/storage/v1/object/public/%s/%s".formatted(
                    supabaseUrl,
                    bucket,
                    encodePath(path)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not upload image to Supabase", e);
        }
    }

    private String resolveExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20").replace("%2F", "/");
    }

    private String removeTrailingSlash(String value) {
        if (value == null) return "";
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
