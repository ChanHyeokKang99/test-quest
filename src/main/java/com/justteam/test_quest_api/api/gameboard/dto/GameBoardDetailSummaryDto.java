package com.justteam.test_quest_api.api.gameboard.dto;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GameBoardDetailSummaryDto {
    private String id;
    private String title; // 순서 변경 가능
    private String description;
    private String platform;
    private String type;
    private String thumbnailUrl;
    private String linkUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String author;
    private int views;
    private LocalDateTime createAt;
    private String recruitStatus;
    private String nickname;

    public GameBoardDetailSummaryDto(String id, String title, String description, String platform, String type,
                               String thumbnailUrl, String linkUrl, LocalDate startDate, LocalDate endDate,
                               String author, int views, LocalDateTime createAt, String recruitStatus, String nickname) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.platform = platform;
        this.type = type;
        this.thumbnailUrl = thumbnailUrl;
        this.linkUrl = linkUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.author = author;
        this.views = views;
        this.createAt = createAt;
        this.recruitStatus = recruitStatus;
        this.nickname = nickname;
    }
}