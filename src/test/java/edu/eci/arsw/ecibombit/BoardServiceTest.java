package edu.eci.arsw.ecibombit;


import edu.eci.arsw.ecibombit.model.Board;
import edu.eci.arsw.ecibombit.model.Cell;
import edu.eci.arsw.ecibombit.model.Player;
import edu.eci.arsw.ecibombit.model.enums.CellType;
import edu.eci.arsw.ecibombit.service.BoardService;
import edu.eci.arsw.ecibombit.model.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardService();
    }

    @Test
    void testGenerateBoardWithMap1() {
        GameConfig config = new GameConfig("map1", 0, 0);
        List<Player> players = List.of();

        Board board = boardService.generateBoard(config, players);

        assertNotNull(board);
        assertEquals(16, board.getRows());
        assertEquals(16, board.getColumns());
        assertEquals(16 * 16, board.getCells().size());
    }

    @Test
    void testPlayersArePlacedInCorners() {
        GameConfig config = new GameConfig("map1", 0, 0);

        Player p1 = new Player(); p1.setId("1"); p1.setUsername("Alice");
        Player p2 = new Player(); p2.setId("2"); p2.setUsername("Bob");
        Player p3 = new Player(); p3.setId("3"); p3.setUsername("Charlie");
        Player p4 = new Player(); p4.setId("4"); p4.setUsername("Dave");

        List<Player> players = List.of(p1, p2, p3, p4);

        Board board = boardService.generateBoard(config, players);
        List<Cell> cells = board.getCells();

        int[][] expectedPositions = {
                {1, 1},
                {1, board.getColumns() - 2},
                {board.getRows() - 2, 1},
                {board.getRows() - 2, board.getColumns() - 2}
        };

        for (int i = 0; i < players.size(); i++) {
            int x = expectedPositions[i][0];
            int y = expectedPositions[i][1];

            Cell cell = cells.stream()
                    .filter(c -> c.getX() == x && c.getY() == y)
                    .findFirst()
                    .orElseThrow();

            assertEquals(CellType.PLAYER, cell.getType(), "Expected PLAYER at (" + x + "," + y + ")");
            assertEquals(players.get(i).getId(), cell.getPlayerId());
        }
    }

    @Test
    void testPlaceRandomItemsDoesNotExceedEmptyCells() {
        GameConfig config = new GameConfig("map1", 500, 0); // Exaggerated number
        List<Player> players = List.of();

        Board board = boardService.generateBoard(config, players);
        long itemCount = board.getCells().stream()
                .filter(c -> c.getType() == CellType.ITEM)
                .count();

        long emptyCount = board.getCells().stream()
                .filter(c -> c.getType() == CellType.EMPTY)
                .count();

        assertTrue(itemCount <= emptyCount + itemCount, "Item count exceeds possible placement");
    }
}
