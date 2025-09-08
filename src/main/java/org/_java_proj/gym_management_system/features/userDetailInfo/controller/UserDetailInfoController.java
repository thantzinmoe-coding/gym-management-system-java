package org._java_proj.gym_management_system.features.userDetailInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.request.UserDetailInfoCreateRequest;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.response.UserDetailInfoResponseDto;
import org._java_proj.gym_management_system.features.userDetailInfo.service.UserDetailInfoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/auth/user-detail-info")
@RequiredArgsConstructor
@Tag(name = "User detail info API", description = "Endpoints for managing user detail Info")
public class UserDetailInfoController {

    private final UserDetailInfoService userDetailInfoService;
    @PostMapping("/{userId}")
    @Operation(
            summary = "Create a new user detail info",
            description = "Registers a new user detail info in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User detail info creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDetailInfoCreateRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User detail info created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> createUserDetailInfo(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody final UserDetailInfoCreateRequest userDetailInfoCreateRequest,
            final HttpServletRequest request
    ){
        final ApiResponse response = this.userDetailInfoService.createUserDetailInfo(userId, userDetailInfoCreateRequest);
        return ResponseUtils.buildResponse(request,response);
    }


    @GetMapping("{userId}")
    @Operation(
            summary = "Get user detail info detail",
            description = "Get user detail info detail by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "User detail info ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User detail infos retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User detail info not found")
            }
    )
    public ResponseEntity<ApiResponse> getUserDetailInfoByUserID(@PathVariable(name="userId") Long id, HttpServletRequest request) {
        final ApiResponse response = this.userDetailInfoService.getUserDetailInfoByUserId(id);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("/getAll/{userId}")
    @Operation(
            summary = "Get all user detail infos by user id.",
            description = "Get user detail infos detail by its user ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User detail infos not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<UserDetailInfoResponseDto>> userDetailInfoDetails(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable(name="userId") Long userId, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<UserDetailInfoResponseDto> response = this.userDetailInfoService.getUserDetailInfos(pageable,userId);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a user detail info",
            description = "Updates the details of an existing user detail info.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User detail info update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDetailInfoCreateRequest.class))
            ),
            parameters = {
                    @Parameter(name = "id", description = "User detail info ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User detail info updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User detail info not found")
            }
    )
    public ResponseEntity<ApiResponse> updateUserDetailInfo(@Valid @RequestBody UserDetailInfoCreateRequest bmiUpdateRequestDto,
                                                     @PathVariable(name="id") Long id,
                                                     HttpServletRequest request) {
        final ApiResponse response = this.userDetailInfoService.updateUserDetailInfo(id, bmiUpdateRequestDto);
        return ResponseUtils.buildResponse(request, response);

    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user detail info",
            description = "Deletes a user detail info from the system by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "User detail info ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "user detail info deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User detail info not found")
            }
    )
    public ResponseEntity<ApiResponse> deleteUserDetailInfo(@PathVariable(name="id") Long id, HttpServletRequest request) {
        final ApiResponse response = this.userDetailInfoService.deleteUserDetailInfo(id);
        return ResponseUtils.buildResponse(request, response);
    }
}
