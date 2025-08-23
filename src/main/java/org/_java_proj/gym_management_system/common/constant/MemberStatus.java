package org._java_proj.gym_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberStatus implements BaseEnum<Integer> {
    PENDING(1),
    ACTIVE(2),
    CANCELLED(3),
    COMPLETED(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
