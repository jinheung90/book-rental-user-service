package com.example.project.book.entity;


import com.example.project.common.enums.BookRentalStateType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.List;

@Table(name = "user_books")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String detail;

    @Column(name = "state", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookRentalStateType state;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "userBook")
    @BatchSize(size = 3)
    @OrderBy("imageOrder asc")
    private List<UserBookImage> images;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
