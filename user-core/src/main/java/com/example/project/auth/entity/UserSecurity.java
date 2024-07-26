package com.example.project.auth.entity;

import com.example.project.enums.LoginProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column
    private String email;

    @Column
    private String password;

    @Column(name = "provider", length = 15)
    @Enumerated(EnumType.STRING)
    private LoginProvider provider;

    @Column(name = "social_member_id")
    private String socialMemberId;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void setSocialMemberId(String socialMemberId) {
        this.socialMemberId = socialMemberId;
    }
}
