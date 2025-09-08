package org._java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Condition implements BaseEnum<Integer>{
    Excellent(1),
    Good(2),
    Fair(3),
    Poor(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }

}
