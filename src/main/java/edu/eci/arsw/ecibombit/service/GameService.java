package edu.eci.arsw.ecibombit.service;

import edu.eci.arsw.ecibombit.Exception.GameException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    public Game createGame(String roomId, List<Player> incomingPlayers, GameConfig config) throws GameException {

        //validacion de datos

        List<Player> players =  validation(roomId,incomingPlayers,config);

        // Despues de la verificacion de jugadores, roomID y configuraciones se puede crear el juego

        Game game = new Game();
        game.setRoomId(roomId);
        game.setConfig(config);
        game.setStatus(GameStatus.WAITING);
        game.setStartTime(LocalDateTime.now().plusSeconds(3));
        propertiesGame(game, 0,0,0,0);
        game.setBoard(generateBoard(config, players));
        game.setPlayers(players);
        game.setStatistics(statisticsGame());
        return gameRepository.save(game);
    }  

    public List<Player> validation(String roomId, List<Player> incomingPlayers, GameConfig config) throws GameException {
        if (roomId == null) throw new GameException(GameException.ROOMID_INVALID);
        if (incomingPlayers == null) throw new GameException(GameException.PLAYERS_INVALID);
        if (config == null) throw new GameException(GameException.CONFIG_INVALID);

        Set<String> uniqueUsernames = new HashSet<>();
        List<Player> validatedPlayers = new ArrayList<>();

        for (Player player : incomingPlayers) {
            if (!uniqueUsernames.add(player.getUsername())) {
                throw new GameException(GameException.DUPLICATE_USERNAME + ": " + player.getUsername());
            }

            UserAccount account = userAccountRepository.findByUsername(player.getUsername());
            if (account == null) {
                throw new GameException(GameException.PLAYER_NOT_FOUND, player.getUsername());
            }
            propertiesPlayer(player, 0, 0, false, player.getCharacter() != null ? player.getCharacter() : "default", 
                                    false, -1, -1, 0, 0, 0, false);
            player.setUserAccount(account);
            validatedPlayers.add(player);
        }
        
        if (config.getItems() <= 0 || config.getTime() <= 0) {
            throw new GameException(GameException.CONFIG_INVALID);
        }

        return validatedPlayers;
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

    public void propertiesPlayer(Player p, Integer score, Integer kills, Boolean dead, String character,
                               Boolean winner, Integer playerRank, Integer timeAlive,
                               Integer totalBlocksDestroyed, Integer totalBombsPlaced, Integer totalMoves, Boolean leftGame) throws GameException {

        if (p == null) throw new GameException(GameException.PLAYER_NOT_FOUND);
        if (score == null || kills == null || dead == null || character == null ||
            winner == null || playerRank == null || timeAlive == null ||
            totalBlocksDestroyed == null || totalBombsPlaced == null ||
            totalMoves == null || leftGame == null) {
            throw new GameException(GameException.NULL_PROPERTY_PLAYER);
        }

        p.setScore(score);
        p.setKills(kills);
        p.setDead(dead);
        p.setCharacter(character);
        p.setWinner(winner);
        p.setPlayerRank(playerRank);
        p.setTimeAlive(timeAlive);
        p.setTotalBlocksDestroyed(totalBlocksDestroyed);
        p.setTotalBombsPlaced(totalBombsPlaced);
        p.setTotalMoves(totalMoves);
        p.setLeftGame(leftGame);
    }


    public void propertiesGame(Game game, Integer totalBlocksDestroyed, Integer totalBombsPlaced, Integer totalMoves, Integer kills) throws GameException {

        if (game == null) throw new GameException(GameException.GAME_NOT_FOUND);
        if (totalBlocksDestroyed == null || totalBombsPlaced == null || totalMoves == null || kills == null) {
            throw new GameException(GameException.NULL_PROPERTY_GAME);
        }

        game.setTotalBlocksDestroyed(totalBlocksDestroyed);
        game.setTotalBombsPlaced(totalBombsPlaced);
        game.setTotalMoves(totalMoves);
        game.setKills(kills);
    }


/**
    public void finalizeGame(String gameId, List<Player> updatedPlayers) throws GameException {
        Game game = getGameByGameId(gameId);
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
*/
    public void finalizeGame(String gameId, Game updatedGame) throws GameException {
        // Buscar el juego por su ID
        Game game = getGameByGameId(gameId);
    
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
            stats.get("timeAlive").add(Map.of("id", name, "name", name, "value", (player.getTimeAlive())/60, "color", character));
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

    public Game getGameByGameId(String gameId) throws GameException {
        if(gameId == null) throw new GameException(GameException.GAMEID_INVALID);
        return gameRepository.findById(gameId)
                .orElseThrow(() ->new GameException(GameException.GAME_NOT_FOUND));
    }


    private Board generateBoard(GameConfig config, List<Player> incomingPlayers) {
        return boardService.generateBoard(config, incomingPlayers);
    }
}