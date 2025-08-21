package org._java_proj.gym_management_system.features.managePackage.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org._java_proj.gym_management_system.model.GymPackage;

import java.util.Arrays;
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

        gymPackage.setName(request.getName());
        gymPackage.setDescription(request.getDescription());
        gymPackage.setPrice(request.getPrice());
        gymPackage.setDuration(request.getDuration());

        gymPackageRepository.save(gymPackage);

        GymPackageResponseDto dto = modelMapper.map(gymPackage, GymPackageResponseDto.class);

        return ApiResponse.builder().success(1).code(HttpStatus.CREATED.value())
                .data(Map.of("Package: ", dto))
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
    public ApiResponse updateGymPackage(Long id, GymPackageUpdateRequest request) {
        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Gym package not found with this ID: "+ id));

        Optional.ofNullable(request.getName()).ifPresent(gymPackage::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(gymPackage::setDescription);
        Optional.of(request.getPrice()).ifPresent(gymPackage::setPrice);
        Optional.ofNullable(request.getDuration()).ifPresent(gymPackage::setDuration);

        gymPackageRepository.save(gymPackage);

        GymPackageResponseDto dto = modelMapper.map(gymPackage, GymPackageResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("Updated gym package",dto))
                .message("Gym package updated successfully")
                .build();
    }



    public ApiResponse deleteGymPackage(Long id) {
        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Gym package not found with this ID: "+ id));

        gymPackage.delete();
        this.gymPackageRepository.save(gymPackage);

        return ApiResponse.builder().success(1)
                .code(HttpStatus.OK.value())
                .message("Gym package Deleted successfully.").build();
    }

}
