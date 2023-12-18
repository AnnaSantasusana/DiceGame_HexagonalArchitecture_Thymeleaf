package com.itacademy.dicegame.domain.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayerDto {

    private Long id;
    private String name;
    private String successRate;

    public PlayerDto(String name) {
        this.name = name;
    }

}
