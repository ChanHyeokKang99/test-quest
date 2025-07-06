package com.justteam.test_quest_api.api.gameboard.service;

import com.justteam.test_quest_api.api.gameboard.dto.*;
import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import com.justteam.test_quest_api.api.gameboard.repository.GameBoardRepository;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.common.exception.NotFound;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List listAllGameBoards(GameBoardListDto gameBoardListDto) {
        log.info(gameBoardListDto.toString());

        LocalDateTime actualLastCreateAt = gameBoardListDto.getLastCreateAt();
        String actualLastId = gameBoardListDto.getLastId();

        if (gameBoardListDto.getSortOrder().equals("latest")) {
            if (actualLastCreateAt == null) {
                actualLastCreateAt = LocalDateTime.now(); // 또는 LocalDateTime.MAX (데이터에 따라 적절히 선택)
            }
            if(actualLastId == null) {
                actualLastId = "";
            }
        }else {
            if (actualLastCreateAt == null) {
                actualLastCreateAt = LocalDateTime.MIN;
            }
            if (actualLastId == null) {
                // String ID의 가장 작은 값을 찾기 어려우므로, 빈 문자열 사용 (데이터에 따라 달라질 수 있음)
                actualLastId = "";
            }
        }

        Pageable pageable = PageRequest.of(0, gameBoardListDto.getPageSize());

        List<GameBoardSummaryDto> gameBoards;
        if (gameBoardListDto.getSortOrder().equals("latest")) {
            gameBoards = gameBoardRepository.findNextPageByCreateAtDescAndOptionalKeyword(
                    gameBoardListDto.getKeyword(), actualLastCreateAt, actualLastId, pageable);
        } else {
            gameBoards = gameBoardRepository.findNextPageByCreateAtAscAndOptionalKeyword(
                    gameBoardListDto.getKeyword(), actualLastCreateAt, actualLastId, pageable);
        }
        return gameBoards.stream().toList();
    }

    @Transactional
    public GameBoardDetailSummaryDto getGameBoardDetail(String id) {
        try {
            Optional<GameBoardDetailSummaryDto> gameBoard = gameBoardRepository.findSummaryDtoById(id);
            return gameBoard.isPresent() ? gameBoard.get() : null;
        } catch (Exception e) {
            throw new NotFound(e.getMessage());
        }
    }


    @Transactional
    public ApiResponseDto<String> deleteGameBoard(String boardId) {
        try {
            gameBoardRepository.deleteById(boardId);
            return ApiResponseDto.defaultOk();
        } catch (Exception e) {
            throw new NotFound(e.getMessage());
        }
    }

}
