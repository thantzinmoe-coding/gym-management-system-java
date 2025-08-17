package org._java_proj.gym_management_system.features.manageEquipment.dto.request;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.Condition;

import java.time.LocalDate;

@Data
public class EquipmentCreateRequest {
    private String name;
    private LocalDate purchaseDate;
    private Condition equipmentCondition;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
}