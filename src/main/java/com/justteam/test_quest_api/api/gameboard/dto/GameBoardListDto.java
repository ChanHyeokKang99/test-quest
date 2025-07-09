package com.justteam.test_quest_api.api.gameboard.dto;

import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class GameBoardListDto {

    private String lastId = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastCreateAt;

    // 검색 타입 ("title" 또는 "author")
    private String keyword; // "title" 또는 "author"

    // 한 페이지당 게시글 수
    @Min(value = 5, message = "페이지 크기는 1 이상이어야 합니다.")
    private int pageSize = 5; // 기본값 10

    // 정렬 순서 ("latest" 또는 "oldest")
    @Schema(defaultValue = "latest")
    private String sortOrder = "latest"; // 기본값 "latest" (최신순)

}
