package org._java_proj.gym_management_system.home.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.features.feedback.repository.FeedbackRepository;
import org._java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.home.service.HomeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final UserRepository userRepository;
    private final GymPackageRepository gymPackageRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public int totalMembers(String role) {
        return userRepository.countByRole_NameAndStatus(role, Status.ACTIVE);
    }

    @Override
    public int totalPackageCount() {
        return gymPackageRepository.findAll().size();
    }

    @Override
    public int totalReviewCount() {
        return feedbackRepository.findAll().size();
    }
}
