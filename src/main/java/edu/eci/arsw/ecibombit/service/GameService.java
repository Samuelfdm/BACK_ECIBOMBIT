package edu.eci.arsw.ecibombit.service;

import edu.eci.arsw.ecibombit.model.*;
import edu.eci.arsw.ecibombit.model.enums.GameStatus;
import edu.eci.arsw.ecibombit.repository.GameRepository;
import edu.eci.arsw.ecibombit.repository.PlayerRepository;
import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final BoardService boardService;
    private final PlayerRepository playerRepository;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public GameService(GameRepository gameRepository, BoardService boardService, PlayerRepository playerRepository, UserAccountRepository userAccountRepository) {
        this.gameRepository = gameRepository;
        this.boardService = boardService;
        this.playerRepository = playerRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public Game createGame(String roomId, List<Player> incomingPlayers, GameConfig config) {
        Game game = new Game();
        game.setRoomId(roomId);
        game.setConfig(config);
        game.setStatus(GameStatus.WAITING);
        game.setStartTime(LocalDateTime.now());
        game.setBoard(generateBoard(config));
        List<Player> players = incomingPlayers.stream().map(p -> {
            p.setScore(0);
            p.setKills(0);
            //p.setGame(game); // asigna la relación
            p.setCharacter(p.getCharacter() != null ? p.getCharacter() : "default");
            UserAccount account = userAccountRepository.findByUsername(p.getUsername());
            //Si el username no es unico, se puede rompertodo.
            //Lo ideal sería hacer esa relacion por oid o _id del usuario (si tenes acceso en frontend).
            if (account != null) {
                p.setUserAccount(account);
            }
            return p;
        }).toList();
        game.setPlayers(players);
        return gameRepository.save(game);
    }

    public void finalizeGame(String gameId, List<Player> updatedPlayers) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        // Marcar como finalizado
        game.setStatus(GameStatus.FINISHED);
        gameRepository.save(game);

        // Actualizar jugadores
        for (Player updated : updatedPlayers) {
            Player player = playerRepository.findById(updated.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Player not found: " + updated.getId()));

            player.setScore(updated.getScore());
            player.setKills(updated.getKills());
            player.setCharacter(updated.getCharacter());

            // Asociar el juego
            // player.setGame(game);


            playerRepository.save(player);
        }
    }

    public Optional<Game> getGameByRoomId(String roomId) {
        return gameRepository.findByRoomId(roomId);
    }

    public Game updateGame(Game game) {
        return gameRepository.save(game);
    }

    private Board generateBoard(GameConfig config) {
        return boardService.generateBoard(config);
    }
}