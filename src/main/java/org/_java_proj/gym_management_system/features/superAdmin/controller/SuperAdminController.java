package org._java_proj.gym_management_system.features.superAdmin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.BookingDetailResponse;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.SuperAdminDashBoardResponse;
import org._java_proj.gym_management_system.features.superAdmin.service.SuperAdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org._java_proj.gym_management_system.features.superAdmin.dto.request.GetAllTrainersRequest;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.TrainerResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/super_admin")
@Tag(name = "User API", description = "Endpoints for managing users")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @DeleteMapping("/user/delete/{id}")
    @Operation(
            summary = "Delete a user",
            description = "Change user status to Inactive.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User is successfully deleted"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invalid user")
            }
    )
    public ResponseEntity<ApiResponse> deletedUser(
            @PathVariable(name = "id") final Long id, HttpServletRequest request) {

        final ApiResponse response = superAdminService.deleteById(id);
        return ResponseUtils.buildResponse(request, response);
    }


    @GetMapping("/all-users")
    @Operation(
            summary = "Fetching Users",
            description = "Fetching Users with keywords - name,email,phone,address,role and status, returning pagination",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users are fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invalid Request")
            }
    )
    public ResponseEntity<PaginatedApiResponse<SuperAdminDashBoardResponse>> getAllUsers(
            @Parameter(description = "Search keyword")
            @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "User role to filter")
            @RequestParam(value = "role", required = false) String role,
            @Parameter(description = "User status to filter")
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Page number (starts from 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<SuperAdminDashBoardResponse> response = superAdminService.getAllUsersPaginated(keyword, role, status, pageable);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @PostMapping("{bookingId}")
    @Operation(
            summary = "Admin accept the booking package",
            description = "Admin accept the booking package that is pending from member.",
            parameters = {
                    @Parameter(name = "id", description = "Booking ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admin accepted booking successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> acceptBooking(@PathVariable("bookingId") Long id, HttpServletRequest request) {
        ApiResponse response = this.superAdminService.acceptBooking(id);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("/get-all-bookings")
    @Operation(
            summary = "Fetching All Bookings",
            description = "Fetching Bookings with optional filters - member name, package name, member status, date range, returning pagination",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Bookings are fetched successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Invalid Request"
                    )
            }
    )
    public ResponseEntity<PaginatedApiResponse<BookingDetailResponse>> getAllPendingBookings(
            @Parameter(description = "Search keyword for member name or package name")
            @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "Filter by member ID")
            @RequestParam(value = "memberId", required = false) Long memberId,
            @Parameter(description = "Filter by package ID")
            @RequestParam(value = "packageId", required = false) Long packageId,
            @Parameter(description = "Filter by member status")
            @RequestParam(value = "memberStatus", required = false) MemberStatus memberStatus,
            @Parameter(description = "Page number (starts from 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<BookingDetailResponse> response =
                superAdminService.getAllBookings(keyword, memberId, packageId,memberStatus, pageable);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @PatchMapping("{trainerId}")
    @Operation(
            summary = "Admin accept the gym trainer",
            description = "Admin accept the gym trainer who is pending to use the system.",
            parameters = {
                    @Parameter(name = "trainerId", description = "Trainer ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admin accepted gym trainer successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> acceptTrainer(@PathVariable("trainerId") Long trainerId, HttpServletRequest request) {
        ApiResponse response = this.superAdminService.acceptTrainer(trainerId);
        return ResponseUtils.buildResponse(request, response);
    }
    @GetMapping("/all-trainers")
    @Operation(summary = "Get all trainers (paginated)")
    public ResponseEntity<PaginatedApiResponse<TrainerResponseDto>> getAllTrainers(
            GetAllTrainersRequest request,
            @Parameter(description = "Page number (starts from 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest servletRequest) {

        Pageable pageable = PageRequest.of(page, size);
        PaginatedApiResponse<TrainerResponseDto> response = superAdminService.getAllTrainers(request, pageable);
        return ResponseUtils.buildPaginatedResponse(servletRequest, response);
    }

    @GetMapping("/all-active-trainers")
    @Operation(summary = "Get all active trainers (paginated)")
    public ResponseEntity<PaginatedApiResponse<TrainerResponseDto>> getAllActiveTrainers(
            @Parameter(description = "Page number (starts from 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest servletRequest) {

        Pageable pageable = PageRequest.of(page, size);
        PaginatedApiResponse<TrainerResponseDto> response = superAdminService.getAllActiveTrainers(pageable);
        return ResponseUtils.buildPaginatedResponse(servletRequest, response);
    }
}

