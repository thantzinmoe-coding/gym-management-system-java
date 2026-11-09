package org.java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Data;
import org.java_proj.gym_management_system.common.constant.MemberStatus;

@Data
public class AcceptedBookingResponse {
    private Long bookingId;

    private Long memberId;

    private MemberStatus memberStatus;

    private Long gymPackageId;

    private String gymPackageName;

    private String description;
}
