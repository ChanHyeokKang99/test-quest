package com.justteam.test_quest_api.api.gameboard.dto;

import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GameBoardUpdateDto {

    private String id;
    private String title;
    private String description;
    private MultipartFile boardImage;

    @Schema(hidden = true)
    private String thumbnailUrl;

    public GameBoard toEntity() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setId(id);
        return gameBoard;
    }
}
