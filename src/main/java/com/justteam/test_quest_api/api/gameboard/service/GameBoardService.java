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

    @Transactional(readOnly = true)
    public GameBoardPageResponse listAllGameBoards(GameBoardListDto gameBoardListDto) {
        String sortOrder = gameBoardListDto.getSortOrder();
        String keyword = gameBoardListDto.getKeyword();
        int pageSize = gameBoardListDto.getPageSize();

        // 다음 페이지 존재 여부 확인을 위해 요청한 사이즈보다 1개 더 조회
        Pageable pageable = PageRequest.of(0, pageSize + 1);

        List<GameBoardSummaryDto> gameBoardsRaw;

        // lastCreateAt 값이 없으면 첫 페이지로 간주
        if (gameBoardListDto.getLastCreateAt() == null) {
            if ("latest".equals(sortOrder)) {
                gameBoardsRaw = gameBoardRepository.findFirstPageByCreateAtDesc(keyword, pageable);
            } else {
                gameBoardsRaw = gameBoardRepository.findFirstPageByCreateAtAsc(keyword, pageable);
            }
        }
        // lastCreateAt 값이 있으면 다음 페이지로 간주 (커서 사용)
        else {
            LocalDateTime lastCreateAt = gameBoardListDto.getLastCreateAt();
            String lastId = gameBoardListDto.getLastId(); // lastId를 여기서 사용

            if ("latest".equals(sortOrder)) {
                gameBoardsRaw = gameBoardRepository.findNextPageByCreateAtDescAndOptionalKeyword(
                        keyword, lastCreateAt, lastId, pageable);
            } else {
                gameBoardsRaw = gameBoardRepository.findNextPageByCreateAtAscAndOptionalKeyword(
                        keyword, lastCreateAt, lastId, pageable);
            }
        }

        // 다음 페이지 존재 여부 계산
        boolean hasNext = gameBoardsRaw.size() > pageSize;
        // 실제 반환할 리스트는 요청된 페이지 사이즈만큼 자르기
        List<GameBoardSummaryDto> resultGameBoards = hasNext ? gameBoardsRaw.subList(0, pageSize) : gameBoardsRaw;

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
