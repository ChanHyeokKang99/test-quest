package com.justteam.test_quest_api.api.gameboard.service;

import com.justteam.test_quest_api.api.gameboard.dto.GameBoardCreateDto;
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
//           Optional<User> userOptional =
//           Optional<User> userOptional = userRepository.findById(boardCreateDto.getUserId());
//           if(userOptional.isPresent()) {
//               User user = userOptional.get();
//               board.setUser(user);
//           }else {
//               throw new NotFound("회원정보가 없습니다");
//           }
           gameBoardRepository.save(board);
           return ApiResponseDto.createOk(board);
       } catch (Exception e) {
           throw new NotFound(e.getMessage());
       }
    }
}
