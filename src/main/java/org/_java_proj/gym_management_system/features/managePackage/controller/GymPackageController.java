package org._java_proj.gym_management_system.features.managePackage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.bmi.dto.request.BMICreateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.request.GymPackageCreateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.request.GymPackageUpdateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.response.GymPackageResponseDto;
import org._java_proj.gym_management_system.features.managePackage.service.GymPackageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/gym-package")
@RequiredArgsConstructor
@Tag(name = "Gym Package API", description = "Endpoints for managing gym package")
public class GymPackageController {

    private final GymPackageService gymPackageService;

    @PostMapping
    @Operation(
            summary = "Create a package",
            description = "Create a package for the gym management system",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Gym package request with JSON",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GymPackageCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Example package",
                                    value = """
                                               {
                                                  "name": "Premium package",
                                                  "description": "premium package for gym management system",
                                                  "price": 70000,
                                                  "duration": "1 month"
                                               }
                                             """
                            )

                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Package created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "Success Response",
                                            value = """
                                                    {
                                                      "success": 1,
                                                      "code": 200,
                                                      "meta": {
                                                        "endpoint": "/api/v1/gym-package",
                                                        "method": "POST"
                                                      },
                                                      "data": {
                                                        "name": "Premium package",
                                                        "description": "premium package for gym management system",
                                                        "price": 70000,
                                                        "duration": "1 month",
                                                      },
                                                      "message": "Package created successfully"
                                                    }"""
                                    )
                            )


                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "Invalid input or error creating package",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "Error response example",
                                            value = """
                                                    {
                                                      "success": 0,
                                                      "code": 400,
                                                      "meta": null,
                                                      "data": null,
                                                      "message": "Invalid input or error creating package"
                                                    }"""
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse> createGymPackage(@RequestBody GymPackageCreateRequest gymPackageCreateRequest, HttpServletRequest request) {
        ApiResponse response = this.gymPackageService.createGymPackage(gymPackageCreateRequest);
        return ResponseUtils.buildResponse(request, response);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get Gym Package detail",
            description = "Get Gym Package detail by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "Gym Package ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gym Package details retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym Package not found")
            }
    )
    public ResponseEntity<ApiResponse> getGymPackageById(@PathVariable("id") Long id, HttpServletRequest request) {
        ApiResponse response = this.gymPackageService.getGymPackageById(id);

        return ResponseUtils.buildResponse(request, response);
    }


    @GetMapping
    @Operation(
            summary = "Get all Gym packages.",
            description = "Get gym package detail.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym package not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<GymPackageResponseDto>> getAllGymPackages(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<GymPackageResponseDto> response = this.gymPackageService.getAllGymPackages(pageable);

        return ResponseUtils.buildPaginatedResponse(request, response);
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Update a Gym package",
            description = "Updates the details of an existing gym package.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Gym package update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GymPackageUpdateRequest.class))
            ),
            parameters = {
                    @Parameter(name = "id", description = "Gym package ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gym package updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym package not found")
            }
    )
    public ResponseEntity<ApiResponse> updateGymPackage(@PathVariable("id") Long id, GymPackageUpdateRequest gymPackageUpdateRequest, HttpServletRequest request) {
        ApiResponse response = this.gymPackageService.updateGymPackage(id, gymPackageUpdateRequest);
        return ResponseUtils.buildResponse(request, response);
    }


    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a Gym package",
            description = "Deletes a Gym package from the system by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "Gym package ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gym package deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym package not found")
            }
    )
    public ResponseEntity<ApiResponse> deleteGymPackage(@PathVariable("id") Long id, HttpServletRequest request) {
        ApiResponse response = this.gymPackageService.deleteGymPackage(id);
        return ResponseUtils.buildResponse(request, response);
    }

}
