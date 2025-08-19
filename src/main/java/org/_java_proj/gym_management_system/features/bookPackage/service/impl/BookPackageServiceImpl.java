package org._java_proj.gym_management_system.features.bookPackage.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.bookPackage.dto.request.BookPackageRequest;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageResponseDto;
import org._java_proj.gym_management_system.features.bookPackage.repository.BookPackageRepository;
import org._java_proj.gym_management_system.features.bookPackage.service.BookPackageService;
import org._java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.Booking;
import org._java_proj.gym_management_system.model.GymPackage;
import org._java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookPackageServiceImpl implements BookPackageService {
    private final BookPackageRepository bookPackageRepository;
    private final UserRepository userRepository;
    private final GymPackageRepository gymPackageRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse bookPackage(BookPackageRequest request) {

        User member = this.userRepository.findById(request.getMemberID())
                .orElseThrow(()-> new EntityNotFoundException("Member not found with id "+ request.getMemberID()));

        if (Objects.equals(member.getRole().getName(), "TRAINER")) {
            throw new EntityNotFoundException("Member not found with id "+ request.getMemberID());
        }

        GymPackage gymPackage = this.gymPackageRepository.findByIdAndStatus(request.getGymPackageID(), Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("Gym package not found with id "+ request.getGymPackageID()));

        if(bookPackageRepository.existsById(request.getMemberID())){
            throw new EntityCreationException("Member with id "+ request.getMemberID()+" already booked a package");
        }

        Booking booking = new Booking();
        booking.setMember(member);
        booking.setGymPackage(gymPackage);
        booking.setMemberStatus(MemberStatus.PENDING);

        bookPackageRepository.save(booking);

        BookPackageResponseDto dto = new BookPackageResponseDto();
        dto.setBookingPackageID(booking.getId());
        dto.setMemberID(booking.getMember().getId());
        dto.setMemberName(booking.getMember().getProfile().getName());
        dto.setMemberEmail(booking.getMember().getEmail());
        dto.setGymPackage(gymPackage);
        dto.setSchedule(gymPackage.getSchedules());
        dto.setStatus(booking.getMemberStatus());

        return ApiResponse.builder().success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("BookPackage", dto))
                .message("Booked package successfully").build();

    }

    @Override
    public ApiResponse cancelPackage(Long id) {

        Booking booking = bookPackageRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Booking not found with id "+ id));

        booking.setMemberStatus(MemberStatus.CANCELLED);
        bookPackageRepository.save(booking);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .message("Booking package canceled successfully").build();
    }

    @Override
    public PaginatedApiResponse<BookPackageResponseDto> getAllBookingPackages(Pageable pageable) {
        Page<Booking> page = bookPackageRepository.getAllBookings(pageable);

        List<BookPackageResponseDto> data = page.getContent().stream()
                .map(booking -> modelMapper.map(booking, BookPackageResponseDto.class))
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<BookPackageResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }

}
