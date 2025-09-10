package org._java_proj.gym_management_system.features.bookPackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MemberStatus;

import java.time.LocalDate;

@Data
public class BookPackageDetailResponseDto {
    private Long bookPackageId;

    private String bookingDate;

    private MemberStatus memberStatus;

    private String gymPackageName;

    private String gymPackageDescription;

    private double price;

    private LocalDate startDate;

    private LocalDate endDate;

    private String duration;

    private String startTime;

    private String endTime;

    private String day;

}
