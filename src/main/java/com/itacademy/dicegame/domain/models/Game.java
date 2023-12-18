package com.itacademy.dicegame.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game {
    private Long id;
    private int dice1;
    private int dice2;
    private String result;
    @JsonIgnore
    private Player player;

    public Game(Long id, int dice1, int dice2, String result) {
        this.id = id;
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.result = result;
    }
}
