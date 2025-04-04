package edu.eci.arsw.ecibombit.repository;

import edu.eci.arsw.ecibombit.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByOid(String oid);
}
