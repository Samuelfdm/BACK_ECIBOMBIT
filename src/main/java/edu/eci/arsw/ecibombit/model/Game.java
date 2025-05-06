package edu.eci.arsw.ecibombit.model;

import edu.eci.arsw.ecibombit.model.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "games")
public class Game {

    @Id
    private String id;
    private String roomId; // ID o código de sala
    private List<Player> players;
    private GameConfig config;
    private GameStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Board board;
    private int totalBombsPlaced;
    private int totalBlocksDestroyed;
    private int totalMoves;
    private int kills;

}