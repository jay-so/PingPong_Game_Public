package org.prography.spring.repository;

import org.prography.spring.domain.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
}
