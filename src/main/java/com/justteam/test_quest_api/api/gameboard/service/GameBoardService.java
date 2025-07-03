package com.justteam.test_quest_api.api.gameboard.service;

import com.justteam.test_quest_api.api.gameboard.dto.GameBoardCreateDto;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardListDto;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardUpdateDto;
import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import com.justteam.test_quest_api.api.gameboard.repository.GameBoardRepository;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.common.exception.BadParameter;
import com.justteam.test_quest_api.common.exception.NotFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameBoardService {

    private final GameBoardRepository gameBoardRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponseDto createGameBoard(GameBoardCreateDto boardCreateDto) {
       try {
           GameBoard board = boardCreateDto.toEntity();
           User user = userRepository.findById(boardCreateDto.getUserId()).orElseGet(User::new);
           board.setUser(user);
           gameBoardRepository.save(board);
           return ApiResponseDto.createOk(board);
       } catch (Exception e) {
           throw new NotFound(e.getMessage());
       }
    }

    @Transactional
    public ApiResponseDto<String> updateGameBoard(GameBoardUpdateDto boardUpdateDto) {
        Optional<GameBoard> result = gameBoardRepository.findById(boardUpdateDto.getId());
        GameBoard gameBoard = result.get();
        if (boardUpdateDto.getTitle() != null) {
            gameBoard.setTitle(boardUpdateDto.getTitle());
        }
        if (boardUpdateDto.getDescription() != null) {
            gameBoard.setDescription(boardUpdateDto.getDescription());
        }
        if(boardUpdateDto.getThumbnailUrl() != null) {
            gameBoard.setThumbnailUrl(boardUpdateDto.getThumbnailUrl());
        }
        gameBoardRepository.save(gameBoard);
        // 4. 업데이트된 엔티티 저장 (JPA Dirty Checking으로 인해 save() 호출 없이도 트랜잭션 종료 시 반영될 수 있지만, 명시적으로 호출하는 것이 좋습니다.)
        return ApiResponseDto.defaultOk();
    }

    @Transactional
    public List<GameBoardListDto> listAllGameBoards(GameBoardListDto gameBoardListDto) {
        String searchKeyword = gameBoardListDto.getSearchKeyword();
        String searchType = gameBoardListDto.getSearchType();
        int pageSize = gameBoardListDto.getPageSize();
        Long lastId = gameBoardListDto.getLastId();
        LocalDateTime lastCreateAt = gameBoardListDto.getLastCreateAt();

        if (searchKeyword != null && searchKeyword.trim().isEmpty()) {
            searchKeyword = null;
        }

        // 정렬 순서에 따라 다른 레포지토리 메소드 호출
        if ("oldest".equalsIgnoreCase(gameBoardListDto.getSortOrder())) {
            log.info("조회 요청: 오래된 순 (lastId: {}, lastCreateAt: {}, keyword: {}, type: {}, size: {})",
                    lastId, lastCreateAt, searchKeyword, searchType, pageSize);
            return gameBoardRepository.findOldestGameBoardsWithCursor(
                    lastId, lastCreateAt, searchKeyword, searchType, pageSize);
        } else { // 기본값 "latest"
            log.info("조회 요청: 최신순 (lastId: {}, lastCreateAt: {}, keyword: {}, type: {}, size: {})",
                    lastId, lastCreateAt, searchKeyword, searchType, pageSize);
            return gameBoardRepository.findLatestGameBoardsWithCursor(
                    lastId, lastCreateAt, searchKeyword, searchType, pageSize);
        }

    }

//    @Transactional
//    public ApiResponseDto updateGameBoard(GameBoardCreateDto boardCreateDto) {}

    @Transactional
    public ApiResponseDto deleteGameBoard(String boardId) {
        try {
            gameBoardRepository.deleteById(boardId);
            return ApiResponseDto.defaultOk();
        } catch (Exception e) {
            throw new NotFound(e.getMessage());
        }
    }
}
