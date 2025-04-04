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
    private CellType type;
}