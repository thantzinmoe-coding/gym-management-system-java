package org._java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AddressEntityType implements BaseEnum<Integer> {
    TRAINER(1),
    MEMBER(2);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
