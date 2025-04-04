package edu.eci.arsw.ecibombit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GameConfig {

    private String map;   // Nombre del mapa
    private int time;     // Minutos
    private int items;    // Cantidad de ítems disponibles

}