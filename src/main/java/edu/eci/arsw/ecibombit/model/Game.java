package edu.eci.arsw.ecibombit.model;

import edu.eci.arsw.ecibombit.model.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, List<Map<String, Object>>> statistics;

    public Game(String roomId, List<Player> players, GameConfig config) {
        this.roomId = roomId;
        this.players = players;
        this.config = config;
        this.status = GameStatus.WAITING;  // Assuming a default status, can be changed based on requirements
        this.startTime = null;
        this.endTime = null;
        this.board = null;
        this.totalBombsPlaced = 0;
        this.totalBlocksDestroyed = 0;
        this.totalMoves = 0;
        this.kills = 0;
        this.statistics = new HashMap<>();
    }

}