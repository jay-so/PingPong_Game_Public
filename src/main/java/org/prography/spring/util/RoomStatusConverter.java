package org.prography.spring.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.prography.spring.domain.enums.RoomStatus;

@Convert
public class RoomStatusConverter implements AttributeConverter<RoomStatus, String> {
    @Override
    public String convertToDatabaseColumn(RoomStatus status) {
        return status.getDescription();
    }

    @Override
    public RoomStatus convertToEntityAttribute(String status) {
        return RoomStatus.from(status);
    }
}
