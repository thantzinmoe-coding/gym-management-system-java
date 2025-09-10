package org._java_proj.gym_management_system.features.bookPackage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.bookPackage.dto.request.BookPackageRequest;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageDetailResponseDto;
import org._java_proj.gym_management_system.features.bookPackage.service.BookPackageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("${api.base.path}/book-package")
@RequiredArgsConstructor
@Validated
@Tag(name = "Book Package Management", description = "APIs for managing gym package bookings")
public class BookPackageController {

    private final BookPackageService bookPackageService;

    @Operation(
            summary = "Book a gym package",
            description = "Create a new booking for a gym package. Only one active booking per member is allowed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Package booked successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = org._java_proj.gym_management_system.config.response.dto.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                    {
                      "success": 1,
                      "code": 201,
                      "message": "Booked package successfully",
                      "data": {
                        "BookPackage": {
                          "bookingPackageID": 1,
                          "memberID": 2,
                          "memberName": "John Doe",
                          "memberEmail": "john.doe@example.com",
                          "gymPackage": {
                            "id": 1,
                            "name": "Premium Package",
                            "description": "Full access to all facilities",
                            "price": 99.99
                          },
                          "status": "PENDING"
                        }
                      }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = """
                    {
                      "success": 0,
                      "code": 400,
                      "message": "Member with id 2 already has an active booking",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member or gym package not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Not Found Error",
                                    value = """
                    {
                      "success": 0,
                      "code": 404,
                      "message": "Member not found with id 999",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<org._java_proj.gym_management_system.config.response.dto.ApiResponse> bookPackage(
            @Parameter(description = "Booking request details", required = true)
            @Valid @RequestBody BookPackageRequest request) {

        org._java_proj.gym_management_system.config.response.dto.ApiResponse response = bookPackageService.bookPackage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Cancel a booking",
            description = "Cancel an existing booking by ID. After cancellation, the member can book another package."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking cancelled successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                    {
                      "success": 1,
                      "code": 200,
                      "message": "Booking package canceled successfully. You can now book a new package.",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking already cancelled or completed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Already Cancelled Error",
                                    value = """
                    {
                      "success": 0,
                      "code": 400,
                      "message": "Booking is already cancelled",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Not Found Error",
                                    value = """
                    {
                      "success": 0,
                      "code": 404,
                      "message": "Booking not found with id 999",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<org._java_proj.gym_management_system.config.response.dto.ApiResponse> cancelPackage(
            @Parameter(description = "Booking ID to cancel", required = true, example = "1")
            @PathVariable @Positive(message = "Booking ID must be positive") Long id) {

        org._java_proj.gym_management_system.config.response.dto.ApiResponse response = bookPackageService.cancelPackage(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}/detail")
    @Operation(
            summary = "Get all bookings by member id.",
            description = "Get bookings detail by its member ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bookings not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<BookPackageDetailResponseDto>> getBookingDetail(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable("id") Long memberId, HttpServletRequest request)
    {
        Pageable pageable = PageRequest.of(page, size);
        final PaginatedApiResponse<BookPackageDetailResponseDto> response = this.bookPackageService.getBookingDetail(memberId, pageable);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get booking package detail",
            description = "Get booking package detail by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "Booking ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "booking package details retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
            }
    )
    public ResponseEntity<org._java_proj.gym_management_system.config.response.dto.ApiResponse> getBookingById(@PathVariable("id") Long id, HttpServletRequest request) {
        org._java_proj.gym_management_system.config.response.dto.ApiResponse response = this.bookPackageService.getBookingById(id);
        return ResponseUtils.buildResponse(request, response);
    }


    @GetMapping("{packageId}/get-by-package-id")
    @Operation(
            summary = "Get all bookings by package id.",
            description = "Get bookings detail by its package ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bookings not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<BookPackageDetailResponseDto>> getBookingByPackageId(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable("packageId") Long packageId, HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedApiResponse<BookPackageDetailResponseDto> response = this.bookPackageService.getAllBookingsByPackage(packageId, pageable);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @Operation(
            summary = "Get distinct user count by trainer",
            description = "Returns the number of unique users who have booked a package assigned to the trainer"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/trainer/{trainerId}/user-count")
    public ResponseEntity<Long> getUserCountByTrainer(
            @PathVariable Long trainerId) {
        Long count = bookPackageService.getUserCountByTrainer(trainerId);
        return ResponseEntity.ok(count);
    }
}

