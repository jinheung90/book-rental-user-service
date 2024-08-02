package com.example.project.auth.entity;

import com.example.project.enums.LoginProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "user_securities",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"provider", "social_member_id"}
    )
)
@Entity
public class UserSecurity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_security_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(name = "provider", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginProvider provider;

    @Column(name = "social_member_id")
    private String socialMemberId;

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    public void setSocialMemberId(String socialMemberId) {
        this.socialMemberId = socialMemberId;
    }
}
