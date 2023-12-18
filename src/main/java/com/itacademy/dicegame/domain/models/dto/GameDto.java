package com.itacademy.dicegame.domain.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameDto {

    private Long id;
    private int dice1;
    private int dice2;
    private String result;
}
