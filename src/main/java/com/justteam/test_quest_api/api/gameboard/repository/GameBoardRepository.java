package com.justteam.test_quest_api.api.gameboard.repository;

import com.justteam.test_quest_api.api.gameboard.dto.GameBoardDetailSummaryDto;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto;
import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameBoardRepository extends JpaRepository<GameBoard, String> {

    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardDetailSummaryDto(" +
            "g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, " +
            "g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname) " +
            "FROM GameBoard g " +
            "WHERE g.id=:boardId")
    Optional<GameBoardDetailSummaryDto> findSummaryDtoById(@Param("boardId") String boardId);



    // ✅ [다음 페이지] 최신순 (DESC) - lastId 포함
    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto(g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname) " +
            "FROM GameBoard g " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR g.title LIKE %:keyword% OR g.user.nickname LIKE %:keyword%) " +
            "AND (g.createAt < :lastCreateAt OR (g.createAt = :lastCreateAt AND g.id < :lastId)) " +
            "ORDER BY g.createAt DESC, g.id DESC")
    List<GameBoardSummaryDto> findNextPageByCreateAtDescAndOptionalKeyword(
            @Param("keyword") String keyword,
            @Param("lastCreateAt") LocalDateTime lastCreateAt,
            @Param("lastId") String lastId, // lastId 필수
            Pageable pageable);

    // ✅ [다음 페이지] 오래된순 (ASC) - lastId 포함
    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto(g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname)  " +
            "FROM GameBoard g " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR g.title LIKE %:keyword% OR g.user.nickname LIKE %:keyword%) " +
            "AND (g.createAt > :lastCreateAt OR (g.createAt = :lastCreateAt AND g.id > :lastId)) " +
            "ORDER BY g.createAt ASC, g.id ASC")
    List<GameBoardSummaryDto> findNextPageByCreateAtAscAndOptionalKeyword(
            @Param("keyword") String keyword,
            @Param("lastCreateAt") LocalDateTime lastCreateAt,
            @Param("lastId") String lastId, // lastId 필수
            Pageable pageable);


    // ✅ [첫 페이지] 최신순 (DESC)
    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto(g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname)  " +
            "FROM GameBoard g " +
            "WHERE :keyword IS NULL OR :keyword = '' OR g.title LIKE %:keyword% OR g.user.nickname LIKE %:keyword% " +
            "ORDER BY g.createAt DESC, g.id DESC")
    List<GameBoardSummaryDto> findFirstPageByCreateAtDesc(@Param("keyword") String keyword, Pageable pageable);

    // ✅ [첫 페이지] 오래된순 (ASC)
    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto(g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname)  " +
            "FROM GameBoard g " +
            "WHERE :keyword IS NULL OR :keyword = '' OR g.title LIKE %:keyword% OR g.user.nickname LIKE %:keyword% " +
            "ORDER BY g.createAt ASC, g.id ASC")
    List<GameBoardSummaryDto> findFirstPageByCreateAtAsc(@Param("keyword") String keyword, Pageable pageable);

}
