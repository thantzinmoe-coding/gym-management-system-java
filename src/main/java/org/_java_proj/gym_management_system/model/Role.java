package org._java_proj.gym_management_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class Role extends MasterData {
    @Column(unique = true)
    private String name;

    public Role() {}

    public Role(final String name) {
        this.name = name;
    }
}
