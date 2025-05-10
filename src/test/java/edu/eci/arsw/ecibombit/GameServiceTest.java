package edu.eci.arsw.ecibombit;

import edu.eci.arsw.ecibombit.model.*;
import edu.eci.arsw.ecibombit.model.enums.GameStatus;
import edu.eci.arsw.ecibombit.repository.GameRepository;
import edu.eci.arsw.ecibombit.repository.PlayerRepository;
import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
import edu.eci.arsw.ecibombit.service.BoardService;
import edu.eci.arsw.ecibombit.service.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private BoardService boardService;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGame_successful() {
        
        String roomId = "room123";
        GameConfig config = new GameConfig();
        List<Player> players = List.of(new Player("user1", "bomber1", roomId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null),
                                        new Player("user2", "bomber2", roomId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null));
        Board board = new Board();
        when(boardService.generateBoard(any(), any())).thenReturn(board);
        when(userAccountRepository.findByUsername(anyString())).thenReturn(new UserAccount());

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        when(gameRepository.save(gameCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        
        Game game = gameService.createGame(roomId, players, config);

        
        assertEquals(roomId, game.getRoomId());
        assertEquals(GameStatus.WAITING, game.getStatus());
        assertNotNull(game.getStartTime());
        assertEquals(2, game.getPlayers().size());
        assertEquals(board, game.getBoard());

        Game savedGame = gameCaptor.getValue();
        assertNotNull(savedGame.getStatistics());
        assertEquals(5, savedGame.getStatistics().size());
    }

    @Test
    void testFinalizeGame_withUpdatedPlayers() {
        
        String gameId = "game123";
        Player player = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        Game game = new Game();
        game.setPlayers(new ArrayList<>(List.of(player)));

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        Player updatedPlayer = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        updatedPlayer.setScore(100);
        updatedPlayer.setKills(2);
        updatedPlayer.setDead(false);
        updatedPlayer.setWinner(true);
        updatedPlayer.setPlayerRank(1);
        updatedPlayer.setTimeAlive(120);
        updatedPlayer.setTotalBlocksDestroyed(5);
        updatedPlayer.setTotalBombsPlaced(3);
        updatedPlayer.setTotalMoves(50);
        updatedPlayer.setLeftGame(false);

        
        gameService.finalizeGame(gameId, List.of(updatedPlayer));

        
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(100, game.getPlayers().get(0).getScore());
        verify(playerRepository).save(any());
        verify(gameRepository).save(game);
    }

    @Test
    void testFinalizeGame_withUpdatedGameStats() {
        
        String gameId = "game456";
        Player player = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        Game originalGame = new Game();
        originalGame.setPlayers(new ArrayList<>(List.of(player)));
        originalGame.setStatistics(new HashMap<>(Map.of(
                "timeAlive", new ArrayList<>(),
                "totalBombsPlaced", new ArrayList<>(),
                "totalBlocksDestroyed", new ArrayList<>(),
                "totalMoves", new ArrayList<>(),
                "kills", new ArrayList<>()
        )));

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(originalGame));

        Player updated = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        updated.setScore(120);
        updated.setKills(3);
        updated.setTimeAlive(90);
        updated.setTotalBlocksDestroyed(6);
        updated.setTotalBombsPlaced(2);
        updated.setTotalMoves(40);
        updated.setWinner(true);

        Game updatedGame = new Game();
        updatedGame.setPlayers(List.of(updated));
        updatedGame.setTotalBlocksDestroyed(6);
        updatedGame.setTotalBombsPlaced(2);
        updatedGame.setTotalMoves(40);
        updatedGame.setKills(3);

        
        gameService.finalizeGame(gameId, updatedGame);

        
        assertEquals(GameStatus.FINISHED, originalGame.getStatus());
        assertEquals(1, originalGame.getStatistics().get("kills").size());
        verify(gameRepository).save(originalGame);
    }

    @Test
    void testGetGameByGameId_found() {
        String gameId = "game789";
        Game game = new Game();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        Optional<Game> result = gameService.getGameByGameId(gameId);

        assertTrue(result.isPresent());
        assertEquals(game, result.get());
    }

    @Test
    void testGetGameByGameId_notFound() {
        when(gameRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Game> result = gameService.getGameByGameId("nonexistent");

        assertFalse(result.isPresent());
    }


    @Test
    void testFinalizeGame_noPlayers() {

        String gameId = "game123";
        Game game = new Game();
        game.setPlayers(new ArrayList<>());
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        gameService.finalizeGame(gameId, Collections.emptyList());
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    @Test
    void testFinalizeGame_invalidGameId() {

        String gameId = "game999";
        Player player = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        Game game = new Game();
        game.setPlayers(new ArrayList<>(List.of(player)));

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> gameService.finalizeGame(gameId, List.of(player)));
    }

    @Test
    void testFinalizeGame_withIncompleteStats() {
        
        String gameId = "game456";
        Player player = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        Game originalGame = new Game();
        originalGame.setPlayers(new ArrayList<>(List.of(player)));

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(originalGame));

        
        Player updated = new Player("user1", "bomber1", gameId, 0, 0, false, false, false, 0, 0, 0, 0, 0, null);
        updated.setScore(120);
        updated.setKills(3); // stats could be incomplete

        
        gameService.finalizeGame(gameId, List.of(updated));

        
        assertEquals(GameStatus.FINISHED, originalGame.getStatus());
        assertNotNull(originalGame.getPlayers().get(0).getScore());  // Ensure score is updated
        verify(gameRepository).save(originalGame);
    }

    @Test
    void testGetGameByGameId_invalidId() {
        
        Optional<Game> result = gameService.getGameByGameId("invalidId");

        
        assertFalse(result.isPresent());
    }

}
