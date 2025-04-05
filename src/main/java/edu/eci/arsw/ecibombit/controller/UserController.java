package edu.eci.arsw.ecibombit.controller;

import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final LoginService loginService;

    public UserController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserAccount> loginOrRegister(@RequestBody UserDTO userDTO) {
        UserAccount user = loginService.loginOrRegister(userDTO);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{oid}")
    public ResponseEntity<UserAccount> getUserByOid(@PathVariable String oid) {
        List<UserAccount> users = loginService.getUserByOid(oid);
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users.get(0)); // Devolver el primer usuario si existe alguno
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
