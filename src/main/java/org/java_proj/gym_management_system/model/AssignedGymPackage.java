package org.java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class AssignedGymPackage extends MasterData {

    // In AssignedGymSchedule entity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id")
    private User trainer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_package_id", unique = true) // enforce uniqueness at DB level
    private GymPackage gymPackage;

    public AssignedGymPackage(){}


}
