package com.justteam.test_quest_api.api.user.repository;

import com.justteam.test_quest_api.api.user.dto.UserInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;

import com.justteam.test_quest_api.api.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    @Query("SELECT new com.justteam.test_quest_api.api.user.dto.UserInfoDto(u.name, u.nickname, u.profileImg) FROM User u WHERE u.userId = :userId")
    Optional<UserInfoDto> findUserInfoDtoByUserId(@Param("userId") String userId);

}
