package org.java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GymPackageType implements BaseEnum<Integer> {
    PERSONAL(1),
    GROUP(2);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
