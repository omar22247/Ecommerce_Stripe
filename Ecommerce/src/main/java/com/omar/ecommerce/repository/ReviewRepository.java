package com.omar.ecommerce.repository;

import com.omar.ecommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(UUID productId);
    Optional<Review> findByUserIdAndProductId(UUID userId, UUID productId);
}
