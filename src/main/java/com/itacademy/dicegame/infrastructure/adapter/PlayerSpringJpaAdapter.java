package com.itacademy.dicegame.infrastructure.adapter;

import com.itacademy.dicegame.domain.models.Player;
import com.itacademy.dicegame.domain.ports.PlayerPersistancePort;
import com.itacademy.dicegame.infrastructure.adapter.entity.PlayerEntity;
import com.itacademy.dicegame.infrastructure.adapter.repository.PlayerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class PlayerSpringJpaAdapter implements PlayerPersistancePort {

    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;

    public PlayerSpringJpaAdapter(PlayerRepository playerRepository, ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Player save(Player player) {
        PlayerEntity playerEntity = modelMapper.map(player, PlayerEntity.class);
        PlayerEntity savedPlayerEntity = playerRepository.save(playerEntity);
        return modelMapper.map(savedPlayerEntity, Player.class);
    }

    @Override
    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id).map(playerEntity ->
                modelMapper.map(playerEntity, Player.class)
        );
    }

    @Override
    public List<Player> findAll() {
        return playerRepository.findAll()
                .stream()
                .map(playerEntity -> modelMapper.map(playerEntity, Player.class))
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return playerRepository.existsByName(name);
    }

    @Override
    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    @Override
    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email)
                .map(playerEntity -> modelMapper.map(playerEntity, Player.class));
    }

}
