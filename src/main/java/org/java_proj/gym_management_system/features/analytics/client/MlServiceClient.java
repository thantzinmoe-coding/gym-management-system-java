package org.java_proj.gym_management_system.features.analytics.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Calls the internal gym-ml-service (churn scores and package recommendations).
 * The service is never exposed to browsers; this backend enforces roles and forwards
 * requests with the shared X-ML-API-Key.
 */
@Slf4j
@Component
public class MlServiceClient {

    private final RestClient restClient;

    public MlServiceClient(@Value("${ml.service.url}") final String baseUrl,
                           @Value("${ml.service.api-key}") final String apiKey) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(30000);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("X-ML-API-Key", apiKey)
                .build();
    }

    public JsonNode get(final String path, final Object... uriVariables) {
        return call(() -> restClient.get().uri(path, uriVariables).retrieve().body(JsonNode.class));
    }

    public JsonNode post(final String path) {
        return call(() -> restClient.post().uri(path).retrieve().body(JsonNode.class));
    }

    private JsonNode call(final java.util.function.Supplier<JsonNode> request) {
        try {
            return request.get();
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntityNotFoundException(detail(e));
        } catch (RestClientResponseException e) {
            log.warn("ML service returned {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MlServiceUnavailableException(detail(e), e.getStatusCode().value());
        } catch (RestClientException e) {
            log.warn("ML service unreachable: {}", e.getMessage());
            throw new MlServiceUnavailableException("Analytics service is not reachable.",
                    HttpStatus.SERVICE_UNAVAILABLE.value());
        }
    }

    private static String detail(final RestClientResponseException e) {
        try {
            final JsonNode body = e.getResponseBodyAs(JsonNode.class);
            if (body != null && body.hasNonNull("detail")) {
                return body.get("detail").asText();
            }
        } catch (RuntimeException ignored) {
            // fall through to the status text
        }
        return e.getStatusText();
    }

    public static class MlServiceUnavailableException extends RuntimeException {
        private final int status;

        public MlServiceUnavailableException(final String message, final int status) {
            super(message);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
