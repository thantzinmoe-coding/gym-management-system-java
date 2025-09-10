package org._java_proj.gym_management_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org._java_proj.gym_management_system.common.entity.MasterData;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroup extends MasterData {

    private String name;

    private Integer maxMembers = 20;

    private Integer currentMembers = 1;

    @Column(nullable = false)
    private Long creatorId;

    private Boolean isPrivate = false;

    private String meetingSchedule;

    @Column(length = 500)
    private String groupImageUrl;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    @JsonBackReference
    private User creator;


    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonIgnore  // prevents serialization of proxy
    private List<GroupMember> members;

}
