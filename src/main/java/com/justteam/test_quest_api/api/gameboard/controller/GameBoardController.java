package com.justteam.test_quest_api.api.gameboard.controller;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.gameboard.dto.*;
import com.justteam.test_quest_api.api.gameboard.service.GameBoardService;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/gameboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class GameBoardController {
    private final FirebaseStorageService firebaseStorageService;
    private final GameBoardService gameBoardService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ApiResponseDto<String> createGameBoard(@Valid GameBoardCreateDto boardAddDto) {

            try {
                String imageUrl = null;
                // DTO 내부에 있는 profileImage 필드 사용
                if (boardAddDto.getBoardImage() != null ) {
                    imageUrl = firebaseStorageService.uploadImage(boardAddDto.getBoardImage());
                    boardAddDto.setThumbnailUrl(imageUrl);
                }else {
                    return ApiResponseDto.createError("FILE_REQUIRED", "게시판 이미지는 필수입니다.");
                }
                log.info("Received request to add gameboard: {}", boardAddDto);
                gameBoardService.createGameBoard(boardAddDto);
                return ApiResponseDto.defaultOk();

            } catch (IOException e) {
                log.error("Image upload failed during add: {}", e.getMessage());
                return ApiResponseDto.createError("IMAGE_UPLOAD_FAILED", "프로필 이미지 업로드에 실패했습니다.");
            } catch (Exception e) {
                log.error("Board add failed: {}", e.getMessage());
                return ApiResponseDto.createError("BOARD_ADD_FAILED", "테스터 모집공고 등록이 실패하였습니다.");
            }
    }

    @GetMapping(value = "/list")
    private ApiResponseDto<GameBoardPageResponse> listGameBoard(@Valid @ModelAttribute GameBoardListDto gameBoardListDto) {
        log.info("Received request to list all gameboards");
        GameBoardPageResponse gameBoardList = gameBoardService.listAllGameBoards(gameBoardListDto);

        return ApiResponseDto.createOk(gameBoardList);
    }

    @GetMapping(value = "/{id}")
    private ApiResponseDto  getGameBoardDetail(@PathVariable("id") String id) {
        try {
            GameBoardDetailSummaryDto gameBoard = gameBoardService.getGameBoardDetail(id);
            return ApiResponseDto.createOk(gameBoard);
        } catch (Exception e) {
            log.error("Game board id not found: {}", id);
            return ApiResponseDto.createError("BOARD_DETAIL_FAILED", "조회 상세 내역이 없습니다");
        }
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ApiResponseDto<String> updateGameBoard(
            @Valid GameBoardUpdateDto gameBoardUpdateDto
    ) {
        log.info("Received request to update gameboard: {}", gameBoardUpdateDto);
        try {
            String imageUrl = null;
            if (gameBoardUpdateDto.getBoardImage() != null && !gameBoardUpdateDto.getBoardImage().isEmpty()) {
                imageUrl = firebaseStorageService.uploadImage(gameBoardUpdateDto.getBoardImage());
                gameBoardUpdateDto.setThumbnailUrl(imageUrl);
            } else {
                log.error("Image upload failed during update: {}", gameBoardUpdateDto);
            }
            gameBoardService.updateGameBoard(gameBoardUpdateDto);
            return ApiResponseDto.defaultOk();
        } catch (IOException e) {
            log.error("Image upload failed during add: {}", e.getMessage());
            return ApiResponseDto.createError("IMAGE_UPLOAD_FAILED", "프로필 이미지 업로드에 실패했습니다.");
        } catch (Exception e) {
            log.error("Board add failed: {}", e.getMessage());
            return ApiResponseDto.createError("BOARD_ADD_FAILED", "테스터 모집공고 등록이 실패하였습니다.");
        }
    }

    @PostMapping(value = "/delete")
    private ApiResponseDto<String> deleteGameBoard(@Valid String boardId) {
        gameBoardService.deleteGameBoard(boardId);
        return ApiResponseDto.defaultOk();
    }
}
