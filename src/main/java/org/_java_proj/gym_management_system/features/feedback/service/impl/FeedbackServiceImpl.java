package org._java_proj.gym_management_system.features.feedback.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackUpdateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.response.UserSummaryDto;
import org._java_proj.gym_management_system.features.feedback.service.FeedbackService;
import org._java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
import org._java_proj.gym_management_system.features.feedback.dto.response.FeedbackResponseDto;
import org._java_proj.gym_management_system.features.feedback.repository.FeedbackRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.Feedback; // Corrected import for Feedback entity
import org._java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse createFeedback(FeedbackCreateRequest request) {

        final User member = userRepository.findById(request.getMember_id())
                .orElseThrow(() -> new EntityNotFoundException("Member not found."));
        final User trainer  = userRepository.findById(request.getTrainer_id())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found."));

        // Create and populate Feedback entity FIRST
        Feedback feedback = new Feedback();
        feedback.setUser(member);
        feedback.setTrainer(trainer);
        feedback.setComment(request.getComment());
        feedback.setRatingPoints(request.getRatingPoints());
        feedback.setDate(request.getDate());

        // Save the Feedback entity
        feedbackRepository.save(feedback);

        // Map to DTO for response using UserSummaryDto
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setMember(UserSummaryDto.fromUser(feedback.getUser()));     // Use DTO factory method
        dto.setTrainer(UserSummaryDto.fromUser(feedback.getTrainer())); // Use DTO factory method
        dto.setComment(feedback.getComment());
        dto.setRatingPoints(feedback.getRatingPoints());
        dto.setDate(feedback.getDate());
        // Map other fields from Feedback entity to DTO if needed (e.g., id, createdAt)
        // Example: dto.setId(feedback.getId());

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("feedback", dto)) // Data key for the response payload
                .message("Feedback submitted successfully.")
                .build();
    }
    @Override
    public ApiResponse getFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setMember(UserSummaryDto.fromUser(feedback.getUser()));
        dto.setTrainer(UserSummaryDto.fromUser(feedback.getTrainer()));
        dto.setComment(feedback.getComment());
        dto.setRatingPoints(feedback.getRatingPoints());
        dto.setDate(feedback.getDate());

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("feedback", dto))
                .message("Feedback fetched.")
                .build();
    }
    @Override
    public ApiResponse listFeedbacks() {
        List<FeedbackResponseDto> dtos = feedbackRepository.findAll().stream()
                .map(fb -> {
                    FeedbackResponseDto dto = new FeedbackResponseDto();
                    dto.setMember(UserSummaryDto.fromUser(fb.getUser()));
                    dto.setTrainer(UserSummaryDto.fromUser(fb.getTrainer()));
                    dto.setComment(fb.getComment());
                    dto.setRatingPoints(fb.getRatingPoints());
                    dto.setDate(fb.getDate());
                    return dto;
                })
                .toList();
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("feedbacks", dtos))
                .message("All feedback entries fetched.")
                .build();
    }
    @Override
    @Transactional
    public ApiResponse updateFeedback(Long id, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

        if (request.getComment() != null) feedback.setComment(request.getComment());
        if (request.getRatingPoints() != null) feedback.setRatingPoints(request.getRatingPoints());
        if (request.getDate() != null) feedback.setDate(request.getDate());

        feedbackRepository.save(feedback);
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setMember(UserSummaryDto.fromUser(feedback.getUser()));
        dto.setTrainer(UserSummaryDto.fromUser(feedback.getTrainer()));
        dto.setComment(feedback.getComment());
        dto.setRatingPoints(feedback.getRatingPoints());
        dto.setDate(feedback.getDate());

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("feedback", dto))
                .message("Feedback updated successfully.")
                .build();
    }
    @Override
    @Transactional
    public ApiResponse deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));
        feedbackRepository.delete(feedback);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Feedback deleted successfully.")
                .build();
    }
    @Override
    public ApiResponse getAverageRatingByTrainer(Long trainerId) {
        Double averageRating = feedbackRepository.findAverageRatingByTrainerId(trainerId);
        if (averageRating == null) {
            averageRating = 0.0; // Or handle the case where there's no rating
        }
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("averageRating", averageRating))
                .message("Average rating fetched for trainer.")
                .build();
    }
}
