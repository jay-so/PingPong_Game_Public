package org.prography.spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
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
    private Long id;

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
}
