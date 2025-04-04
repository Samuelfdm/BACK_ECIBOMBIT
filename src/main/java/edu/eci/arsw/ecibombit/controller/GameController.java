package edu.eci.arsw.ecibombit.controller;

import edu.eci.arsw.ecibombit.dto.GameRequestDTO;
import edu.eci.arsw.ecibombit.model.Board;
import edu.eci.arsw.ecibombit.model.Game;
import edu.eci.arsw.ecibombit.model.Player;
import edu.eci.arsw.ecibombit.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody GameRequestDTO request) {
        Game newGame = gameService.createGame(request.getRoomId(), request.getPlayers(), request.getConfig());
        return ResponseEntity.ok(newGame);
    }

    @PutMapping("/{gameId}/finish")
    public ResponseEntity<Void> finishGame(@PathVariable String gameId, @RequestBody List<Player> players) {
        gameService.finalizeGame(gameId, players);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Game> getGameByRoomId(@PathVariable String roomId) {
        return gameService.getGameByRoomId(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{roomId}/board")
    public ResponseEntity<Board> getBoardByRoomId(@PathVariable String roomId) {
        return gameService.getGameByRoomId(roomId)
                .map(game -> ResponseEntity.ok(game.getBoard()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{roomId}/players")
    public ResponseEntity<List<Player>> getPlayersByRoomId(@PathVariable String roomId) {
        return gameService.getGameByRoomId(roomId)
                .map(game -> ResponseEntity.ok(game.getPlayers()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable String id, @RequestBody Game updatedGame) {
        if (!id.equals(updatedGame.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(gameService.updateGame(updatedGame));
    }

}