package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class UserDetailInfo extends MasterData {

    private double weight;

    private double height;

    private String healthInfo;

    private String goal;

    private Integer experience;

    private String specialization;

    @Column(name = "entity_id")
    private Long entityId;

    public UserDetailInfo() {}

    public UserDetailInfo(double weight, double height, String healthInfo, String goal, Long entityId) {
        this.weight = weight;
        this.height = height;
        this.healthInfo = healthInfo;
        this.goal = goal;
        this.entityId = entityId;
    }
}
