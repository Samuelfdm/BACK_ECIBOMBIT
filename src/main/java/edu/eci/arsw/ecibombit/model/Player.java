package edu.eci.arsw.ecibombit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "players")
public class Player {
    @Id
    private String id;
    private String username;
    private String character;
    private int score;
    private int kills;
    private boolean dead;
    private boolean leftGame;
    private boolean winner; 
    private int playerRank;
    private int timeAlive;
    private int totalBombsPlaced;
    private int totalBlocksDestroyed;
    private int totalMoves;
    private UserAccount userAccount;

    public Player(String username, String character) {
        this.username = username;
        this.character = character;
        this.score = 0;
        this.kills = 0;
        this.dead = false;
        this.leftGame = false;
        this.winner = false;
        this.playerRank = 0;
        this.timeAlive = 0;
        this.totalBombsPlaced = 0;
        this.totalBlocksDestroyed = 0;
        this.totalMoves = 0;
        this.userAccount = null;
    }
}