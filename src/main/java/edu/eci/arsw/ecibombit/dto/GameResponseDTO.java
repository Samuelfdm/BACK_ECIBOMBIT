package edu.eci.arsw.ecibombit.dto;

import edu.eci.arsw.ecibombit.model.Board;
import edu.eci.arsw.ecibombit.model.GameConfig;
import edu.eci.arsw.ecibombit.model.Player;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
public class GameResponseDTO {
    private String gameId;
    private List<Player> players;
    private GameConfig config;
    private Board board;
}
