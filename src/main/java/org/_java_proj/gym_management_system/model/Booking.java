package org._java_proj.gym_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.common.converter.MemberStatusConverter;
import org._java_proj.gym_management_system.common.entity.MasterData;

@Entity
@Getter
@Setter
public class Booking extends MasterData {

    @Column(nullable = false)
    @Convert(converter = MemberStatusConverter.class)
    private MemberStatus memberStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Removed unique = true constraint - multiple bookings can have same package
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gymPackage_id", nullable = false)
    private GymPackage gymPackage;

    public Booking() {}
}
