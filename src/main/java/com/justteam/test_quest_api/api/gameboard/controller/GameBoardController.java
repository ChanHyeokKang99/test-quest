package com.justteam.test_quest_api.api.gameboard.controller;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.gameboard.dto.GameBoardCreateDto;
import com.justteam.test_quest_api.api.gameboard.service.GameBoardService;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/gameboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class GameBoardController {
    private final FirebaseStorageService firebaseStorageService;
    private final GameBoardService gameBoardService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ApiResponseDto<String> createGameBoard(@Valid @RequestBody GameBoardCreateDto boardAddDto) {

            try {
                String imageUrl = null;
                // DTO 내부에 있는 profileImage 필드 사용
                if (boardAddDto.getBoardImage() != null && !boardAddDto.getBoardImage().isEmpty()) {
                    imageUrl = firebaseStorageService.uploadImage(boardAddDto.getBoardImage());
                    boardAddDto.setThumbnailUrl(imageUrl);
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
}
