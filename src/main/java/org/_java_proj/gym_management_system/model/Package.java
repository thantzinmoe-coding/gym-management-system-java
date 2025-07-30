package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class Package extends MasterData {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String duration;

    @OneToOne(mappedBy = "aPackage", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Schedule schedule;

    @OneToOne(mappedBy = "aPackage", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Booking booking;

    public Package() {}
}
