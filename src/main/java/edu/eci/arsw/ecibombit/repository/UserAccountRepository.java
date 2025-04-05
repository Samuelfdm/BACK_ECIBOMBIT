package edu.eci.arsw.ecibombit.repository;

import edu.eci.arsw.ecibombit.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    UserAccount findByUsername(String username);
    UserAccount findByOid(String oid);
}
