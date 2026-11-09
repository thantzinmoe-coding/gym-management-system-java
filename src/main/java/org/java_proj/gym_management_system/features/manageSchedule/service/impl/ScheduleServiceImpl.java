package org.java_proj.gym_management_system.features.manageSchedule.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org.java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org.java_proj.gym_management_system.features.manageSchedule.dto.request.ScheduleBulkRequest;
import org.java_proj.gym_management_system.features.manageSchedule.dto.request.ScheduleUpdateRequest;
import org.java_proj.gym_management_system.features.manageSchedule.dto.response.ScheduleResponseDto;
import org.java_proj.gym_management_system.features.manageSchedule.repository.ScheduleRepository;
import org.java_proj.gym_management_system.features.manageSchedule.service.ScheduleService;
import org.java_proj.gym_management_system.model.GymPackage;
import org.java_proj.gym_management_system.model.Schedule;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final GymPackageRepository gymPackageRepository;

    @Override
    @Transactional
    public List<ScheduleResponseDto> createSchedules(ScheduleBulkRequest request) {
        GymPackage gymPackage = gymPackageRepository.findById(request.getGymPackageId())
                .orElseThrow(() -> new RuntimeException("GymPackage not found"));

        List<Schedule> schedules = request.getSchedules().stream()
                .map(req -> {
                    Schedule schedule = new Schedule();
                    schedule.setStartTime(req.getStartTime());
                    schedule.setEndTime(req.getEndTime());
                    schedule.setDay(req.getDay());
                    schedule.setGymPackage(gymPackage);
                    return schedule;
                }).toList();

        List<Schedule> saved = scheduleRepository.saveAll(schedules);

        return saved.stream().map(s -> {
            ScheduleResponseDto resp = new ScheduleResponseDto();
            resp.setId(s.getId());
            resp.setStartTime(s.getStartTime());
            resp.setEndTime(s.getEndTime());
            resp.setDay(s.getDay());
            resp.setPackageId(s.getGymPackage().getId());
            resp.setPackageName(s.getGymPackage().getName());
            return resp;
        }).toList();
    }

    @Override
    public ApiResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id " + id));

        ScheduleResponseDto dto = modelMapper.map(schedule, ScheduleResponseDto.class);
        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .data(Map.of("Schedule", dto))
                .message("Schedule detail retrieved successfully.")
                .build();
    }

    @Override
    public PaginatedApiResponse<ScheduleResponseDto> getAllSchedules(Pageable pageable) {
        Page<Schedule> page = scheduleRepository.findAllSchedules(pageable);
        List<ScheduleResponseDto> data = page.getContent()
                .stream()
                .map(s -> modelMapper.map(s, ScheduleResponseDto.class))
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<ScheduleResponseDto>builder()
                .success(1).code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateSchedule(Long id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id " + id));

        schedule.setDay(request.getDay());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        // TODO: update trainer & package as well

        scheduleRepository.save(schedule);

        ScheduleResponseDto dto = modelMapper.map(schedule, ScheduleResponseDto.class);
        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .data(Map.of("Updated Schedule", dto))
                .message("Schedule updated successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id " + id));

        scheduleRepository.delete(schedule);
        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .message("Schedule deleted successfully.")
                .build();
    }
}
