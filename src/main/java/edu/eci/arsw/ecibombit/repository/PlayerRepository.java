package edu.eci.arsw.ecibombit.repository;

import edu.eci.arsw.ecibombit.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PlayerRepository extends MongoRepository<Player, String> {
    Optional<Player> findByOid(String oid);
}