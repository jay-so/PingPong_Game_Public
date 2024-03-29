package org.prography.spring.repository;

import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    Optional<UserRoom> findByUserId_Id(Long userId);

    List<UserRoom> findByRoomId_Id(Long roomId);

    Optional<UserRoom> findByUserId_IdAndRoomId_Id(Long userId, Long roomId);

    Long countByRoomId_IdAndTeamStatus(Long roomId, TeamStatus changeTeamStatus);

    Optional<UserRoom> findByRoomId_IdAndUserId_Id(Long roomId, Long userId);

    void deleteByRoomId_Id(Long roomId);

    void deleteByUserId_IdAndRoomId_Id(Long userId, Long roomId);
}
