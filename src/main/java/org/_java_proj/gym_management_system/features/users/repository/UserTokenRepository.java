package org._java_proj.gym_management_system.features.users.repository;


import org._java_proj.gym_management_system.features.users.dto.request.UserTokenProjection;
import org._java_proj.gym_management_system.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    UserToken findByUsername(String email);

    @Query(value = "SELECT * FROM user_tokens u WHERE u.username = :email ORDER BY u.createdAt DESC LIMIT 1;" , nativeQuery = true)
    UserToken findTopByUsernameOrderByCreatedAtDesc(@Param("email") String username);

    void deleteByUsername(String email);

    @Query("SELECT ut.token as token, ut.createdAt as createdAt " +
            "FROM UserToken ut WHERE ut.username = :username " +
            "ORDER BY ut.createdAt DESC LIMIT 1")
    Optional<UserTokenProjection> findLatestTokenByUsername(@Param("username") String username);

}
