package org._java_proj.gym_management_system.features.bmi.controller;

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
import org._java_proj.gym_management_system.features.bmi.dto.request.BMICreateRequest;
import org._java_proj.gym_management_system.features.bmi.dto.response.BMIDetailResponseDto;
import org._java_proj.gym_management_system.features.bmi.service.BMIService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/auth/bmi")
@RequiredArgsConstructor
@Tag(name = "BMI API", description = "Endpoints for managing BMI")
public class BMIController {

    private final BMIService bmiService;
    @PostMapping
    @Operation(
            summary = "Create a new bmi",
            description = "Registers a new bmi in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "BMI creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BMICreateRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "BMI created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> createBMI(
            @Valid @RequestBody final BMICreateRequest addressCreateRequest,
            final HttpServletRequest request
    ){
        final ApiResponse response = this.bmiService.createBMI(addressCreateRequest);
        return ResponseUtils.buildResponse(request,response);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get BMI detail",
            description = "Get BMI detail by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "BMI ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "BMI details retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "BMI not found")
            }
    )
    public ResponseEntity<ApiResponse> addressDetail(@PathVariable(name="id") Long id, HttpServletRequest request) {
        final ApiResponse response = this.bmiService.getBMI(id);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("/getAll/{userId}")
    @Operation(
            summary = "Get all BMIs by user id.",
            description = "Get BMIs detail by its user ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "BMIs not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<BMIDetailResponseDto>> bmiDetails(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable(name="userId") Long userId, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<BMIDetailResponseDto> response = this.bmiService.getBMIsDetail(pageable,userId);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a BMI",
            description = "Updates the details of an existing BMI.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "BMI update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BMICreateRequest.class))
            ),
            parameters = {
                    @Parameter(name = "id", description = "Address ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "BMI updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "BMI not found")
            }
    )
    public ResponseEntity<ApiResponse> updateBMI(@Valid @RequestBody BMICreateRequest bmiUpdateRequestDto,
                                                     @PathVariable(name="id") Long id,
                                                     HttpServletRequest request) {
        final ApiResponse response = this.bmiService.updateBMI(id, bmiUpdateRequestDto);
        return ResponseUtils.buildResponse(request, response);

    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a BMI",
            description = "Deletes a BMI from the system by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "BMI ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "BMI deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "BMI not found")
            }
    )
    public ResponseEntity<ApiResponse> deleteBMI(@PathVariable(name="id") Long id, HttpServletRequest request) {
        final ApiResponse response = this.bmiService.deleteBMI(id);
        return ResponseUtils.buildResponse(request, response);
    }
}
