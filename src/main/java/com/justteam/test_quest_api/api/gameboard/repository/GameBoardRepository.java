package com.justteam.test_quest_api.api.gameboard.repository;

import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface GameBoardRepository extends JpaRepository<GameBoard, String> {
}
