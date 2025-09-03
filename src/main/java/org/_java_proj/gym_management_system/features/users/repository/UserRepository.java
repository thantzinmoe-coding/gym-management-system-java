package org._java_proj.gym_management_system.features.users.repository;

import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.features.users.dto.request.UserLoginProjection;
import org._java_proj.gym_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

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

    User findByIdAndStatus(Long id, Status status);

    @Query("SELECT u.id as id, u.email as email, r.name as roleName, u.password as password, u.status as status " +
            "FROM User u JOIN u.role r WHERE u.email = :email")
    Optional<UserLoginProjection> findUserLoginByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.name = :role AND u.status = :status")
    Page<User> findByRoleAndStatus(@Param("role") String role, @Param("status") Status status, Pageable pageable);

    @Query("SELECT u.id, u.profile.name, u.email, u.profile.phone, u.status FROM User u WHERE u.role.name = :trainer")
    Page<Object[]> findByRoleName(String trainer, Pageable pageable);
}
