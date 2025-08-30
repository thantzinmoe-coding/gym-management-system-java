package org._java_proj.gym_management_system.features.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.features.profile.dto.response.ProfileResponseDto;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.response.UserDetailInfoResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String email;
    private String roleName;
    private String token;
    private String refreshToken;
    private ProfileResponseDto profile;
    private UserDetailInfoResponseDto userDetailInfoResponse;
}
