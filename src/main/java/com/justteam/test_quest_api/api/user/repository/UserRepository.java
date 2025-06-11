package com.justteam.test_quest_api.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justteam.test_quest_api.api.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}
