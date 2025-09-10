package org._java_proj.gym_management_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private String experience;

    private String specialization;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    public UserDetailInfo() {}

    public UserDetailInfo(double weight, double height, String healthInfo, String goal, final User user) {
        this.weight = weight;
        this.height = height;
        this.healthInfo = healthInfo;
        this.goal = goal;
        this.user = user;
    }
}
