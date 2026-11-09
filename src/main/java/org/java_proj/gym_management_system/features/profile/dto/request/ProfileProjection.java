package org.java_proj.gym_management_system.features.profile.dto.request;

import java.time.LocalDate;

public interface ProfileProjection {
    String getName();
    String getNrc();
    String getPhone();
    LocalDate getDob();
    String getGender();
    String getProfilePic();
    String getAddress();
}

