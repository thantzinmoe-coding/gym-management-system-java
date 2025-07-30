package org._java_proj.gym_management_system.common.converter;

import jakarta.persistence.Converter;
import org._java_proj.gym_management_system.common.constant.Status;

@Converter(autoApply = true)
public class StatusConverter extends BaseEnumConverter<Status, Integer>{

     public StatusConverter() {
        super(Status.class);
    }
}