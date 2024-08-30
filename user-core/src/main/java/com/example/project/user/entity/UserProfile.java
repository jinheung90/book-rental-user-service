package com.example.project.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "user_profiles")
@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nick_name", nullable = false, unique = true)
    private String nickName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void updateProfileImageUrl(String profileImageUrl) {
        if(profileImageUrl == null || profileImageUrl.isBlank()) {
            return;
        }
        this.profileImageUrl = profileImageUrl;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


}
