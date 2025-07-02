package com.justteam.test_quest_api.api.gameboard.repository;

import com.justteam.test_quest_api.api.gameboard.dto.GameBoardListDto;
import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameBoardRepository extends JpaRepository<GameBoard, String> {
    @Query("SELECT g FROM GameBoard g " +
            "WHERE (:lastCreateAt IS NULL OR " + // 첫 페이지 요청 시 커서 없음
            "       g.createAt < :lastCreateAt OR " + // 생성일시가 커서보다 이전인 경우 (최신순)
            "       (g.createAt = :lastCreateAt AND g.id < :lastId)) " + // 생성일시가 같으면 ID가 커서보다 작은 경우 (동일 생성일시 처리)
            "AND (:searchKeyword IS NULL OR :searchKeyword = '' OR " + // 검색 키워드가 없거나 비어있으면 검색 조건 무시
            "    (:searchType = 'title' AND g.title LIKE %:searchKeyword%) OR " + // 제목으로 검색
            "    (:searchType = 'author' AND g.author LIKE %:searchKeyword%)) " + // 작성자로 검색
            "ORDER BY g.createAt DESC, g.id DESC " + // 최신순 정렬 (생성일시 내림차순, ID 내림차순)
            "LIMIT :pageSize") // 페이지 크기만큼 제한
    List<GameBoardListDto> findLatestGameBoardsWithCursor(
            @Param("lastId") Long lastId,
            @Param("lastCreateAt") LocalDateTime lastCreateAt,
            @Param("searchKeyword") String searchKeyword,
            @Param("searchType") String searchType,
            @Param("pageSize") int pageSize);

    /**
     * 오래된 순으로 게임보드를 조회합니다 (커서 기반 페이지네이션 및 검색).
     *
     * @param lastId 마지막으로 조회된 게시글의 ID (커서)
     * @param lastCreateAt 마지막으로 조회된 게시글의 생성일시 (커서)
     * @param searchKeyword 검색 키워드 (제목 또는 작성자)
     * @param searchType 검색 타입 ("title" 또는 "author")
     * @param pageSize 한 페이지당 게시글 수
     * @return 조회된 게임보드 리스트
     */
    @Query("SELECT g FROM GameBoard g " +
            "WHERE (:lastCreateAt IS NULL OR " + // 첫 페이지 요청 시 커서 없음
            "       g.createAt > :lastCreateAt OR " + // 생성일시가 커서보다 이후인 경우 (오래된 순)
            "       (g.createAt = :lastCreateAt AND g.id > :lastId)) " + // 생성일시가 같으면 ID가 커서보다 큰 경우 (동일 생성일시 처리)
            "AND (:searchKeyword IS NULL OR :searchKeyword = '' OR " + // 검색 키워드가 없거나 비어있으면 검색 조건 무시
            "    (:searchType = 'title' AND g.title LIKE %:searchKeyword%) OR " + // 제목으로 검색
            "    (:searchType = 'author' AND g.author LIKE %:searchKeyword%)) " + // 작성자로 검색
            "ORDER BY g.createAt ASC, g.id ASC " + // 오래된 순 정렬 (생성일시 오름차순, ID 오름차순)
            "LIMIT :pageSize") // 페이지 크기만큼 제한
    List<GameBoardListDto> findOldestGameBoardsWithCursor(
            @Param("lastId") Long lastId,
            @Param("lastCreateAt") LocalDateTime lastCreateAt,
            @Param("searchKeyword") String searchKeyword,
            @Param("searchType") String searchType,
            @Param("pageSize") int pageSize);

}
