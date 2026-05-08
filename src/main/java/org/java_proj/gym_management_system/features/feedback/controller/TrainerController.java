package org.java_proj.gym_management_system.features.feedback.controller;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.feedback.dto.response.TrainerRatingDto;
import org.java_proj.gym_management_system.model.User;
import org.java_proj.gym_management_system.features.feedback.service.FeedbackService;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trainers")
public class TrainerController {

    private final UserRepository userRepository;
    private final FeedbackService feedbackService;

    @GetMapping("/ratings")
    public String getTrainerRatings(Model model) {
        // Assuming you have a Role defined for "Trainer"
        List<User> trainers = userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && "Trainer".equals(user.getRole().getName()))
                .collect(Collectors.toList());

        List<TrainerRatingDto> trainerRatings = trainers.stream().map(trainer -> {
            // Extract trainer ID from user object and use it safely
            Long trainerId = trainer.getId(); // Retrieve Trainer ID
            ApiResponse response = feedbackService.getAverageRatingByTrainer(trainerId);
            Double averageRating = (Double) ((Map<?, ?>) response.getData()).get("averageRating");

            return new TrainerRatingDto(trainerId, trainer.getEmail(), averageRating != null ? averageRating : 0.0);

        }).collect(Collectors.toList());

        model.addAttribute("trainerRatings", trainerRatings);
        return "trainers/trainer_ratings"; // Thymeleaf template
    }
}
