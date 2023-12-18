package com.itacademy.dicegame.application.service;

import com.itacademy.dicegame.application.exceptions.CustomAuthenticationException;
import com.itacademy.dicegame.application.exceptions.DuplicatedPlayerNameException;
import com.itacademy.dicegame.application.exceptions.EmptyPlayerListException;
import com.itacademy.dicegame.application.exceptions.PlayerNotFoundException;
import com.itacademy.dicegame.application.usecases.PlayerService;
import com.itacademy.dicegame.domain.models.Game;
import com.itacademy.dicegame.domain.models.Player;
import com.itacademy.dicegame.domain.models.Role;
import com.itacademy.dicegame.domain.models.dto.GameDto;
import com.itacademy.dicegame.domain.models.dto.PlayerDto;
import com.itacademy.dicegame.domain.models.dto.request.AuthenticationRequest;
import com.itacademy.dicegame.domain.models.dto.request.RegisterRequest;
import com.itacademy.dicegame.domain.models.dto.response.AuthenticationResponse;
import com.itacademy.dicegame.domain.ports.PlayerPersistancePort;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerManagementService implements PlayerService {

    public static final int MAX_VALUE = 6;
    public static final String NO_GAMES = "No played yet";
    private final PlayerPersistancePort playerPersistancePort;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public PlayerManagementService(PlayerPersistancePort playerPersistancePort, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.playerPersistancePort = playerPersistancePort;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void registerPlayer(RegisterRequest request) {
        Player player = new Player(
                verifyPlayerName(request.getName()),
                verifyPlayerEmail(request.getEmail()),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );
        playerPersistancePort.save(player);
    }

    @Override
    public AuthenticationResponse authenticatePlayer(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            Player player = playerPersistancePort.findByEmail(request.getEmail())
                    .orElseThrow();
            String jwtToken = jwtService.generateToken(player);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (BadCredentialsException ex) {
            throw new CustomAuthenticationException("Incorrect credentials");
        }
    }

    @Override
    public GameDto createGame(Long playerId) {
        Player player = getPlayerByIdOrThrowException(playerId);
        Game game = rollDices();
        player.getGames().add(game);
        game.setPlayer(player);
        playerPersistancePort.save(player);
        return modelMapper.map(game, GameDto.class);
    }

    @Override
    public List<PlayerDto> getPlayersSuccessRate() {
        List<Player> players = checkList(playerPersistancePort.findAll());
        List<PlayerDto> playersDto = new ArrayList<>(players.size());

        for(Player player: players) {
            playersDto.add(getSuccessRate(player));
        }
        return playersDto;
    }


    @Override
    public PlayerDto modifyPlayerName(Long playerId, String name) {
        Player player = getPlayerByIdOrThrowException(playerId);
        player.setName(verifyPlayerName(name));
        playerPersistancePort.save(player);
        return getSuccessRate(player);
    }

    @Override
    public List<GameDto> getAllGamesByPlayerId(Long playerId) {
        Player player = getPlayerByIdOrThrowException(playerId);
        List<Game> games = player.getGames();

        return games.stream()
                .map(game -> modelMapper.map(game, GameDto.class))
                .toList();
    }

    @Override
    public String getAverageSuccessRate() {
        List<PlayerDto> playersDto = getPlayersSuccessRate();
        long numPlayersPlayed = playersDto.stream().filter(p -> !p.getSuccessRate().equals(NO_GAMES)).count();
        double totalSuccessRate = playersDto.stream()
                .filter(p -> !p.getSuccessRate().equals(NO_GAMES))
                .mapToDouble(p -> Double.parseDouble(p.getSuccessRate().replace(',', '.')))
                .sum();
        return String.format(Locale.forLanguageTag("es-ES"), "%.2f", totalSuccessRate / numPlayersPlayed);
    }

    @Override
    public PlayerDto getWorstPlayer() {
        List<PlayerDto> playersDto = getPlayersSuccessRate();
        Optional<PlayerDto> result = playersDto.stream()
                .filter(p -> !p.getSuccessRate().equals(NO_GAMES))
                .min(Comparator.comparingDouble(p -> Double.parseDouble(p.getSuccessRate().replace(',', '.'))));
        return result.orElse(null);
    }

    @Override
    public PlayerDto getBestPlayer() {
        List<PlayerDto> playersDto = getPlayersSuccessRate();
        Optional<PlayerDto> result = playersDto.stream()
                .filter(p -> !p.getSuccessRate().equals(NO_GAMES))
                .max(Comparator.comparingDouble(p -> Double.parseDouble(p.getSuccessRate().replace(',', '.'))));
        return result.orElse(null);
    }

    @Override
    public void deleteGames(Long playerId) {
        Player player = getPlayerByIdOrThrowException(playerId);
        player.getGames().clear();
        playerPersistancePort.save(player);
    }

    public Player getPlayerFromToken(String token) {
        String username = jwtService.extractUsername(token);
        return playerPersistancePort.findByEmail(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found"));
    }

    private Player getPlayerByIdOrThrowException(Long playerId) {
        return playerPersistancePort.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + playerId));
    }

    private String verifyPlayerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Unknown";
        }

        if (playerPersistancePort.existsByName(name)) {
            throw new DuplicatedPlayerNameException("This name already exists, please choose another one.");
        }

        return name;
    }

    private String verifyPlayerEmail(String playerEmail) {
        boolean playerEmailExists = playerPersistancePort.existsByEmail(playerEmail);
        if(playerEmailExists) {
            throw new DuplicatedPlayerNameException("This email already exists, please choose another one.");
        }
        return playerEmail;
    }

    private PlayerDto getSuccessRate (Player player){
        long totalGames = player.getGames().size();
        long wins = player.getGames().stream().filter(g -> g.getResult().equals("Won")).count();
        PlayerDto playerToShow = modelMapper.map(player, PlayerDto.class);
        playerToShow.setSuccessRate(calculateSuccessRate(wins, totalGames));
        return playerToShow;
    }

    private String calculateSuccessRate(long wins, long totalGames) {
        if (totalGames > 0) {
            double successRate = (double) wins / totalGames * 100;
            return String.format(Locale.forLanguageTag("es-ES"), "%.2f", successRate);
        } else {
            return NO_GAMES;
        }
    }

    private List<Player> checkList(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new EmptyPlayerListException("No players found");
        }
        return players;
    }

    private Game rollDices(){
        Game game = new Game();
        int dice1 = (int) (Math.random() * MAX_VALUE) + 1;
        int dice2 = (int) (Math.random() * MAX_VALUE) + 1;
        String result = (dice1 + dice2 == 7) ? "Won" : "Lost";
        game.setDice1(dice1);
        game.setDice2(dice2);
        game.setResult(result);
        return game;
    }

}
