package org._java_proj.gym_management_system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class Group extends MasterData {

    @Schema(description = "Unique identifier (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
    private String groupId;

    @Schema(description = "Name of the group", example = "Study Buddies")
    private String name;

    public Group(){}
}
