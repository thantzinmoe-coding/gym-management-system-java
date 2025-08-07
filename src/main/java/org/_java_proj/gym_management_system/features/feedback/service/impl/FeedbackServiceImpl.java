package org._java_proj.gym_management_system.features.feedback.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
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

        Feedback feedback = new Feedback();
            // Uses email from DTO
        feedback.setUser(member);
        feedback.setTrainer(trainer);
        feedback.setComment(request.getComment()); // Uses comment from DTO
        feedback.setRatingPoints(request.getRatingPoints());
        feedback.setDate(request.getDate());// Uses ratingPoints from DTO

        // Note: The 'date', 'user', and 'trainer' fields from the entity are not
        // being populated from the request. You might need to handle these
        // depending on your application's requirements (e.g., get current date,
        // fetch logged-in user, or require trainer ID in the request).

        feedbackRepository.save(feedback);

        FeedbackResponseDto dto = modelMapper.map(feedback, FeedbackResponseDto.class);
         // Set a status message for the response

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("feedback", dto)) // Data key for the response payload
                .message("Feedback submitted successfully.")
                .build();
    }
}