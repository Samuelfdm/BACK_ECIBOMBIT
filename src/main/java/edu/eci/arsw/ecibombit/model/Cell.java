package edu.eci.arsw.ecibombit.model;

import edu.eci.arsw.ecibombit.model.enums.CellType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Cell {
    private int x;
    private int y;
    private String playerId; // o username
    private CellType type;

    public Cell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.playerId = null;
    }
}