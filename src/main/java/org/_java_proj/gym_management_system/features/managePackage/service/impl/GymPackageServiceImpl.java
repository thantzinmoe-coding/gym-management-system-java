package org._java_proj.gym_management_system.features.managePackage.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.GymPackageType;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.managePackage.dto.request.GymPackageCreateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.request.GymPackageUpdateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.response.GymPackageResponseDto;
import org._java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org._java_proj.gym_management_system.features.managePackage.service.GymPackageService;
import org._java_proj.gym_management_system.model.AssignedGymPackage;
import org._java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org._java_proj.gym_management_system.model.GymPackage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GymPackageServiceImpl implements GymPackageService {

    private final GymPackageRepository gymPackageRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse createGymPackage(GymPackageCreateRequest request) {
        GymPackage gymPackage = new GymPackage();

        LocalDate today = LocalDate.now();

        // ✅ Ensure startDate is at least 5 days from today
        if (request.getStartDate() != null && !request.getStartDate().isAfter(today.plusDays(4))) {
            throw new IllegalArgumentException("❌ Start date must be at least 5 days from today.");
        }

        gymPackage.setName(request.getName());
        gymPackage.setDescription(request.getDescription());
        gymPackage.setGymPackageType(request.getGymPackageType());
        gymPackage.setPrice(request.getPrice());
        gymPackage.setDuration(request.getDuration());
        gymPackage.setStartDate(request.getStartDate());
        gymPackage.setEndDate(request.getEndDate());
        gymPackage.setStatus(Status.INACTIVE);

        gymPackageRepository.save(gymPackage);

        GymPackageResponseDto dto = modelMapper.map(gymPackage, GymPackageResponseDto.class);

        return ApiResponse.builder().success(1).code(HttpStatus.CREATED.value())
                .data(Map.of("Package", dto))
                .message("Package created Successfully.").build();
    }

    @Override
    public ApiResponse getGymPackageById(Long gymPackageId) {
        GymPackage gymPackage = this.gymPackageRepository.findByIdAndStatus(gymPackageId, Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("Gym package not with this id: "+gymPackageId));

        GymPackageResponseDto dto = modelMapper.map(gymPackage, GymPackageResponseDto.class);

        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("GymPackageDetail", dto))
                .message("Gym Package Detail")
                .build();
    }

    @Override
    public PaginatedApiResponse<GymPackageResponseDto> getAllGymPackages(Pageable pageable) {
        Page<GymPackage> page = gymPackageRepository.findAllGymPackages(pageable);

        List<GymPackageResponseDto> data = page.getContent().stream()
                .map(gymPackage -> {
                    GymPackageResponseDto dto = modelMapper.map(gymPackage, GymPackageResponseDto.class);

                    // Get assigned trainer name (only ACTIVE)
                    AssignedGymPackage assignment = gymPackage.getAssignedGymPackage();

                    if (assignment != null && assignment.getStatus() == Status.ACTIVE) {
                        User trainer = assignment.getTrainer();
                        if (trainer != null) {
                            dto.setTrainerId(trainer.getId());
                            dto.setTrainerName(trainer.getProfile().getName());
                        }
                    }

                    return dto;
                })
                .toList();



        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<GymPackageResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }


    @Override
    public PaginatedApiResponse<GymPackageResponseDto> getGymPackagesByType(GymPackageType type, Pageable pageable) {
        Page<GymPackage> page = this.gymPackageRepository.findByGymPackageType(type, pageable);

        List<GymPackageResponseDto> data = page.getContent().stream()
                .map(gymPackage -> modelMapper.map(gymPackage, GymPackageResponseDto.class))
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<GymPackageResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateGymPackage(Long id, GymPackageUpdateRequest request) {
        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found with this ID: " + id));

        LocalDate today = LocalDate.now();

        // ❌ Prevent editing expired package
        if (today.isAfter(gymPackage.getEndDate())) {
            throw new IllegalStateException("❌ Cannot update an overdue (expired) package.");
        }

        // ❌ Prevent editing active package
        if (!today.isBefore(gymPackage.getStartDate()) && !today.isAfter(gymPackage.getEndDate())) {
            throw new IllegalStateException("❌ Cannot update an active package.");
        }

        if (request.getStartDate() != null && !request.getStartDate().isAfter(today.plusDays(4))) {
            throw new IllegalArgumentException("❌ Start date must be at least 5 days from today.");
        }

        // ✅ Allow update only if package is upcoming
        Optional.ofNullable(request.getName()).ifPresent(gymPackage::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(gymPackage::setDescription);
        Optional.ofNullable(request.getGymPackageType()).ifPresent(gymPackage::setGymPackageType);
        Optional.of(request.getPrice()).ifPresent(gymPackage::setPrice);
        Optional.ofNullable(request.getDuration()).ifPresent(gymPackage::setDuration);
        Optional.ofNullable(request.getStartDate()).ifPresent(gymPackage::setStartDate);
        Optional.ofNullable(request.getEndDate()).ifPresent(gymPackage::setEndDate);

        GymPackage saved = gymPackageRepository.save(gymPackage);

        GymPackageResponseDto dto = modelMapper.map(saved, GymPackageResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("updatedGymPackage", dto))
                .message("✅ Upcoming package updated successfully")
                .build();
    }



    public ApiResponse deleteGymPackage(Long id) {
        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found with this ID: " + id));

        LocalDate today = LocalDate.now();

        // ✅ Prevent deletion of active packages
        if (!today.isBefore(gymPackage.getStartDate()) && !today.isAfter(gymPackage.getEndDate())) {
            throw new IllegalStateException("Cannot delete an active package.");
        }

        // ✅ Allow deletion if package is upcoming
        if (today.isBefore(gymPackage.getStartDate())) {
            gymPackageRepository.delete(gymPackage);
            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .message("Upcoming package deleted successfully.")
                    .build();
        }

        throw new IllegalStateException("Overdue packages are auto-deleted, cannot delete manually.");
    }

    @Scheduled(cron = "0 0 0 * * ?") // every midnight
    public void autoDeleteExpiredPackages() {
        LocalDate today = LocalDate.now();
        List<GymPackage> expiredPackages = gymPackageRepository.findAll()
                .stream()
                .filter(pkg -> today.isAfter(pkg.getEndDate()))
                .toList();

        if (!expiredPackages.isEmpty()) {
            gymPackageRepository.deleteAll(expiredPackages);
            System.out.println("Deleted " + expiredPackages.size() + " expired packages.");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // every midnight
    @Transactional
    public void autoActivatePackages() {
        LocalDate today = LocalDate.now();

        List<GymPackage> packagesToActivate = gymPackageRepository.findAll()
                .stream()
                .filter(pkg -> pkg.getStartDate() != null
                        && pkg.getStartDate().isEqual(today)
                        && pkg.getStatus() != Status.ACTIVE)
                .toList();

        if (!packagesToActivate.isEmpty()) {
            packagesToActivate.forEach(pkg -> pkg.setStatus(Status.ACTIVE));
            gymPackageRepository.saveAll(packagesToActivate);
            System.out.println("Activated " + packagesToActivate.size() + " packages starting today.");
        }
    }


}
