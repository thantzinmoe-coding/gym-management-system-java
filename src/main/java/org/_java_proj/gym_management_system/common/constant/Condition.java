package org._java_proj.gym_management_system.common.constant;

public enum Condition {
    GOOD(1),
    FAIR(2),
    POOR(3);

    private final int value;

    Condition(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Optional: Method to find enum by value
    public static Condition fromValue(int value) {
        for (Condition c : Condition.values()) {
            if (c.value == value) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown condition value: " + value);
    }
}
