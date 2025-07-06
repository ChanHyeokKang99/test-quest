package com.justteam.test_quest_api.api.gameboard.entity;

import com.justteam.test_quest_api.api.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "game_board")
@Data
public class GameBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "platform", nullable = false)
    private String platform;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt=LocalDateTime.now();

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "recruit_status", nullable = false)
    private String recruitStatus = "open";

    @Column(name = "views", nullable = false)
    private int views=1;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
