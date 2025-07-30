package org._java_proj.gym_management_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.Condition;
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

    @Column(nullable = false)
    private Condition condition;

    @Column(nullable = false)
    private LocalDate lastMaintenanceDate;

    @Column(nullable = false)
    private LocalDate nextMaintenanceDate;

    public Equipment() {}

}
