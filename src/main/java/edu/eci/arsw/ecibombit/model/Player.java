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
    private boolean winner; //Para saber si fue o no el ganador de la partida (o uno de los ganadores en caso de empate)
    private int playerRank;
    private int timeAlive;
    private UserAccount userAccount;
}