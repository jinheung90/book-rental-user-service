package com.example.project.book.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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

    @Column(name = "is_main")
    private Boolean mainImage;

    @ManyToOne
    @JoinColumn(name = "user_book_id")
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
    public void setMainImage(Boolean mainImage) {
        this.mainImage = mainImage;
    }
}
