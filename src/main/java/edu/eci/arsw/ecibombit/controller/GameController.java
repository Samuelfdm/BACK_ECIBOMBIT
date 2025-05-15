package edu.eci.arsw.ecibombit.controller;

import edu.eci.arsw.ecibombit.Exception.GameException;
import edu.eci.arsw.ecibombit.dto.GameRequestDTO;
import edu.eci.arsw.ecibombit.dto.GameResponseDTO;
import edu.eci.arsw.ecibombit.model.Game;
import edu.eci.arsw.ecibombit.model.Player;
import edu.eci.arsw.ecibombit.service.GameService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.qos.logback.classic.Logger;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameController.class);

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<GameResponseDTO> createGame(@RequestBody GameRequestDTO request) {
        try {
            Game newGame = gameService.createGame(request.getRoomId(), request.getPlayers(), request.getConfig());
            GameResponseDTO responseDTO = new GameResponseDTO(
                    newGame.getId(), newGame.getPlayers(), newGame.getConfig(), newGame.getBoard()
            );
            return ResponseEntity.ok(responseDTO);
        } catch (GameException e) {
            logger.error("Error creating game: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{gameId}/finish")
    public ResponseEntity<Void> finishGame(@PathVariable String gameId, @RequestBody Game game) {
        try {
            gameService.finalizeGame(gameId, game);
            return ResponseEntity.ok().build();
        } catch (GameException e) {
            logger.error("Error finishing game for gameId {}: {}", gameId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Unexpected error while finishing game for gameId {}: {}", gameId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameByGameId(@PathVariable String gameId) {
        try {
            Game game = gameService.getGameByGameId(gameId);
            return ResponseEntity.ok(game);
        } catch (GameException e) {
            logger.error("Error fetching game by gameId {}: {}", gameId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching game by gameId {}: {}", gameId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}