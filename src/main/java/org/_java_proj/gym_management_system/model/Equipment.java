package org._java_proj.gym_management_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.Condition; // Assuming this enum exists
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Equipment extends MasterData {
    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private LocalDate purchase_date;

    // *** Renamed 'condition' to 'equipmentCondition' ***
    @Column(nullable = false)
    private Condition equipmentCondition; // Renamed field

    @Column(nullable = false)
    private LocalDate lastMaintenanceDate;

    @Column(nullable = false)
    private LocalDate nextMaintenanceDate;

    public Equipment() {}

    public Equipment(String name, LocalDate purchase_date, Condition equipmentCondition, LocalDate lastMaintenanceDate, LocalDate nextMaintenanceDate) { // Updated constructor parameter
        this.name = name;
        this.purchase_date = purchase_date;
        this.equipmentCondition = equipmentCondition; // Updated setter call
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}
