package edu.eci.arsw.ecibombit.Exception;

public class GameException extends Exception {

    // Error messages
    public static final String ROOMID_INVALID = "The room ID is invalid.";
    public static final String PLAYERS_INVALID = "The players are invalid.";
    public static final String CONFIG_INVALID = "The config is invalid.";
    public static final String GAMEID_INVALID = "The game id is invalid.";
    public static final String NULL_PROPERTY_PLAYER = "One or more player properties are null.";
    public static final String NULL_PROPERTY_GAME = "One or more game properties are null.";
    public static final String GAME_NOT_CREATED = "The game could not be created.";
    public static final String GAME_NOT_FOUND = "The game could not be found.";
    public static final String PLAYER_NOT_FOUND = "The player could not be found.";
    public static final String GAME_FINALIZATION_FAILED = "The game could not be finalized.";
    public static final String DUPLICATE_USERNAME = "Duplicate username";
    public static final String ROOM_ALREADY_EXISTS = "Room already exists";
    public static final String PLAYER_COUNT_MISMATCH = "Player count mismatch";


    /**
     * Constructs a new GameException with the specified message.
     *
     * @param message The detail message.
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * Constructs a new GameException with the specified message.
     *
     * @param message The detail message.
     */
    public GameException(String message, String user) {
        super(message + user);
    }
}
