package com.shophub.rest.entity.auth;

import com.shophub.rest.entity.enums.EProvider;
import com.shophub.rest.entity.enums.EYesNo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    EProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false)
    EYesNo isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @OneToOne
    @JoinColumn(name = "authority_id", referencedColumnName = "id", updatable = false)
    Authority authority;

    @PrePersist
    protected void onCreate() {
        provider = EProvider.LOCAL;
        isActive = EYesNo.YES;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
}
