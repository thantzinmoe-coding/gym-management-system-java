package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MemberStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    private Long bookingId;
    private Long memberId;
    private String memberName;
    private Long gymPackageId;
    private String gymPackageName;
    private String memberStatus;

    // Constructor for JPQL projection
    public BookingDetailResponse(Long bookingId, Long memberId, String memberName,
                                 Long gymPackageId, String gymPackageName, MemberStatus memberStatus) {
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.gymPackageId = gymPackageId;
        this.gymPackageName = gymPackageName;
        this.memberStatus = memberStatus != null ? memberStatus.toString() : null;
    }
}

