package com.example.project.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private String phone = "";

    @Column(nullable = false, unique = true)
    private String email = "";

    @OneToOne(mappedBy = "user")
    @OrderBy("createdAt asc")
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user")
    @BatchSize(size = 3)
    private List<UserAddress> userAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default private Set<UserSecurity> userSecurities = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_authorities",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private List<Authority> authorities;

    public List<String> getAuthorityNames() {
        return authorities.stream().map(Authority::getAuthorityName).toList();
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void inactive() {
        this.deleted = true;
    }
}
