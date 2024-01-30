package org.prography.spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.prography.spring.domain.enums.UserStatus;

import java.time.LocalDateTime;

import static org.prography.spring.domain.enums.UserStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Long fakerId;

    @NotNull
    @Column(length = 255)
    private String name;

    @NotNull
    @Column(length = 255)
    private String email;

    @NotNull
    @Column(length = 25)
    private UserStatus status = ACTIVE;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;
}
