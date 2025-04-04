package edu.eci.arsw.ecibombit.service;

import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Service
public class LoginService {
    private final UserAccountRepository repository;

    public LoginService(UserAccountRepository repository) {
        this.repository = repository;
    }

    public UserAccount loginOrRegister(UserDTO dto) {
        return repository.findByOid(dto.getOid())
                .map(existing -> update(existing, dto))
                .orElseGet(() -> create(dto));
    }

    private UserAccount create(UserDTO dto) {
        UserAccount user = new UserAccount();
        user.setOid(dto.getOid());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setCreatedAt(Instant.now());
        return repository.save(user);
    }

    private UserAccount update(UserAccount user, UserDTO dto) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return repository.save(user);
    }

    public Optional<UserAccount> getUserByOid(String oid) {
        return repository.findByOid(oid);
    }

}
