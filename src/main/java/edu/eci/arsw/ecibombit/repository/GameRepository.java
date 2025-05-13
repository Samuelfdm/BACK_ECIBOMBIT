package edu.eci.arsw.ecibombit.repository;

import edu.eci.arsw.ecibombit.model.Game;
import edu.eci.arsw.ecibombit.model.Player;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {

    Optional<Game> findByRoomId(String roomId);
}