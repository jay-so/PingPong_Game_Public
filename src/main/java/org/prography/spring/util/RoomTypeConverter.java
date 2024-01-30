package org.prography.spring.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.prography.spring.domain.enums.RoomType;

@Convert
public class RoomTypeConverter implements AttributeConverter<RoomType, String> {
    @Override
    public String convertToDatabaseColumn(RoomType type) {
        return type.getDescription();
    }

    @Override
    public RoomType convertToEntityAttribute(String type) {
        return RoomType.from(type);
    }
}
