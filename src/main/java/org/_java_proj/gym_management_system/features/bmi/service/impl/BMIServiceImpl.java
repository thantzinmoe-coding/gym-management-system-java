package org._java_proj.gym_management_system.features.bmi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.bmi.dto.request.BMICreateRequest;
import org._java_proj.gym_management_system.features.bmi.dto.response.BMIDetailResponseDto;
import org._java_proj.gym_management_system.features.bmi.repository.BMIRepository;
import org._java_proj.gym_management_system.features.bmi.service.BMIService;
import org._java_proj.gym_management_system.model.BMI;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BMIServiceImpl implements BMIService {


    private final BMIRepository bmiRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse createBMI(BMICreateRequest createRequest) {

        BMI bmi = new BMI();
        bmi.setWeight(createRequest.getWeight());
        bmi.setHeight(createRequest.getHeight());
        bmi.setHealthInfo(createRequest.getHealthInfo());
        bmi.setGoal(createRequest.getGoal());
        bmi.setEntityId(createRequest.getEntityId());

        bmiRepository.save(bmi);


        return ApiResponse.builder().success(1).code(HttpStatus.CREATED.value())
                .data(bmi)
                .message("BMI created successfully").build();
    }

    @Override
    public ApiResponse getBMI(Long id) {
        BMI bmi = this.bmiRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("BMI did not found with id: " + id));

        BMIDetailResponseDto dto = modelMapper.map(bmi, BMIDetailResponseDto.class);
        dto.setBmiId(bmi.getId());
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("addressDetail", dto))
                .message("Address Detail")
                .build();
    }

    @Transactional
    public ApiResponse updateBMI(Long id, BMICreateRequest updateRequest) {

        BMI bmi = this.bmiRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("BMI did not found with id: " + id));

        bmi.setId(id);
        bmi.setWeight(updateRequest.getWeight());
        bmi.setHeight(updateRequest.getHeight());
        bmi.setHealthInfo(updateRequest.getHealthInfo());
        bmi.setGoal(updateRequest.getGoal());


        bmiRepository.save(bmi);


        BMICreateRequest updateRequestDto = modelMapper.map(bmi, BMICreateRequest.class);
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("Update BMI", updateRequestDto))
                .message("BMI updated successfully")
                .build();
    }

    @Override
    public ApiResponse deleteBMI(Long id) {
        BMI bmi = this.bmiRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("BMI did not found with id: " + id));
        bmi.setStatus(Status.INACTIVE);
        this.bmiRepository.save(bmi);
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(bmi)
                .message("BMI deleted successfully with id: "+id)
                .build();
    }

    @Override
    public PaginatedApiResponse<BMIDetailResponseDto> getBMIsDetail(Pageable pageable, Long userId) {
        Page<BMI> page = bmiRepository.findByEntityId(userId,pageable);
        List<BMIDetailResponseDto> data = page.getContent().stream()
                .map(bmi -> modelMapper.map(bmi, BMIDetailResponseDto.class))
                .toList();
        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<BMIDetailResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }
}
