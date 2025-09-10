package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MemberStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    private Long bookingId;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String phone;
    private String nrc;
    private LocalDate dob;
    private String gender;
    private Double weight;
    private Double height;
    private String address;
    private String goal;
    private Long gymPackageId;
    private String gymPackageName;
    private String memberStatus;

    // Constructor for JPQL projection
    public BookingDetailResponse(Long bookingId,
                                 Long memberId,
                                 String memberName,
                                 String memberEmail,
                                 String phone,
                                 String nrc,
                                 LocalDate dob,
                                 String gender,
                                 Double weight,
                                 Double height,
                                 String address,
                                 String goal,
                                 Long gymPackageId,
                                 String gymPackageName,
                                 MemberStatus memberStatus) {
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.phone = phone;
        this.nrc = nrc;
        this.dob = dob;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.address = address;
        this.goal = goal;
        this.gymPackageId = gymPackageId;
        this.gymPackageName = gymPackageName;
        this.memberStatus = memberStatus != null ? memberStatus.toString() : null;
    }
}
