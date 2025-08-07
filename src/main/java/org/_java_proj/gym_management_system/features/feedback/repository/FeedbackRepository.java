package org._java_proj.gym_management_system.features.feedback.repository;

import org._java_proj.gym_management_system.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Custom methods can be added here if needed
}