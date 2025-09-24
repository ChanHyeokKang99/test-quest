package com.justteam.test_quest_api.api.gameboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GameBoardSummaryDto {
    private String id; // GameBoard의 id 타입에 맞게 조정
    private String title;
//    private String description;
    private String platform;
    private String type;
    private String thumbnailUrl;
//    private String linkUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private int views;
    private LocalDateTime createAt;
//    private String recruitStatus;
    private String nickname;
    // 모든 필드를 받는 생성자 (JPQL NEW 키워드 사용 시 필수)
    public GameBoardSummaryDto(String id, String title, String platform, String type,
                               String thumbnailUrl,
//                             String linkUrl, String recruitStatus, String description
                               LocalDate startDate, LocalDate endDate,
                               int views, LocalDateTime createAt, String nickname) {
        this.id = id;
        this.title = title;
//        this.description = description;
        this.platform = platform;
        this.type = type;
        this.thumbnailUrl = thumbnailUrl;
//        this.linkUrl = linkUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.views = views;
        this.createAt = createAt;
//        this.recruitStatus = recruitStatus;
        this.nickname = nickname;
    }

}