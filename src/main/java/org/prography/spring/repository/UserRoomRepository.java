package org.prography.spring.repository;

import org.prography.spring.domain.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    Optional<UserRoom> findByUserId_Id(Integer userId);
}
