package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class Booking extends MasterData {

    @Column(nullable = false)
    private MemberStatus memberStatus;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private User member;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gymPackage_id", nullable = false, unique = true)
    private GymPackage gymPackage;

    public Booking() {}
}
