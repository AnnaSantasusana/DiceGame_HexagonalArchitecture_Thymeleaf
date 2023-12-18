package com.itacademy.dicegame.infrastructure.adapter.repository;

import com.itacademy.dicegame.infrastructure.adapter.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    boolean existsByName(String name);
    boolean existsByEmail(String email);
    Optional<PlayerEntity> findByEmail(String email);
}
