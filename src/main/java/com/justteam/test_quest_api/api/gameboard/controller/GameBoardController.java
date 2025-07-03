package com.justteam.test_quest_api.api.gameboard.controller;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardCreateDto;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardListDto;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardUpdateDto;
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
//@SecurityRequirement(name = "BearerAuth")
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
    private ApiResponseDto<List<GameBoardListDto>> listGameBoard(@Valid @ModelAttribute GameBoardListDto gameBoardListDto) {
        log.info("Received request to list all gameboards");
        List<GameBoardListDto>  gameBoardList = gameBoardService.listAllGameBoards(gameBoardListDto);

        return ApiResponseDto.createOk(gameBoardList);
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
                // This 'else' block is problematic for optional images.
                log.error("Image upload failed during update: {}", gameBoardUpdateDto);
                // It currently logs an error even if no image was intended to be updated,
                // and proceeds with the update logic potentially without an image URL.
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
