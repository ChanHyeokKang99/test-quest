package com.justteam.test_quest_api.api.gameboard.dto;

import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class GameBoardCreateDto {

    private String title;

    private String description;

    private String platform;

    private String type;

    @Schema(hidden = true)
    private String thumbnailUrl;

    private String linkUrl;

    private MultipartFile boardImage;

    private String recruitStatus;

    @NotBlank(message = "게시글 작성자 아이디 입력")
    private String userId;

    private String author;

    private LocalDate startDate;

    private LocalDate endDate;


    public GameBoard toEntity() {
        GameBoard board = new GameBoard();

        board.setTitle(this.title);
        board.setDescription(this.description);
        board.setPlatform(this.platform);
        board.setType(this.type);
        board.setThumbnailUrl(this.thumbnailUrl);
        board.setLinkUrl(this.linkUrl);
        board.setAuthor(this.author);
        board.setStartDate(this.startDate);
        board.setEndDate(this.endDate);
        board.setRecruitStatus(this.recruitStatus);
        return board;
    }
}
