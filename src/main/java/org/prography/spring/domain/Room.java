package org.prography.spring.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prography.spring.domain.enums.RoomStatus;
import org.prography.spring.domain.enums.RoomType;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;
import static org.prography.spring.domain.enums.RoomStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 255)
    private String title;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host")
    private User host;

    @NotNull
    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotNull
    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Builder
    private Room(
            Long id,
            String title,
            User host,
            RoomType roomType,
            RoomStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.host = host;
        this.roomType = roomType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void initCreatedAt() {
        this.createdAt = now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void initUpdatedAt() {
        this.updatedAt = now();
    }

    public void startGame() {
        this.status = PROGRESS;
    }

    public void finishGame() {
        this.status = FINISH;
    }

    public void exitRoom() {
        this.status = FINISH;
    }
}
