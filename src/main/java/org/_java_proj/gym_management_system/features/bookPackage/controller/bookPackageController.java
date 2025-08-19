package org._java_proj.gym_management_system.features.bookPackage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.bookPackage.dto.request.BookPackageRequest;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageResponseDto;
import org._java_proj.gym_management_system.features.bookPackage.service.BookPackageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/book-package")
@RequiredArgsConstructor
@Tag(name = "Book package API", description = "Endpoints for booking package")
public class bookPackageController {
    private final BookPackageService bookPackageService;

    @PostMapping
    @Operation(
            summary = "Book package from member",
            description = "Member book to the gym package.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Book package request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "BookPackageRequest",
                                    summary = "Example request",
                                    value = "{ \"memberID\": \"1\", \"gymPackageID\": \"1\" }"
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Package booked successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> bookPackage(@RequestBody BookPackageRequest bookPackageRequest, HttpServletRequest request) {
        ApiResponse response = this.bookPackageService.bookPackage(bookPackageRequest);
        return ResponseUtils.buildResponse(request, response);
    }

    @PostMapping("{id}")
    @Operation(
            summary = "Member cancel package",
            description = "Member cancel the package with booking id.",
            parameters = {
                    @Parameter(name = "id", description = "Booking ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member canceled booking package successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking ID not found")
            }
    )
    public ResponseEntity<ApiResponse> cancelPackage(@PathVariable("id") Long id, HttpServletRequest request) {
        ApiResponse response = this.bookPackageService.cancelPackage(id);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping
    @Operation(
            summary = "List all booking packages",
            description = "Fetch all booking package entries",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking package not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<BookPackageResponseDto>> getAllBookPackages(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<BookPackageResponseDto> response = this.bookPackageService.getAllBookingPackages(pageable);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }
}
