package com.justteam.test_quest_api.api.gameboard.dto;

import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Schema(description = "게임보드 생성 DTO")
public class GameBoardCreateDto {

    @NotBlank
    @Schema(description = "게임보드 제목", required = true, nullable = false)
    private String title;

    @Schema(description = "게임보드 설명", nullable = true)
    private String description;

    @NotBlank
    @Schema(description = "플랫폼", required = true, nullable = false)
    private String platform;

    @NotBlank
    @Schema(description = "게임 타입", required = true, nullable = false)
    private String type;

    @Schema(description = "썸네일 URL", hidden = true)
    private String thumbnailUrl;

    @Schema(description = "링크 URL", nullable = true)
    private String linkUrl;

    @Schema(description = "게임보드 이미지 파일", nullable = true)
    private MultipartFile boardImage;

    @NotBlank
    @Schema(description = "모집 상태", required = true, nullable = false)
    private String recruitStatus;

    @NotNull
    @Schema(description = "시작 날짜", required = true, nullable = false)
    private LocalDate startDate;

    @NotNull
    @Schema(description = "종료 날짜", required = true, nullable = false)
    private LocalDate endDate;


    public GameBoard toEntity() {
        GameBoard board = new GameBoard();

        board.setTitle(this.title);
        board.setDescription(this.description);
        board.setPlatform(this.platform);
        board.setType(this.type);
        board.setThumbnailUrl(this.thumbnailUrl);
        board.setLinkUrl(this.linkUrl);
        board.setStartDate(this.startDate);
        board.setEndDate(this.endDate);
        board.setRecruitStatus(this.recruitStatus);
        return board;
    }
}
