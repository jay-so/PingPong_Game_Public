package org.prography.spring.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prography.spring.domain.enums.UserStatus;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
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
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Builder
    private User(
            Long fakerId,
            String name,
            String email,
            UserStatus status
    ) {
        this.fakerId = fakerId;
        this.name = name;
        this.email = email;
        this.status = status;
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
