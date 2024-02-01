package org.prography.spring.repository;

import org.prography.spring.domain.Room;
import org.prography.spring.domain.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByHost_Id(Long userId);

    Optional<Room> findByIdAndRoomStatus(Long roomId, RoomStatus roomStatus);
}
