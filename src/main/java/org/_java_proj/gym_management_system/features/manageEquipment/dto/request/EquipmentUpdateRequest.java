package org._java_proj.gym_management_system.features.manageEquipment.dto.request;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.Condition;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class EquipmentUpdateRequest {
    private String name;
    private Condition equipmentCondition;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private MultipartFile equipmentPhoto;
}