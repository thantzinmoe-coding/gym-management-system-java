package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
// Using @Data for simplicity, which generates setters and getters
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.Condition;
import org._java_proj.gym_management_system.common.converter.ConditionConverter;
import org._java_proj.gym_management_system.common.entity.MasterData; // Assuming this is a valid base class

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Equipment extends MasterData { // Inherits from MasterData

    @Column(unique = true, nullable = false) // Assuming name is unique and required
    private String name;

    @Column(nullable = false)
    private LocalDate purchaseDate; // Corrected from purchase_date

    @Column(nullable = false)
    @Convert(converter = ConditionConverter.class)
    private Condition equipmentCondition; // Renamed field

    @Column(nullable = false)
    private LocalDate lastMaintenanceDate; // Corrected from lastMaintenanceDate

    @Column(nullable = false)
    private LocalDate nextMaintenanceDate; // Corrected from nextMaintenanceDate

    // Constructor matching the fields (adjust if you have a default constructor requirement or different fields)
    public Equipment() {} // Default constructor

    // If you want to add a constructor that initializes these fields:
    public Equipment(String name, LocalDate purchaseDate, Condition equipmentCondition, LocalDate lastMaintenanceDate, LocalDate nextMaintenanceDate) {
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.equipmentCondition = equipmentCondition;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}
