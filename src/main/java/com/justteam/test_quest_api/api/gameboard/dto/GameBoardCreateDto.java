package com.justteam.test_quest_api.api.gameboard.dto;

import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GameBoardCreateDto {

    private String title;

    private String description;

    private String platform;

    private String type;

    @Schema(hidden = true)
    private String thumbnailUrl;

    private String linkUrl;

    @NotBlank(message = "썸네일을 넣어주세요")
    private MultipartFile boardImage;

    @NotBlank(message = "작성자를 입력해주세요")
    private String userId;

    public GameBoard toEntity() {
        GameBoard board = new GameBoard();

        board.setTitle(this.title);
        board.setDescription(this.description);
        board.setPlatform(this.platform);
        board.setType(this.type);
        board.setThumbnailUrl(this.thumbnailUrl);
        board.setLinkUrl(this.linkUrl);
        return board;
    }
}
