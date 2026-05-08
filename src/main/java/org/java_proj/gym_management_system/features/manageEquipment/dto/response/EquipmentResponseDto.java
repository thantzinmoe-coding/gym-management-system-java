package org.java_proj.gym_management_system.features.manageEquipment.dto.response;

import lombok.Data;
import org.java_proj.gym_management_system.common.constant.Condition;

import java.time.LocalDate;

@Data
public class EquipmentResponseDto {
    private String id; // Changed to String
    private String name;
    private LocalDate purchaseDate;
    private Condition equipmentCondition;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String equipmentPhoto;
}