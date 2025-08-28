package org._java_proj.gym_management_system.common.converter;

import jakarta.persistence.Converter;
import org._java_proj.gym_management_system.common.constant.Condition;

@Converter(autoApply = true)
public class ConditionConverter extends BaseEnumConverter<Condition, Integer>{
    public ConditionConverter() {
        super(Condition.class);
    }
}
