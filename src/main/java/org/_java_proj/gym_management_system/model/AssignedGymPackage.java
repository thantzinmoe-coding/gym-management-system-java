package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class AssignedGymPackage extends MasterData {

    // In AssignedGymSchedule entity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id")
    private User trainer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignedGymPackage_id")
    private GymPackage assignedGymPackage;

    public AssignedGymPackage(){}


}
