package com.navigation.route_navigator.repository;

import com.navigation.route_navigator.entities.SearchHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {

    boolean existsByUserIdAndFromLocationAndToLocation(
            Long userId,
            String fromLocation,
            String toLocation
    );

    // User ki saari history
    List<SearchHistoryEntity> findByUserIdOrderBySearchedAtDesc(Long userId);

    // User ki history exist karti hai ya nahi
    boolean existsByUserId(Long userId);

    // User ki saari history delete karo
    void deleteByUserId(Long userId);
}
