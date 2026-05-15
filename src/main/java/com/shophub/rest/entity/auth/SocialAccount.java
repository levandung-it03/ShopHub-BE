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
@Table(
    name = "social_account",
    uniqueConstraints = {@UniqueConstraint(columnNames = { "provider", "provider_id" })}
)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "email")
    String email;

    @Column(name = "phone", unique = true)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    EProvider provider;

    @Column(name = "provider_id", nullable = false)
    String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false)
    EYesNo isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        isActive = EYesNo.YES;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
}
