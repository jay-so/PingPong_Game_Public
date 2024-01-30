package org.prography.spring.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.prography.spring.domain.enums.UserStatus;

@Convert
public final class UserStatusConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserStatus status) {
        return status.getDescription();
    }

    @Override
    public UserStatus convertToEntityAttribute(String status) {
        return UserStatus.from(status);
    }
}
