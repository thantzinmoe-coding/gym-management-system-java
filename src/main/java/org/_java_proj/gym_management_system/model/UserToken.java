package org._java_proj.gym_management_system.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class UserToken extends MasterData {
    private String token;
    private String username;
}
