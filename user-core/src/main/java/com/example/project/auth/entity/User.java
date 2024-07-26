package com.example.project.auth.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column
    private String phone = "";

    @Column
    private String name = "";

    @Column
    private String email = "";

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "star_rating")
    private float starRating;

    @Column(name = "user_role")
    private Integer userRole = 1;

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
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)
            )},
            inverseJoinColumns = {
                @JoinColumn(name = "authority_name", referencedColumnName = "authority_name",
                    foreignKey =  @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))})
    private List<Authority>  authorities;

    public List<String> getAuthorityNames() {
        return this.authorities.stream().map(Authority::getAuthorityName).toList();
    }

    public void updateUserProfile(String profileImageUrl, String name) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void addStarRating(float starRating) {
        this.starRating += starRating;
    }
}
