package com.itacademy.dicegame.domain.ports;

import com.itacademy.dicegame.domain.models.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerPersistancePort {

    Player save(Player player);
    Optional<Player> findById(Long id);
    List<Player> findAll();
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    Optional<Player> findByEmail(String email);



}
