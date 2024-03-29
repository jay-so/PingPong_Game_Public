package org.prography.spring.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prography.spring.domain.enums.TeamStatus;

import static org.prography.spring.domain.enums.TeamStatus.BLUE;
import static org.prography.spring.domain.enums.TeamStatus.RED;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room roomId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User userId;

    @NotNull
    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "team")
    private TeamStatus teamStatus;

    @Builder
    private UserRoom(
            Room roomId,
            User userId,
            TeamStatus teamStatus
    ) {
        this.roomId = roomId;
        this.userId = userId;
        this.teamStatus = teamStatus;
    }

    public void changeTeamStatus() {
        this.teamStatus = this.teamStatus.equals(RED) ? BLUE : RED;
    }
}
