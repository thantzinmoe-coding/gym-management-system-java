package org._java_proj.gym_management_system.features.users.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.users.dto.request.UserCreateRequest;
import org._java_proj.gym_management_system.features.users.dto.response.UserResponseDto;
import org._java_proj.gym_management_system.features.users.repository.RoleRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.features.users.service.UserService;
import org._java_proj.gym_management_system.model.Role;
import org._java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse createUser(UserCreateRequest request) {
        final Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found."));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("currentUser", dto))
                .message("User account created Successfully.").build();
    }
}
