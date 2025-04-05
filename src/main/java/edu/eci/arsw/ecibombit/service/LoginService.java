package edu.eci.arsw.ecibombit.service;

import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class LoginService {
    private final UserAccountRepository repository;

    public LoginService(UserAccountRepository repository) {
        this.repository = repository;
    }

    public UserAccount loginOrRegister(UserDTO dto) {
        List<UserAccount> existingUsers = repository.findByOid(dto.getOid());
        System.out.println("OID recibido: " + dto.getOid());
        System.out.println("Usuarios encontrados con este OID: " + existingUsers.size());

        if (existingUsers.isEmpty()) {
            System.out.println("Creando nuevo usuario.");
            return create(dto);
        } else {
            System.out.println("Usuario existente encontrado, actualizando.");
            return update(existingUsers.getFirst(), dto);
        }
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

    public List<UserAccount> getUserByOid(String oid) {
        return repository.findByOid(oid);
    }

}
