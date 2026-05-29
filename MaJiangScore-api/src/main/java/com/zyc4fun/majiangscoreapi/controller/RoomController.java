package com.zyc4fun.majiangscoreapi.controller;

import com.zyc4fun.majiangscoreapi.common.ApiResponse;
import com.zyc4fun.majiangscoreapi.entity.AppUser;
import com.zyc4fun.majiangscoreapi.dto.RoomDtos;
import com.zyc4fun.majiangscoreapi.dto.ScoreHistoryDtos;
import com.zyc4fun.majiangscoreapi.service.AuthService;
import com.zyc4fun.majiangscoreapi.service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController extends BaseController {
    private final AuthService authService;
    private final RoomService roomService;

    public RoomController(AuthService authService, RoomService roomService) {
        this.authService = authService;
        this.roomService = roomService;
    }

    @PostMapping
    public ApiResponse<RoomDtos.RoomDto> create(@RequestHeader(value = "Authorization", required = false) String authorization) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(roomService.createRoom(user));
    }

    @PostMapping("/join")
    public ApiResponse<RoomDtos.RoomDto> join(@RequestHeader(value = "Authorization", required = false) String authorization,
                                              @RequestBody RoomDtos.JoinRoomRequest request) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(roomService.joinRoom(user, request.getRoomCode()));
    }

    @GetMapping("/{roomCode}")
    public ApiResponse<RoomDtos.RoomDto> detail(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                @PathVariable String roomCode) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(roomService.detail(roomCode, user));
    }

    @PostMapping("/{roomCode}/spend")
    public ApiResponse<RoomDtos.RoomDto> spend(@RequestHeader(value = "Authorization", required = false) String authorization,
                                               @PathVariable String roomCode,
                                               @RequestBody RoomDtos.SpendScoreRequest request) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(roomService.spend(user, roomCode, request.getPlayerId(), request.getAmount() == null ? 0 : request.getAmount()));
    }

    @PostMapping("/{roomCode}/batch-spend")
    public ApiResponse<RoomDtos.RoomDto> batchSpend(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                    @PathVariable String roomCode,
                                                    @RequestBody RoomDtos.BatchSpendRequest request) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(roomService.batchSpend(user, roomCode, request.getSpends()));
    }

    @PostMapping("/{roomCode}/settle")
    public ApiResponse<ScoreHistoryDtos.ScoreHistoryDto> settle(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                                @PathVariable String roomCode) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(roomService.settle(user, roomCode));
    }
}
