package com.justteam.test_quest_api.api.gameboard.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor // 모든 final 필드를 인자로 받는 생성자를 자동 생성
public class GameBoardPageResponse {
    private final List<GameBoardSummaryDto> gameBoards; // 현재 페이지의 게임 보드 목록
    private final boolean hasNext; // 다음 페이지가 있는지 여부

    // 필요하다면 Builder 패턴이나 다른 생성자를 추가할 수 있습니다.
}