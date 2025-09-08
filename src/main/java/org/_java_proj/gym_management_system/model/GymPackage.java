package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.GymPackageType;
import org._java_proj.gym_management_system.common.converter.GymPackageTypeConverter;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDate;
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
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String duration;

    @OneToMany(mappedBy = "gymPackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "gymPackage", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Booking> booking = new ArrayList<>();

    @OneToOne(mappedBy = "gymPackage", cascade = CascadeType.ALL, orphanRemoval = true) // or use OneToMany if you're using versioning/history
    private AssignedGymPackage assignedGymPackage;

    public GymPackage() {}
}
