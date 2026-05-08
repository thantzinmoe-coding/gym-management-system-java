package org.java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status implements BaseEnum<Integer>{
    ACTIVE(1),
    INACTIVE(2),
    PENDING(3),
    PAID(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
