package org.java_proj.gym_management_system.features.analytics.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.analytics.client.MlServiceClient;
import org.java_proj.gym_management_system.features.analytics.service.AnalyticsService;
import org.java_proj.gym_management_system.features.bookPackage.repository.BookPackageRepository;
import org.java_proj.gym_management_system.features.users.repository.ProfileRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.Profile;
import org.java_proj.gym_management_system.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final int MAX_LIMIT = 500;

    private final MlServiceClient mlServiceClient;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BookPackageRepository bookPackageRepository;

    @Override
    public ApiResponse getChurnSummary() {
        return ok(mlServiceClient.get("/churn/summary"), "Churn summary retrieved.");
    }

    @Override
    public ApiResponse getAtRiskMembers(final int limit, final double minRisk) {
        final JsonNode result = mlServiceClient.get("/churn/members?limit={limit}&min_risk={minRisk}",
                clamp(limit), minRisk);
        withMemberNames((ArrayNode) result.get("members"));
        return ok(result, "At-risk members retrieved.");
    }

    @Override
    public ApiResponse getMyMembersAtRisk() {
        final User trainer = currentUser();
        final Set<Long> myMembers = new HashSet<>(
                bookPackageRepository.findDistinctMemberIdsByTrainer(trainer.getId()));
        final JsonNode result = mlServiceClient.get("/churn/members?limit={limit}", MAX_LIMIT);

        final ArrayNode filtered = ((ObjectNode) result).arrayNode();
        result.get("members").forEach(m -> {
            if (myMembers.contains(m.get("user_id").asLong())) {
                filtered.add(m);
            }
        });
        withMemberNames(filtered);
        ((ObjectNode) result).set("members", filtered);
        ((ObjectNode) result).put("total", filtered.size());
        return ok(result, "Your members' churn risk retrieved.");
    }

    @Override
    public ApiResponse getMemberChurn(final Long memberId) {
        final JsonNode result = mlServiceClient.get("/churn/members/{id}", memberId);
        addName((ObjectNode) result, "user_id", "name");
        return ok(result, "Member churn risk retrieved.");
    }

    @Override
    public ApiResponse getMyRecommendations(final int limit) {
        return getRecommendationsFor(currentUser().getId(), limit);
    }

    @Override
    public ApiResponse getRecommendations(final Long memberId, final int limit) {
        return getRecommendationsFor(memberId, limit);
    }

    @Override
    public ApiResponse getModelInfo() {
        return ok(mlServiceClient.get("/model"), "Model information retrieved.");
    }

    @Override
    public ApiResponse retrainModel() {
        return ok(mlServiceClient.post("/admin/retrain"), "Model retrained.");
    }

    private ApiResponse getRecommendationsFor(final Long memberId, final int limit) {
        final JsonNode result = mlServiceClient.get("/recommendations/{id}?k={k}", memberId,
                Math.max(1, Math.min(limit, 10)));
        result.get("recommendations").forEach(r -> addName((ObjectNode) r, "trainer_id", "trainer_name"));
        return ok(result, "Package recommendations retrieved.");
    }

    private User currentUser() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    private void withMemberNames(final ArrayNode members) {
        if (members != null) {
            members.forEach(m -> addName((ObjectNode) m, "user_id", "name"));
        }
    }

    private void addName(final ObjectNode node, final String idField, final String nameField) {
        if (node == null || !node.hasNonNull(idField)) {
            return;
        }
        profileRepository.findByUser_Id(node.get(idField).asLong())
                .map(Profile::getName)
                .ifPresent(name -> node.put(nameField, name));
    }

    private static int clamp(final int limit) {
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }

    private static ApiResponse ok(final JsonNode data, final String message) {
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(data)
                .message(message)
                .build();
    }
}
