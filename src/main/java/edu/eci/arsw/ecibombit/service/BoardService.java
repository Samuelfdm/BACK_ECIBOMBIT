package edu.eci.arsw.ecibombit.service;

import edu.eci.arsw.ecibombit.model.Board;
import edu.eci.arsw.ecibombit.model.Cell;
import edu.eci.arsw.ecibombit.model.enums.CellType;
import edu.eci.arsw.ecibombit.model.GameConfig;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BoardService {

    private static final List<String[]> MAPS = List.of(
            new String[]{
                    "################",
                    "#      +       #",
                    "# ###### ####  #",
                    "# #    +    #  #",
                    "# # ###### #  ##",
                    "# # #    # #  ##",
                    "# # # ## # #  ##",
                    "# + # ## # #  ##",
                    "# # #    # #  ##",
                    "# # ###### #  ##",
                    "# #    +    #  #",
                    "# ###### ####  #",
                    "#      +       #",
                    "################"
            },
            new String[]{
                    "################",
                    "# # +    + #   #",
                    "# # ###### # # #",
                    "# #        # # #",
                    "# # ###### # # #",
                    "# + #    # + # #",
                    "# # # ## # # # #",
                    "# # # ## # # # #",
                    "# # #    # # # #",
                    "# # ###### # # #",
                    "# #        # # #",
                    "# # ###### # # #",
                    "# # +    + #   #",
                    "################"
            },
            new String[]{
                    "################",
                    "#     ++     ###",
                    "# ## ##### ##  #",
                    "# #   +++   #  #",
                    "# # ##### # # ##",
                    "#   #   # #   ##",
                    "# # # # # # # ##",
                    "# + # # # # + ##",
                    "# #   #   # # ##",
                    "# # ##### # # ##",
                    "# #   +++   #  #",
                    "# ## ##### ##  #",
                    "#     ++     ###",
                    "################"
            }
    );

    private static final Random random = new Random();

    public Board generateBoard(GameConfig config) {
        int select = switch (config.getMap()) {
            case "map1" -> 0;
            case "map2" -> 1;
            case "map3" -> 2;
            default -> 0;
        };

        String[] rawMap = MAPS.get(select);
        List<Cell> cells = new ArrayList<>();

        for (int y = 0; y < rawMap.length; y++) {
            for (int x = 0; x < rawMap[y].length(); x++) {
                char ch = rawMap[y].charAt(x);
                CellType type = switch (ch) {
                    case '#' -> CellType.WALL;
                    case '+' -> CellType.BLOCK;
                    default -> CellType.EMPTY;
                };
                cells.add(new Cell(x, y, type));
            }
        }

        Board board = new Board(16, 16, cells);
        placePlayersInCorners(board);
        placeRandomItems(board, config.getItems());

        return board;
    }

    private void placePlayersInCorners(Board board) {
        List<int[]> corners = List.of(
                new int[]{1, 1},
                new int[]{1, board.getColumns() - 2},
                new int[]{board.getRows() - 2, 1},
                new int[]{board.getRows() - 2, board.getColumns() - 2}
        );

        for (int[] corner : corners) {
            getCellAt(board, corner[0], corner[1]).setType(CellType.PLAYER);
        }
    }

    private void placeRandomItems(Board board, int count) {
        List<Cell> emptyCells = board.getCells().stream()
                .filter(c -> c.getType() == CellType.EMPTY)
                .toList();

        Collections.shuffle(emptyCells);
        for (int i = 0; i < Math.min(count, emptyCells.size()); i++) {
            emptyCells.get(i).setType(CellType.ITEM);
        }
    }

    private Cell getCellAt(Board board, int x, int y) {
        return board.getCells().stream()
                .filter(c -> c.getX() == x && c.getY() == y)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid coordinates"));
    }
}