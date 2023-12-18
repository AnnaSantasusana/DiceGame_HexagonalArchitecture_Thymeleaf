package com.itacademy.dicegame.infrastructure.rest.contoller;

import com.itacademy.dicegame.application.exceptions.CustomAuthenticationException;
import com.itacademy.dicegame.application.exceptions.DuplicatedPlayerNameException;
import com.itacademy.dicegame.application.service.JwtService;
import com.itacademy.dicegame.application.service.PlayerManagementService;
import com.itacademy.dicegame.domain.models.Player;
import com.itacademy.dicegame.domain.models.dto.GameDto;
import com.itacademy.dicegame.domain.models.dto.PlayerDto;
import com.itacademy.dicegame.domain.models.dto.request.AuthenticationRequest;
import com.itacademy.dicegame.domain.models.dto.request.RegisterRequest;
import com.itacademy.dicegame.domain.models.dto.response.AuthenticationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerManagementService playerManagementService;
    private final JwtService jwtService;

    public PlayerController(PlayerManagementService diceService, JwtService jwtService) {
        this.playerManagementService = diceService;
        this.jwtService = jwtService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerPlayer(@ModelAttribute @Valid RegisterRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            playerManagementService.registerPlayer(request);
            model.addAttribute("message", "Player registered successfully");
            return "redirect:/";
        } catch (DuplicatedPlayerNameException e) {
            return "redirect:/api/players/register?duplicatedName=true";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("authRequest", new AuthenticationRequest());
        return "login";
    }

    @PostMapping("/login")
    public String authenticatePlayer(@ModelAttribute @Valid AuthenticationRequest request, BindingResult result,
                                     HttpServletResponse response, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            AuthenticationResponse token = playerManagementService.authenticatePlayer(request);

            Cookie cookie = new Cookie("token", token.getToken());
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            response.addCookie(cookie);

            return "redirect:/api/players/dashboard";
        } catch (CustomAuthenticationException ex) {
            return "redirect:/api/players/login?incorrectCredentials=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @CookieValue(name = "token", required = false) String token) {
        if (token != null && jwtService.isTokenValid(token, (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {

            // Obtener información del usuario desde el token
            Player loggedInPlayer = playerManagementService.getPlayerFromToken(token);

            model.addAttribute("loggedInPlayerName", loggedInPlayer.getName());
            model.addAttribute("loggedInPlayerId", loggedInPlayer.getId());
            model.addAttribute("token", token);

            // Obtener todas las jugadas del jugador
            List<GameDto> playerGames = playerManagementService.getAllGamesByPlayerId(loggedInPlayer.getId());
            model.addAttribute("playerGames", playerGames);

            return "dashboard";
        } else {
            return "redirect:/api/players/login";
        }
    }

    @PostMapping("/createGame")
    public String createGame(@RequestParam("playerId") Long playerId, Model model) {
        GameDto createdGame = playerManagementService.createGame(playerId);

        model.addAttribute("gameResult", createdGame.getResult());
        model.addAttribute("dice1", createdGame.getDice1());
        model.addAttribute("dice2", createdGame.getDice2());

        return "redirect:/api/players/dashboard";
    }

    @PostMapping("/{playerId}")
    public String updatePlayerName(@PathVariable Long playerId, @RequestParam String name) {

        try {
            playerManagementService.modifyPlayerName(playerId, name);
            return "redirect:/api/players/dashboard";
        } catch (DuplicatedPlayerNameException e) {
            return "redirect:/api/players/dashboard?duplicatedName=true";
        }
    }

    @PostMapping("/delete/{playerId}")
    public String deletePlayerGames(@PathVariable Long playerId) {
        playerManagementService.deleteGames(playerId);
        return "redirect:/api/players/dashboard";
    }

    @GetMapping("/statistics")
    public String getAllPlayersSuccessRate(Model model) {
        List<PlayerDto> players = playerManagementService.getPlayersSuccessRate();
        model.addAttribute("players", players);

        if (!players.isEmpty()) {
            PlayerDto bestPlayer = playerManagementService.getBestPlayer();
            model.addAttribute("bestPlayer", bestPlayer);

            PlayerDto worstPlayer = playerManagementService.getWorstPlayer();
            model.addAttribute("worstPlayer", worstPlayer);

            String averageSuccessRate = playerManagementService.getAverageSuccessRate();
            model.addAttribute("averageSuccessRate", averageSuccessRate);
        }

        return "players";
    }


}
