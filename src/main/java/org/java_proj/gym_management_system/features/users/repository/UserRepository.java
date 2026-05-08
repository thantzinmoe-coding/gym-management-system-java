package org.java_proj.gym_management_system.features.users.repository;

import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.features.users.dto.request.UserLoginProjection;
import org.java_proj.gym_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.profile p " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "       LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(p.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(CAST(p.address AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:role IS NULL OR :role = '' OR u.role.name = :role) " +
            "AND (:status IS NULL OR u.status = :status)")
    Page<User> findUsersWithFilters(@Param("keyword") String keyword,
                                    @Param("role") String role,
                                    @Param("status") Status status,
                                    Pageable pageable);


    @Query("SELECT u.id as id, u.email as email, r.name as roleName, u.password as password, u.status as status " +
            "FROM User u JOIN u.role r WHERE u.email = :email")
    Optional<UserLoginProjection> findUserLoginByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.name = :role AND u.status = :status")
    Page<User> findByRoleAndStatus(@Param("role") String role, @Param("status") Status status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.name = :trainer")
    Page<User> findByRoleName(String trainer, Pageable pageable);


    @Query(
            value = """
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.profile
        LEFT JOIN FETCH u.userDetailInfo
        WHERE u.role.name = 'TRAINER'
          AND SIZE(u.assignedGymPackages) < :maxCount
          AND u.status = :status
    """,
            countQuery = """
        SELECT COUNT(u)
        FROM User u
        WHERE u.role.name = 'TRAINER'
          AND SIZE(u.assignedGymPackages) < :maxCount
          AND u.status = :status
    """
    )
    Page<User> findAvailableTrainers(int maxCount, Status status, Pageable pageable);

    Optional<User> findByIdAndStatus(Long trainerId, Status status);

    int countByRole_NameAndStatus(String role, Status status);

    boolean existsByIdAndRole_NameAndStatus(Long id, String trainer, Status status);
}
