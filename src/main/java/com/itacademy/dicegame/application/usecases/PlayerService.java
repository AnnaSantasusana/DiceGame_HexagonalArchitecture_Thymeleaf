package com.itacademy.dicegame.application.usecases;

import com.itacademy.dicegame.domain.models.dto.GameDto;
import com.itacademy.dicegame.domain.models.dto.PlayerDto;
import com.itacademy.dicegame.domain.models.dto.request.AuthenticationRequest;
import com.itacademy.dicegame.domain.models.dto.request.RegisterRequest;
import com.itacademy.dicegame.domain.models.dto.response.AuthenticationResponse;

import java.util.List;

public interface PlayerService {

    void registerPlayer(RegisterRequest request);
    AuthenticationResponse authenticatePlayer(AuthenticationRequest request);
    GameDto createGame(Long playerId);
    List<PlayerDto> getPlayersSuccessRate();
    PlayerDto modifyPlayerName(Long playerId, String name);
    List<GameDto> getAllGamesByPlayerId(Long playerId);
    String getAverageSuccessRate();
    PlayerDto getWorstPlayer();
    PlayerDto getBestPlayer();
    void deleteGames(Long playerId);

}
