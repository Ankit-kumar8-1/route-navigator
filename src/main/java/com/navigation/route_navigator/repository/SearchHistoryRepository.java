package com.navigation.route_navigator.repository;

import com.navigation.route_navigator.entities.SearchHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {
}
