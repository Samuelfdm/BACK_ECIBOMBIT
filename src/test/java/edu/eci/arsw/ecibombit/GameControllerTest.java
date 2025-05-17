//package edu.eci.arsw.ecibombit;
//
//import edu.eci.arsw.ecibombit.Exception.GameException;
//import edu.eci.arsw.ecibombit.controller.GameController;
//import edu.eci.arsw.ecibombit.dto.GameRequestDTO;
//import edu.eci.arsw.ecibombit.dto.GameResponseDTO;
//import edu.eci.arsw.ecibombit.model.Game;
//import edu.eci.arsw.ecibombit.model.GameConfig;
//import edu.eci.arsw.ecibombit.model.Player;
//import edu.eci.arsw.ecibombit.service.GameService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import java.util.Arrays;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//public class GameControllerTest {
//
//    @Mock
//    private GameService gameService;
//    @InjectMocks
//    private GameController gameController;
//    private GameRequestDTO gameRequestDTO;
//    private Game game;
//    private GameConfig gameConfig;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        gameConfig = new GameConfig("map1", 3, 3);
//        gameRequestDTO = new GameRequestDTO("room1", Arrays.asList(new Player("Player1", "bomber1"), new Player("Player2", "bomber2")), gameConfig);
//        game = new Game("game1", Arrays.asList(new Player("Player1", "bomber1"), new Player("Player2", "bomber2")), gameConfig);
//        game.setId("gameId123"); // Simulamos que el juego tiene un ID asignado
//    }
//
//    @Test
//    void testCreateGame_Success() throws GameException {
//        // Arrange
//        GameResponseDTO expectedResponse = new GameResponseDTO("gameId123", Arrays.asList(new Player("Player1", "bomber1"), new Player("Player2", "bomber2")), gameConfig, null);
//        when(gameService.createGame(eq("room1"), anyList(), any(GameConfig.class))).thenReturn(game);
//
//        // Act
//        ResponseEntity<GameResponseDTO> response = gameController.createGame(gameRequestDTO);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(expectedResponse.getGameId(), response.getBody().getGameId());
//        assertEquals(expectedResponse.getPlayers().size(), response.getBody().getPlayers().size());
//        assertEquals(expectedResponse.getConfig().getMap(), response.getBody().getConfig().getMap());
//    }
//
//    @Test
//    void testCreateGame_Failure_GameException() throws GameException {
//        // Arrange
//        when(gameService.createGame(eq("room1"), anyList(), any(GameConfig.class))).thenThrow(new GameException("Error creating game"));
//
//        // Act
//        ResponseEntity<GameResponseDTO> response = gameController.createGame(gameRequestDTO);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void testCreateGame_Failure_UnexpectedException() throws GameException {
//        // Arrange
//        when(gameService.createGame(eq("room1"), anyList(), any(GameConfig.class))).thenThrow(new RuntimeException("Unexpected error"));
//
//        // Act
//        ResponseEntity<GameResponseDTO> response = gameController.createGame(gameRequestDTO);
//
//        // Assert
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void testFinishGame_Success() throws GameException {
//        // Arrange
//        doNothing().when(gameService).finalizeGame(eq("game1"), any(Game.class)); // Mock ahora espera un Game
//        when(gameService.getGameByGameId(eq("game1"))).thenReturn(game);
//
//        // Act
//        ResponseEntity<Void> response = gameController.finishGame("game1", game); // Ahora pasamos el objeto Game
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        verify(gameService, times(1)).finalizeGame(eq("game1"), eq(game));
//    }
//
//    @Test
//    void testFinishGame_Failure_GameException() throws GameException {
//        // Arrange
//        doThrow(new GameException("Error finishing game")).when(gameService).finalizeGame(eq("game1"), any(Game.class));
//
//        // Act
//        ResponseEntity<Void> response = gameController.finishGame("game1", game); // Ahora pasamos el objeto Game
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void testFinishGame_Failure_UnexpectedException() throws GameException {
//        // Arrange
//        doThrow(new RuntimeException("Unexpected error")).when(gameService).finalizeGame(eq("game1"), any(Game.class));
//
//        // Act
//        ResponseEntity<Void> response = gameController.finishGame("game1", game); // Ahora pasamos el objeto Game
//
//        // Assert
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Test
//    void testGetGameByGameId_Success() throws GameException {
//        // Arrange
//        when(gameService.getGameByGameId("game1")).thenReturn(game);
//
//        // Act
//        ResponseEntity<Game> response = gameController.getGameByGameId("game1");
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("game1", response.getBody().getRoomId());
//    }
//
//    @Test
//    void testGetGameByGameId_Failure_GameException() throws GameException {
//        // Arrange
//        when(gameService.getGameByGameId("game1")).thenThrow(new GameException("Game not found"));
//
//        // Act
//        ResponseEntity<Game> response = gameController.getGameByGameId("game1");
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void testGetGameByGameId_Failure_UnexpectedException() throws GameException {
//        // Arrange
//        when(gameService.getGameByGameId("game1")).thenThrow(new RuntimeException("Unexpected error"));
//
//        // Act
//        ResponseEntity<Game> response = gameController.getGameByGameId("game1");
//
//        // Assert
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//}