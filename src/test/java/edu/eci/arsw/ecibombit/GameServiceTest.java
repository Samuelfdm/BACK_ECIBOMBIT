//package edu.eci.arsw.ecibombit;
//
//import edu.eci.arsw.ecibombit.Exception.GameException;
//import edu.eci.arsw.ecibombit.model.*;
//import edu.eci.arsw.ecibombit.model.enums.GameStatus;
//import edu.eci.arsw.ecibombit.repository.GameRepository;
//import edu.eci.arsw.ecibombit.repository.PlayerRepository;
//import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
//import edu.eci.arsw.ecibombit.service.BoardService;
//import edu.eci.arsw.ecibombit.service.GameService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class GameServiceTest {
//
//    @Mock
//    private GameRepository gameRepository;
//
//    @Mock
//    private PlayerRepository playerRepository;
//
//    @Mock
//    private UserAccountRepository userAccountRepository;
//
//    @Mock
//    private BoardService boardService;
//
//    @InjectMocks
//    private GameService gameService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private Player mockPlayer(String username) {
//        Player p = new Player();
//        p.setUsername(username);
//        p.setCharacter("bomber1");
//        return p;
//    }
//
//    private GameConfig mockGameConfig() {
//        GameConfig config = new GameConfig();
//        config.setItems(5);
//        config.setTime(300);
//        return config;
//    }
//
//    @Test
//    public void testCreateGameSuccess() throws GameException {
//        String roomId = "room123";
//        List<Player> players = List.of(mockPlayer("user1"), mockPlayer("user2"));
//
//        UserAccount ua1 = new UserAccount(); ua1.setUsername("user1");
//        UserAccount ua2 = new UserAccount(); ua2.setUsername("user2");
//
//        when(userAccountRepository.findByUsername("user1")).thenReturn(ua1);
//        when(userAccountRepository.findByUsername("user2")).thenReturn(ua2);
//        when(gameRepository.findByRoomId(roomId)).thenReturn(Optional.empty());
//        when(boardService.generateBoard(any(), any())).thenReturn(new Board());
//        when(gameRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        GameConfig config = mockGameConfig();
//        Game createdGame = gameService.createGame(roomId, players, config);
//
//        assertEquals(GameStatus.WAITING, createdGame.getStatus());
//        assertEquals(2, createdGame.getPlayers().size());
//        assertEquals(roomId, createdGame.getRoomId());
//    }
//
//    @Test
//    public void testCreateGameThrowsOnDuplicateUsername() {
//        List<Player> players = List.of(mockPlayer("user1"), mockPlayer("user1"));
//        GameConfig config = mockGameConfig();
//        String roomId = "room123";
//
//        assertThrows(GameException.class, () -> {
//            gameService.createGame(roomId, players, config);
//        });
//    }
//
//    @Test
//    public void testCreateGameThrowsWhenUserNotFound() {
//        String roomId = "room123";
//        List<Player> players = List.of(mockPlayer("user1"));
//        GameConfig config = mockGameConfig();
//
//        when(userAccountRepository.findByUsername("user1")).thenReturn(null);
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.createGame(roomId, players, config);
//        });
//
//        assertEquals(GameException.PLAYER_NOT_FOUND + "user1", exception.getMessage());
//    }
//
//
//    @Test
//    public void testCreateGameAssignsDefaultCharacterIfNull() throws GameException {
//        String roomId = "room123";
//        Player player = mockPlayer("user1");
//        player.setCharacter(null); // <--- clave
//
//        UserAccount account = new UserAccount(); account.setUsername("user1");
//
//        when(userAccountRepository.findByUsername("user1")).thenReturn(account);
//        when(gameRepository.findByRoomId(roomId)).thenReturn(Optional.empty());
//        when(boardService.generateBoard(any(), any())).thenReturn(new Board());
//        when(gameRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        GameConfig config = mockGameConfig();
//        Game createdGame = gameService.createGame(roomId, List.of(player), config);
//
//        assertEquals("default", createdGame.getPlayers().get(0).getCharacter());
//    }
//
//    @Test
//    public void testCreateGameThrowsOnDuplicateUsername1() {
//        List<Player> players = List.of(mockPlayer("user1"), mockPlayer("user1"));
//        GameConfig config = mockGameConfig();
//        String roomId = "room123";
//
//        assertThrows(GameException.class, () -> {
//            gameService.createGame(roomId, players, config);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayerThrowsOnNullFields() {
//        Player player = new Player(); // puedes usar mock también
//
//        // Probar que lanzar excepción cuando un campo obligatorio es null
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, null, 1, true, "char", true, 1, 10, 10, 10, 10, false);
//        });
//        assertEquals(GameException.NULL_PROPERTY_PLAYER, exception.getMessage());
//    }
//
//    @Test
//    public void testPropertiesPlayerThrowsOnNullPlayer() {
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(null, 10, 2, false, "char", false, 3, 40, 5, 6, 7, true);
//        });
//        assertEquals(GameException.PLAYER_NOT_FOUND, exception.getMessage());
//    }
//
//    @Test
//    public void testPropertiesPlayerValid() {
//        Player player = new Player();
//
//        assertDoesNotThrow(() -> {
//            gameService.propertiesPlayer(player, 10, 2, false, "char", true, 1, 30, 20, 5, 50, false);
//        });
//
//        assertEquals(10, player.getScore());
//        assertEquals(2, player.getKills());
//        assertFalse(player.isDead());
//        assertEquals("char", player.getCharacter());
//        assertTrue(player.isWinner());
//        assertEquals(1, player.getPlayerRank());
//        assertEquals(30, player.getTimeAlive());
//        assertEquals(20, player.getTotalBlocksDestroyed());
//        assertEquals(5, player.getTotalBombsPlaced());
//        assertEquals(50, player.getTotalMoves());
//        assertFalse(player.isLeftGame());
//    }
//
//    @Test
//    public void testPropertiesGameThrowsOnNullFields() {
//        Game game = new Game(); // o mock
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, null, 1, 1, 1);
//        });
//        assertEquals(GameException.NULL_PROPERTY_GAME, exception.getMessage());
//    }
//
//    @Test
//    public void testPropertiesGameThrowsOnNullFields1() {
//        Game game = new Game(); // o mock
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, null, 1, 1, 1);
//        });
//        assertEquals(GameException.NULL_PROPERTY_GAME, exception.getMessage());
//    }
//
//    @Test
//    public void testPropertiesGameValid() {
//        Game game = new Game();
//
//        assertDoesNotThrow(() -> {
//            gameService.propertiesGame(game, 20, 10, 30, 5);
//        });
//
//        assertEquals(20, game.getTotalBlocksDestroyed());
//        assertEquals(10, game.getTotalBombsPlaced());
//        assertEquals(30, game.getTotalMoves());
//        assertEquals(5, game.getKills());
//    }
//
//    @Test
//    public void testPropertiesGameValid1() {
//        Game game = new Game();
//
//        assertDoesNotThrow(() -> {
//            gameService.propertiesGame(game, 20, 10, 30, 5);
//        });
//
//        assertEquals(20, game.getTotalBlocksDestroyed());
//        assertEquals(10, game.getTotalBombsPlaced());
//        assertEquals(30, game.getTotalMoves());
//        assertEquals(5, game.getKills());
//    }
//
//    @Test
//    public void testCreateGameThrowsOnInvalidConfig() {
//        String roomId = "room123";
//        List<Player> players = List.of(mockPlayer("user1"), mockPlayer("user2"));
//
//        // Mock user accounts to bypass user not found
//        UserAccount ua1 = new UserAccount(); ua1.setUsername("user1");
//        UserAccount ua2 = new UserAccount(); ua2.setUsername("user2");
//
//        when(userAccountRepository.findByUsername("user1")).thenReturn(ua1);
//        when(userAccountRepository.findByUsername("user2")).thenReturn(ua2);
//
//        GameConfig invalidConfig = new GameConfig();
//        invalidConfig.setItems(0); // Invalid
//        invalidConfig.setTime(0);  // Invalid
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.createGame(roomId, players, invalidConfig);
//        });
//
//        assertEquals(GameException.CONFIG_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testCreateGameThrowsWhenConfigIsNull() {
//        List<Player> players = List.of(mockPlayer("user1"));
//        assertThrows(GameException.class, () -> {
//            gameService.createGame("room123", players, null);
//        });
//    }
//
//    @Test
//    public void testCreateGameThrowsWhenConfigValuesInvalid() {
//        GameConfig config = new GameConfig();
//        config.setItems(0); // o negativo
//        config.setTime(0);  // o negativo
//        List<Player> players = List.of(mockPlayer("user1"));
//
//        UserAccount ua = new UserAccount(); ua.setUsername("user1");
//        when(userAccountRepository.findByUsername("user1")).thenReturn(ua);
//        when(gameRepository.findByRoomId("room123")).thenReturn(Optional.empty());
//
//        assertThrows(GameException.class, () -> {
//            gameService.createGame("room123", players, config);
//        });
//    }
//
//    @Test
//    public void testPropertiesGame_GameIsNull_ThrowsException() {
//        Game game = null;
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, 10, 5, 20, 1);
//        });
//
//        assertEquals(GameException.GAME_NOT_FOUND, exception.getMessage());
//    }
//
//    @Test
//    public void testPropertiesGame_totalBlocksDestroyedIsNull_ThrowsException() {
//        Game game = new Game();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, null, 5, 20, 1);
//        });
//    }
//
//    @Test
//    public void testPropertiesGame_totalBombsPlacedIsNull_ThrowsException() {
//        Game game = new Game();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, 10, null, 20, 1);
//        });
//    }
//
//    @Test
//    public void testPropertiesGame_totalMovesIsNull_ThrowsException() {
//        Game game = new Game();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, 10, 5, null, 1);
//        });
//    }
//
//    @Test
//    public void testPropertiesGame_killsIsNull_ThrowsException() {
//        Game game = new Game();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesGame(game, 10, 5, 20, null);
//        });
//    }
//
//    @Test
//    public void testPropertiesGame_AllValid_NoExceptionThrown() {
//        Game game = new Game();
//
//        assertDoesNotThrow(() -> {
//            gameService.propertiesGame(game, 10, 5, 20, 1);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_PlayerIsNull_ThrowsException() {
//        Player player = null;
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, 100, 20, 5, 30, false);
//        });
//
//        assertEquals(GameException.PLAYER_NOT_FOUND, exception.getMessage());
//    }
//
//    @Test
//    public void testPropertiesPlayer_ScoreIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, null, 10, false, "Character", true, 1, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_KillsIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, null, false, "Character", true, 1, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_DeadIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, null, "Character", true, 1, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_CharacterIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, null, true, 1, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_WinnerIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", null, 1, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_PlayerRankIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, null, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_TimeAliveIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, null, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_TotalBlocksDestroyedIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, 100, null, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_TotalBombsPlacedIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, 100, 20, null, 30, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_TotalMovesIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, 100, 20, 5, null, false);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_LeftGameIsNull_ThrowsException() {
//        Player player = new Player();
//
//        assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, 100, 20, 5, 30, null);
//        });
//    }
//
//    @Test
//    public void testPropertiesPlayer_AllValid_NoExceptionThrown() {
//        Player player = new Player();
//
//        assertDoesNotThrow(() -> {
//            gameService.propertiesPlayer(player, 100, 10, false, "Character", true, 1, 100, 20, 5, 30, false);
//        });
//    }
//
//    @Test
//    public void testValidation_ConfigIsNull_ThrowsException() {
//        String roomId = "room1";
//        List<Player> incomingPlayers = new ArrayList<>();
//        GameConfig config = null;
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.validation(roomId, incomingPlayers, config);
//        });
//
//        assertEquals(GameException.CONFIG_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testValidation_ItemsIsZero_ThrowsException() {
//        String roomId = "room1";
//        List<Player> incomingPlayers = new ArrayList<>();
//        GameConfig config = new GameConfig("default",0, 10); // Items = 0, Time = 10
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.validation(roomId, incomingPlayers, config);
//        });
//
//        assertEquals(GameException.CONFIG_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testValidation_TimeIsZero_ThrowsException() {
//        String roomId = "room1";
//        List<Player> incomingPlayers = new ArrayList<>();
//        GameConfig config = new GameConfig("default",10, 0); // Items = 10, Time = 0
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.validation(roomId, incomingPlayers, config);
//        });
//
//        assertEquals(GameException.CONFIG_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testValidation_ItemsAndTimeAreZero_ThrowsException() {
//        String roomId = "room1";
//        List<Player> incomingPlayers = new ArrayList<>();
//        GameConfig config = new GameConfig("default",0, 0); // Items = 0, Time = 0
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.validation(roomId, incomingPlayers, config);
//        });
//
//        assertEquals(GameException.CONFIG_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testValidation_ValidConfig_NoException() {
//        String roomId = "room1";
//        List<Player> incomingPlayers = new ArrayList<>();
//        GameConfig config = new GameConfig("default", 10, 10); // Items = 10, Time = 10
//
//        assertDoesNotThrow(() -> {
//            gameService.validation(roomId, incomingPlayers, config);
//        });
//    }
//
//    @Test
//    public void testGetGameByGameIdNotFound() {
//        when(gameRepository.findById("invalid")).thenReturn(Optional.empty());
//        assertThrows(GameException.class, () -> {
//            gameService.getGameByGameId("invalid");
//        });
//    }
//
//    @Test
//    public void testColorPlayer() {
//        assertEquals("#7B61FF", gameService.colorPlayer("bomber4"));
//        assertEquals("#CCCCCC", gameService.colorPlayer("unknown"));
//    }
//
//
//
//    @Test
//    public void testCreateGameInvalidRoomId() {
//        String invalidRoomId = null;
//        List<Player> players = List.of(mockPlayer("user1"), mockPlayer("user2"));
//        GameConfig config = mockGameConfig();
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.createGame(invalidRoomId, players, config);
//        });
//
//        assertEquals(GameException.ROOMID_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testCreateGameInvalidPlayers() {
//        String roomId = "room123";
//        List<Player> invalidPlayers = null;
//        GameConfig config = mockGameConfig();
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.createGame(roomId, invalidPlayers, config);
//        });
//
//        assertEquals(GameException.PLAYERS_INVALID, exception.getMessage());
//    }
//
//    @Test
//    public void testCreateGameInvalidConfig() {
//        String roomId = "room123";
//        List<Player> players = List.of(mockPlayer("user1"), mockPlayer("user2"));
//
//        GameConfig invalidConfig = new GameConfig();
//        invalidConfig.setItems(0);
//        invalidConfig.setTime(0);
//
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.createGame(roomId, players, invalidConfig);
//        });
//        System.out.println(exception.getMessage());
//        assertEquals("The player could not be found.user1", exception.getMessage());
//    }
//
//
//
//    @Test
//    public void testColorPlayerValidCharacter() {
//        assertEquals("#7B61FF", gameService.colorPlayer("bomber4"));
//        assertEquals("#3498DB", gameService.colorPlayer("bomber3"));
//        assertEquals("#F39C12", gameService.colorPlayer("bomber2"));
//        assertEquals("#2ECC71", gameService.colorPlayer("bomber1"));
//        assertEquals("#CCCCCC", gameService.colorPlayer("unknownCharacter"));
//    }
//
//    @Test
//    public void testPropertiesPlayerNullPlayer() {
//        GameException exception = assertThrows(GameException.class, () -> {
//            gameService.propertiesPlayer(null, 10, 5, false, "bomber1", false, 1, 100, 10, 5, 20, false);
//        });
//
//        assertEquals(GameException.PLAYER_NOT_FOUND, exception.getMessage());
//    }
//
//
//}
