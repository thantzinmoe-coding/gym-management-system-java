package org._java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum Condition implements BaseEnum<Integer> {
    Excellent(1),
    Good(2),
    Fair(3),
    Poor(4);

    private final int value;

    Condition(int value) {   // 👈 manual constructor
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}

