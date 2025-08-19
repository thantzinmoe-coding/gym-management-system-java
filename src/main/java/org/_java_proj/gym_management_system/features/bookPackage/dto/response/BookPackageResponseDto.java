package org._java_proj.gym_management_system.features.bookPackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.model.GymPackage;
import org._java_proj.gym_management_system.model.Schedule;

import java.util.List;

@Data
public class BookPackageResponseDto {

    private Long bookingPackageID;

    private Long memberID;

    private String memberName;

    private GymPackage gymPackage;

    private List<Schedule> schedule;

    private String memberEmail;

    private MemberStatus status;
}
