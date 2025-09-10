package org._java_proj.gym_management_system.features.message.repository;

import org._java_proj.gym_management_system.model.ClassGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
    Page<ClassGroup> findByIsPrivateFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT cg FROM ClassGroup cg WHERE " +
            "LOWER(cg.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
            "cg.isPrivate = false " +
            "ORDER BY cg.createdAt DESC")
    List<ClassGroup> searchGroups(String searchTerm);
}
