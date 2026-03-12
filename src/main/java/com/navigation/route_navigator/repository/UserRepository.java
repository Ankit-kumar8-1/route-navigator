package com.navigation.route_navigator.repository;

import com.navigation.route_navigator.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
}
