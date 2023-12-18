package com.itacademy.dicegame.infrastructure.adapter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int dice1;
    private int dice2;
    private String result;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

}
