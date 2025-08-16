package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class Schedule extends MasterData {

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column(nullable = false)
    private String day;

    // ✅ Many schedules belong to one package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gymPackage_id", nullable = false)
    private GymPackage gymPackage;

    @OneToOne(mappedBy = "assignedGymSchedule", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private AssignedGymSchedule assignedGymSchedule;

    public Schedule() {}
}

