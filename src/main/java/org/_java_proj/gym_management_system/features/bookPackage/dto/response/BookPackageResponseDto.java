package org._java_proj.gym_management_system.features.bookPackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.features.managePackage.dto.response.ScheduleSummaryDto;
import org._java_proj.gym_management_system.model.Schedule;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookPackageResponseDto {

    private Long bookingPackageID;

    private String bookingDate;

    private String cancelDate;

    private Long memberID;

    private String memberName;

    private String memberEmail;

    private String gymPackageName;

    private String gymPackageDescription;

    private double price;

    private String duration;

    private List<ScheduleSummaryDto> schedules;

    private MemberStatus status;

}
