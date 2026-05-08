package org.java_proj.gym_management_system.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.java_proj.gym_management_system.common.constant.BaseEnum;
import org.java_proj.gym_management_system.common.constant.MemberStatus;

@Converter(autoApply = true)
public class MemberStatusConverter implements AttributeConverter<MemberStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MemberStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public MemberStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return BaseEnum.fromValue(MemberStatus.class, dbData);
    }
}
