package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Data; // Using @Data for simplicity, which generates setters and getters
import org._java_proj.gym_management_system.common.entity.MasterData; // Assuming this is a valid base class

import java.time.LocalDate;

@Entity
@Data // Lombok generates getters, setters, equals, hashCode, and toString
@Table(name = "equipments") // Table name
public class Equipment extends MasterData { // Inherits from MasterData

    @Column(unique = true, nullable = false) // Assuming name is unique and required
    private String name;

    @Column(nullable = false)
    private LocalDate purchaseDate; // Corrected from purchase_date

    // Renamed 'condition' to 'equipmentCondition' to avoid SQL reserved keyword conflict
    // If 'Condition' was meant to be an enum, you'd use 'private Condition equipmentCondition;'
    // For simplicity, let's assume it's an integer status.
    @Column(nullable = false)
    private Integer equipmentCondition; // Renamed field

    @Column(nullable = false)
    private LocalDate lastMaintenanceDate; // Corrected from lastMaintenanceDate

    @Column(nullable = false)
    private LocalDate nextMaintenanceDate; // Corrected from nextMaintenanceDate

    // Constructor matching the fields (adjust if you have a default constructor requirement or different fields)
    public Equipment() {} // Default constructor

    // If you want to add a constructor that initializes these fields:
    public Equipment(String name, LocalDate purchaseDate, Integer equipmentCondition, LocalDate lastMaintenanceDate, LocalDate nextMaintenanceDate) {
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.equipmentCondition = equipmentCondition;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}
