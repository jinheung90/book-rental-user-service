package com.example.project.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * User
 */

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "users")
@Entity
@SQLRestriction("where is_deleted = 0")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private String phone = "";

    @Column(name = "nick_name", nullable = false, unique = true)
    private String nickName = "";

    @Column(nullable = false, unique = true)
    private String email = "";

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default private Set<UserSecurity> userSecurities = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted", columnDefinition = "integer DEFAULT 0")
    @Builder.Default
    private Integer deleted = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_authorities",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private List<Authority> authorities;

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getAuthorityNames() {
        return authorities.stream().map(Authority::getAuthorityName).toList();
    }
}
