package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class BMI extends MasterData {
    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private String healthInfo;

    @Column(nullable = false)
    private String goal;

    @Column(name = "entity_id")
    private Long entityId;

    public BMI() {}

    public BMI(double weight, double height, String healthInfo, String goal, Long entityId) {
        this.weight = weight;
        this.height = height;
        this.healthInfo = healthInfo;
        this.goal = goal;
        this.entityId = entityId;
    }
}
