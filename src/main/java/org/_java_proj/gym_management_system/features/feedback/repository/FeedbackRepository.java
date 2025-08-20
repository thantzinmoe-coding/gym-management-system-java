package org._java_proj.gym_management_system.features.feedback.repository;

import org._java_proj.gym_management_system.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f")
    Page<Feedback> getAllFeedbacks(Pageable pageable);
}