package org._java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Condition implements BaseEnum<Integer>{
    GOOD(1),
    NEED_TO_PREPARE(2),
    BROKEN(3);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
