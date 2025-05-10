package edu.eci.arsw.ecibombit.Exception;

public class GameException extends Exception {

    // Error messages
    public static final String ROOMID_INVALID = "The room ID is invalid.";
    public static final String GAME_NOT_CREATED = "The game could not be created.";
    public static final String GAME_NOT_FOUND = "The game could not be found.";
    public static final String PLAYER_NOT_FOUND = "The player could not be found.";
    public static final String GAME_FINALIZATION_FAILED = "The game could not be finalized.";

    /**
     * Constructs a new GameException with the specified message.
     *
     * @param message The detail message.
     */
    public GameException(String message) {
        super(message);
    }
}
