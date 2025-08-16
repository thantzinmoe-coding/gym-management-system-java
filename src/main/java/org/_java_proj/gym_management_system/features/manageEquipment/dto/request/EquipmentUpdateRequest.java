package org._java_proj.gym_management_system.features.manageEquipment.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EquipmentUpdateRequest {
    private String name;
    private Integer equipmentCondition;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
}
