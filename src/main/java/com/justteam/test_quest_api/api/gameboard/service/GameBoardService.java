package com.justteam.test_quest_api.api.gameboard.service;

import com.justteam.test_quest_api.api.gameboard.dto.*;
import com.justteam.test_quest_api.api.gameboard.entity.GameBoard;
import com.justteam.test_quest_api.api.gameboard.repository.GameBoardRepository;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.common.exception.NotFound;
import com.justteam.test_quest_api.common.web.context.RequestHeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
           User user = userRepository.findById(RequestHeaderUtils.getUserId()).orElseGet(User::new);
           board.setUser(user);
           gameBoardRepository.save(board);
           return ApiResponseDto.createOk(null);
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
        return ApiResponseDto.defaultOk();
    }

    @Transactional(readOnly = true) // 읽기 전용 작업이므로 readOnly = true 권장
    public GameBoardPageResponse listAllGameBoards(GameBoardListDto gameBoardListDto) {

        LocalDateTime actualLastCreateAt = gameBoardListDto.getLastCreateAt();
        String actualLastId = gameBoardListDto.getLastId();
        String sortOrder = gameBoardListDto.getSortOrder();
        String keyword = gameBoardListDto.getKeyword();
        int pageSize = gameBoardListDto.getPageSize();

        if (sortOrder.equals("latest")) {
            if (actualLastCreateAt == null) {
                actualLastCreateAt = LocalDateTime.now();
            }
            if (actualLastId == null || actualLastId.isEmpty()) {
                actualLastId = "";
            }
        } else {
            if (actualLastCreateAt == null) {
                actualLastCreateAt = LocalDateTime.MIN;
            }
            if (actualLastId == null || actualLastId.isEmpty()) {
                actualLastId = "";
            }
        }

        Pageable pageable = PageRequest.of(0, pageSize + 1);

        List<GameBoardSummaryDto> gameBoardsRaw;

        if (sortOrder.equals("latest")) {
            gameBoardsRaw = gameBoardRepository.findNextPageByCreateAtDescAndOptionalKeyword(
                    keyword, actualLastCreateAt, actualLastId, pageable);
        } else { // "oldest"
            gameBoardsRaw = gameBoardRepository.findNextPageByCreateAtAscAndOptionalKeyword(
                    keyword, actualLastCreateAt, actualLastId, pageable);
        }

        boolean hasNext = false;
        List<GameBoardSummaryDto> resultGameBoards;

        if (gameBoardsRaw.size() > pageSize) {
            hasNext = true;
            resultGameBoards = gameBoardsRaw.subList(0, pageSize);
        } else {
            resultGameBoards = gameBoardsRaw;
        }

        return new GameBoardPageResponse(resultGameBoards, hasNext);
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
