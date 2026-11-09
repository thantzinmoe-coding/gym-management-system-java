package org.java_proj.gym_management_system.features.feedback.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org.java_proj.gym_management_system.features.feedback.dto.request.FeedbackUpdateRequest;
import org.java_proj.gym_management_system.features.feedback.service.FeedbackService;
import org.java_proj.gym_management_system.features.feedback.dto.request.FeedbackCreateRequest;
import org.java_proj.gym_management_system.features.feedback.dto.response.FeedbackResponseDto;
import org.java_proj.gym_management_system.features.feedback.repository.FeedbackRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.Feedback; // Corrected import for Feedback entity
import org.java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        LocalDate date = LocalDate.now();
        // Create and populate Feedback entity FIRST
        Feedback feedback = new Feedback();
        feedback.setMember(member);
        feedback.setTrainer(trainer);
        feedback.setComment(request.getComment());
        feedback.setRatingPoints(request.getRatingPoints());
        feedback.setDate(date);

        // Save the Feedback entity
        feedbackRepository.save(feedback);

        // Map to DTO for response using UserSummaryDto
//        FeedbackResponseDto dto = new FeedbackResponseDto();
//        dto.setMember(UserSummaryDto.fromUser(feedback.getUser()));     // Use DTO factory method
//        dto.setTrainer(UserSummaryDto.fromUser(feedback.getTrainer())); // Use DTO factory method
//        dto.setComment(feedback.getComment());
//        dto.setRatingPoints(feedback.getRatingPoints());
//        dto.setDate(feedback.getDate());
        // Map other fields from Feedback entity to DTO if needed (e.g., id, createdAt)
        // Example: dto.setId(feedback.getId());

        FeedbackResponseDto dto = modelMapper.map(feedback, FeedbackResponseDto.class);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("feedback", dto)) // Data key for the response payload
                .message("Feedback submitted successfully.")
                .build();
    }



    @Override
    public ApiResponse getFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

        FeedbackResponseDto dto = modelMapper.map(feedback, FeedbackResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("feedback", dto))
                .message("Feedback fetched.")
                .build();
    }


    @Override
    public PaginatedApiResponse<FeedbackResponseDto> listFeedbacks(Pageable pageable) {
        Page<Feedback> page = feedbackRepository.findAll(pageable);

        List<FeedbackResponseDto> data = page.getContent().stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackResponseDto.class))
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<FeedbackResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }



    @Override
    @Transactional
    public ApiResponse updateFeedback(Long id, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

        LocalDate date = LocalDate.now();
        if (request.getComment() != null) feedback.setComment(request.getComment());
        if (request.getRatingPoints() != null) feedback.setRatingPoints(request.getRatingPoints());
        feedback.setDate(date);

        feedbackRepository.save(feedback);
        FeedbackResponseDto dto = modelMapper.map(feedback, FeedbackResponseDto.class);

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
