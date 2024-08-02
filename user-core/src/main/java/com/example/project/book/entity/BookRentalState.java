package com.example.project.book.entity;

import com.example.project.auth.entity.User;
import com.example.project.enums.BookRentalStateType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Table(name = "book_rental_state")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BookRentalState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state", nullable = false)
    private BookRentalStateType state;

    @OneToOne
    @JoinColumn(name = "borrower_user_id", referencedColumnName = "id")
    private User borrowerUser;

    @ManyToOne
    @JoinColumn(name = "user_book_id", referencedColumnName = "id")
    private UserBook userBook;

    @Column(name = "review_star_rating", nullable = false)
    private float reviewStarRating;

    @Column(name = "review_context", nullable = false)
    private String reviewContext;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
