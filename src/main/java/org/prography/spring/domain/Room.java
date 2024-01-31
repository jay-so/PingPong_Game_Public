package org.prography.spring.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prography.spring.domain.enums.RoomStatus;
import org.prography.spring.domain.enums.RoomType;
import org.prography.spring.util.RoomStatusConverter;
import org.prography.spring.util.RoomTypeConverter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(length = 255)
    private String title;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User host;

    @NotNull
    @Column(length = 25)
    @Convert(converter = RoomTypeConverter.class)
    private RoomType roomType;

    @NotNull
    @Column(length = 25)
    @Convert(converter = RoomStatusConverter.class)
    private RoomStatus roomStatus;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Builder
    private Room(
            String title,
            User host,
            RoomType roomType,
            RoomStatus roomStatus
    ) {
        this.title = title;
        this.host = host;
        this.roomType = roomType;
        this.roomStatus = roomStatus;
    }

    @PrePersist
    public void initCreatedAt() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void initUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
