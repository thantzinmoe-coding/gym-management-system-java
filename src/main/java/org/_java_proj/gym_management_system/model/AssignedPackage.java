package org._java_proj.gym_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class AssignedPackage extends MasterData {


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false, unique = true)
    private User trainer;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false, unique = true)
    private GymPackage assignedPackage;




}
