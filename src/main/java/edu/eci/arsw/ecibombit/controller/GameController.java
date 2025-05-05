package edu.eci.arsw.ecibombit.controller;

import edu.eci.arsw.ecibombit.dto.GameRequestDTO;
import edu.eci.arsw.ecibombit.dto.GameResponseDTO;
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
    public ResponseEntity<GameResponseDTO> createGame(@RequestBody GameRequestDTO request) {
        Game newGame = gameService.createGame(request.getRoomId(), request.getPlayers(), request.getConfig());
        return ResponseEntity.ok(new GameResponseDTO(
                newGame.getId(), newGame.getPlayers(), newGame.getConfig(), newGame.getBoard()
        ));
    }

    @PutMapping("/{gameId}/finish")
    public ResponseEntity<Void> finishGame(@PathVariable String gameId, @RequestBody List<Player> players) {
        gameService.finalizeGame(gameId, players);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameByGameId(@PathVariable String gameId) {
        return gameService.getGameByGameId(gameId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}