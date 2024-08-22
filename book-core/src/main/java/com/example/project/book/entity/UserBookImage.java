package com.example.project.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Table(name = "user_book_images")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class UserBookImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_order", nullable = false)
    private Integer imageOrder;

    @Column(name = "image_url", length = 1023, nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_book_id", nullable = false)
    private UserBook userBook;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void setImageOrder(Integer imageOrder) {
        this.imageOrder = imageOrder;
    }
}
