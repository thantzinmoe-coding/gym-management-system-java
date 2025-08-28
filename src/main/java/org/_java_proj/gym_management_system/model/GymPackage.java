package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.GymPackageType;
import org._java_proj.gym_management_system.common.converter.GymPackageTypeConverter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class GymPackage extends MasterData {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    @Convert(converter = GymPackageTypeConverter.class)
    private GymPackageType gymPackageType;

    @Column(nullable = false)
    private String duration;

    @OneToMany(mappedBy = "gymPackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "gymPackage", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Booking> booking = new ArrayList<>();

    @OneToMany(mappedBy = "assignedGymPackage", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<AssignedGymPackage> assignedGymPackage = new ArrayList<>();

    public GymPackage() {}
}
