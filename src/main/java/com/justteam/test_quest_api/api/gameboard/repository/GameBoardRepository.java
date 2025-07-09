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



    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto(g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname) " +
            "FROM GameBoard g " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR g.title LIKE %:keyword% OR CAST(g.user.userId AS string) LIKE %:keyword%) " + // g.user.userId -> g.userId (일반적인 경우)
            "AND (g.createAt, g.id) > (:lastCreateAt, :lastId) " + // 오름차순 커서 조건
            "ORDER BY g.createAt ASC, g.id ASC") // 정렬 기준도 커서와 일치
    List<GameBoardSummaryDto> findNextPageByCreateAtAscAndOptionalKeyword( // <-- 반환 타입 GameBoard로 변경
                                                                           @Param("keyword") String keyword,
                                                                           @Param("lastCreateAt") LocalDateTime lastCreateAt,
                                                                           @Param("lastId") String lastId, Pageable pageable);

    @Query("SELECT new com.justteam.test_quest_api.api.gameboard.dto.GameBoardSummaryDto(g.id, g.title, g.description, g.platform, g.type, g.thumbnailUrl, g.linkUrl, g.startDate, g.endDate, g.author, g.views, g.createAt, g.recruitStatus, g.user.nickname) " +
            "FROM GameBoard g " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR g.title LIKE %:keyword% OR CAST(g.user.userId AS string) LIKE %:keyword%) " + // g.user.userId -> g.userId (일반적인 경우)
            "AND (g.createAt, g.id) < (:lastCreateAt, :lastId) " + // 내림차순 커서 조건
            "ORDER BY g.createAt DESC, g.id DESC") // 정렬 기준도 커서와 일치
    List<GameBoardSummaryDto> findNextPageByCreateAtDescAndOptionalKeyword( // <-- 반환 타입 GameBoard로 변경
                                                                  @Param("keyword") String keyword,
                                                                  @Param("lastCreateAt") LocalDateTime lastCreateAt,
                                                                  @Param("lastId") String lastId,
                                                                  Pageable pageable);
}
