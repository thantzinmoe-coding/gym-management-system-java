package org._java_proj.gym_management_system.features.feedback.repository;

import org._java_proj.gym_management_system.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Custom methods can be added here if needed

    @Query("SELECT AVG(f.ratingPoints) FROM Feedback f WHERE f.trainer.id = :trainerId")
    Double findAverageRatingByTrainerId(@Param("trainerId") Long trainerId);
}