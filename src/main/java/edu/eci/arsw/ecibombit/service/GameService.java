package edu.eci.arsw.ecibombit.service;

import edu.eci.arsw.ecibombit.model.*;
import edu.eci.arsw.ecibombit.model.enums.GameStatus;
import edu.eci.arsw.ecibombit.repository.GameRepository;
import edu.eci.arsw.ecibombit.repository.PlayerRepository;
import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        game.setStartTime(LocalDateTime.now().plusSeconds(3));
        propertiesGame(game, 0,0,0,0);
        game.setBoard(generateBoard(config, incomingPlayers));
        List<Player> players = incomingPlayers.stream().map(p -> {
            propertiesPlayer(p, 0, 0, false, p.getCharacter() != null ? p.getCharacter() : "default", 
                                false, -1, -1, 0, 0, 0, false);
            UserAccount account = userAccountRepository.findByUsername(p.getUsername());
            if (account != null) {
                p.setUserAccount(account);
            }
            return p;
        }).toList();
        game.setPlayers(players);
        game.setStatistics(statisticsGame());
        return gameRepository.save(game);
    }  
    
    public Map<String, List<Map<String, Object>>> statisticsGame() {
    
        Map<String, List<Map<String, Object>>> stats = new HashMap<>();
        stats.put("timeAlive", new ArrayList<>());
        stats.put("totalBombsPlaced", new ArrayList<>());
        stats.put("totalBlocksDestroyed", new ArrayList<>());
        stats.put("totalMoves", new ArrayList<>());
        stats.put("kills", new ArrayList<>());
        return stats;

    }

    private void propertiesPlayer(Player p, int score, int kills, boolean dead, String character,
                               boolean winner, int playerRank, int timeAlive,
                               int totalBlocksDestroyed, int totalBombsPlaced, int totalMoves, boolean leftGame) {
        // Puntuación
        p.setScore(score);
        p.setKills(kills);
        // Estado
        p.setDead(dead);
        p.setCharacter(character);
        p.setWinner(winner);
        p.setPlayerRank(playerRank);
        p.setTimeAlive(timeAlive);
        // Estadísticas
        p.setTotalBlocksDestroyed(totalBlocksDestroyed);
        p.setTotalBombsPlaced(totalBombsPlaced);
        p.setTotalMoves(totalMoves);
        p.setLeftGame(leftGame);
    }

    private void propertiesGame(Game game, int totalBlocksDestroyed,int totalBombsPlaced, int totalMoves, int kills){
        game.setTotalBlocksDestroyed(totalBlocksDestroyed);
        game.setTotalBombsPlaced(totalBombsPlaced);
        game.setTotalMoves(totalMoves);
        game.setKills(kills);
    }


    public void finalizeGame(String gameId, List<Player> updatedPlayers) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        // Marcar como finalizado
        game.setStatus(GameStatus.FINISHED);
        game.setEndTime(LocalDateTime.now());
        
        List<Player> gamePlayers = game.getPlayers();

        for (Player updated : updatedPlayers) {
            // Buscar en los jugadores del juego por username
            Player player = gamePlayers.stream()
                    .filter(p -> p.getUsername().equals(updated.getUsername()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Player with username " + updated.getUsername() + " not found in game " + gameId));

                propertiesPlayer(player, updated.getScore(), updated.getKills(), updated.isDead(), updated.getCharacter(), 
                                    updated.isWinner(), updated.getPlayerRank(), updated.getTimeAlive(), 
                                    updated.getTotalBlocksDestroyed(), updated.getTotalBombsPlaced(), updated.getTotalMoves(), updated.isLeftGame());
                playerRepository.save(player);
        }
        gameRepository.save(game);
    }

    public void finalizeGame(String gameId, Game updatedGame) {
        // Buscar el juego por su ID
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
    
        //Creacion de estadisticas
        Map<String, List<Map<String, Object>>> stats = game.getStatistics();
        List<Player> gamePlayers = game.getPlayers();
    
        // Actualizar jugadores
        for (Player updated : updatedGame.getPlayers()) {
            Player player = gamePlayers.stream()
                    .filter(p -> p.getUsername().equals(updated.getUsername()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Player with username " + updated.getUsername() + " not found in game " + gameId));

            // Actualizar jugador
            propertiesPlayer(player, updated.getScore(), updated.getKills(), updated.isDead(), updated.getCharacter(), 
                                updated.isWinner(), updated.getPlayerRank(), updated.getTimeAlive(), updated.getTotalBlocksDestroyed(), 
                                updated.getTotalBombsPlaced(), updated.getTotalMoves(), updated.isLeftGame());
            playerRepository.save(player);

            // Agregar jugador a las estadisticas
            String name = player.getUsername();
            String character = colorPlayer(player.getCharacter());
            stats.get("timeAlive").add(Map.of("id", name, "name", name, "value", player.getTimeAlive(), "color", character));
            stats.get("totalBombsPlaced").add(Map.of("id", name, "name", name, "value", player.getTotalBombsPlaced(), "color", character));
            stats.get("totalBlocksDestroyed").add(Map.of("id", name, "name", name, "value", player.getTotalBlocksDestroyed(), "color", character));
            stats.get("totalMoves").add(Map.of("id", name, "name", name, "value", player.getTotalMoves(), "color", character));
            stats.get("kills").add(Map.of("id", name, "name", name, "value", player.getKills(), "color", character));
        }

        // Actualizar estado general del juego
        game.setStatus(GameStatus.FINISHED);
        propertiesGame(game, updatedGame.getTotalBlocksDestroyed(), updatedGame.getTotalBombsPlaced(), updatedGame.getTotalMoves(), updatedGame.getKills());
        gameRepository.save(game);
    }

    public String colorPlayer(String character){
        switch (character.toLowerCase()) {
            case "bomber4":
                return "#7B61FF";
            case "bomber3":
                return "#3498DB";
            case "bomber2":
                return "#F39C12";
            case "bomber1":
                return "#2ECC71";
            default:
                return "#CCCCCC"; 
        }

    }

    public Optional<Game> getGameByGameId(String gameId) {
        return gameRepository.findById(gameId);
    }

    private Board generateBoard(GameConfig config, List<Player> incomingPlayers) {
        return boardService.generateBoard(config, incomingPlayers);
    }
}