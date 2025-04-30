package edu.eci.arsw.ecibombit.controller;

import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}