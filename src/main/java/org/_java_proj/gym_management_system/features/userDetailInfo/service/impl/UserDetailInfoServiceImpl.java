package org._java_proj.gym_management_system.features.userDetailInfo.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.request.UserDetailInfoCreateRequest;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.response.UserDetailInfoResponseDto;
import org._java_proj.gym_management_system.features.userDetailInfo.repository.UserDetailInfoRepository;
import org._java_proj.gym_management_system.features.userDetailInfo.service.UserDetailInfoService;
import org._java_proj.gym_management_system.model.UserDetailInfo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDetailInfoServiceImpl implements UserDetailInfoService {


    private final UserDetailInfoRepository userDetailInfoRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse createUserDetailInfo(UserDetailInfoCreateRequest createRequest) {

        if(this.userDetailInfoRepository.existsByEntityIdAndStatus(createRequest.getEntityId(), Status.ACTIVE)) {
            throw new EntityCreationException("User detail info already exists by entity id " + createRequest.getEntityId());
        }

        UserDetailInfo userDetailInfo = getUserDetailInfo(createRequest);

        userDetailInfoRepository.save(userDetailInfo);


        return ApiResponse.builder().success(1).code(HttpStatus.CREATED.value())
                .data(userDetailInfo)
                .message("User detail info created successfully").build();
    }

    private static UserDetailInfo getUserDetailInfo(UserDetailInfoCreateRequest createRequest) {
        UserDetailInfo userDetailInfo = new UserDetailInfo();
        userDetailInfo.setWeight(createRequest.getWeight());
        userDetailInfo.setHeight(createRequest.getHeight());
        userDetailInfo.setGoal(createRequest.getGoal());
        userDetailInfo.setExperience(createRequest.getExperience());
        userDetailInfo.setSpecialization(createRequest.getSpecialization());
        userDetailInfo.setEntityId(createRequest.getEntityId());
        return userDetailInfo;
    }

    @Override
    public ApiResponse getUserDetailInfoByUserId(Long userId) {
        UserDetailInfo info = this.userDetailInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("No user detail info found with user Id "+ userId));

        UserDetailInfoResponseDto dto = modelMapper.map(info, UserDetailInfoResponseDto.class);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .data(dto).message("Retrieved user detail info by user id").build();
    }

    @Transactional
    public ApiResponse updateUserDetailInfo(Long id, UserDetailInfoCreateRequest updateRequest) {

        UserDetailInfo userDetailInfo = this.userDetailInfoRepository.findFirstByEntityIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("User detail info did not found with id: " + id));

        userDetailInfo.setWeight(updateRequest.getWeight());
        userDetailInfo.setHeight(updateRequest.getHeight());
        userDetailInfo.setGoal(updateRequest.getGoal());
        userDetailInfo.setExperience(updateRequest.getExperience());
        userDetailInfo.setSpecialization(updateRequest.getSpecialization());


        userDetailInfoRepository.save(userDetailInfo);


        UserDetailInfoCreateRequest updateRequestDto = modelMapper.map(userDetailInfo, UserDetailInfoCreateRequest.class);
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("UpdatedUserDetailInfo", updateRequestDto))
                .message("User detail info updated successfully")
                .build();
    }

    @Override
    public ApiResponse deleteUserDetailInfo(Long id) {
        UserDetailInfo userDetailInfo = this.userDetailInfoRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("User detail info did not found with id: " + id));
        userDetailInfo.delete();
        this.userDetailInfoRepository.save(userDetailInfo);
        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(userDetailInfo)
                .message("User detail info deleted successfully with id: "+id)
                .build();
    }

    @Override
    public PaginatedApiResponse<UserDetailInfoResponseDto> getUserDetailInfos(Pageable pageable, Long userId) {
        Page<UserDetailInfo> page = userDetailInfoRepository.findByEntityId(userId,pageable);
        List<UserDetailInfoResponseDto> data = page.getContent().stream()
                .map(userDetailInfo -> modelMapper.map(userDetailInfo, UserDetailInfoResponseDto.class))
                .toList();
        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<UserDetailInfoResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }
}
