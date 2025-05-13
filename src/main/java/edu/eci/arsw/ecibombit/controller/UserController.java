package edu.eci.arsw.ecibombit.controller;

import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.service.JwtService;
import edu.eci.arsw.ecibombit.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final LoginService loginService;

    @Autowired
    JwtService jwtService;

    public UserController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginOrRegister(@RequestBody UserDTO userDTO) {
        UserAccount user = loginService.loginOrRegister(userDTO);
        String token = jwtService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{oid}")
    public ResponseEntity<UserAccount> getUserByOid(@PathVariable String oid) {
        UserAccount user = loginService.getUserByOid(oid);
        if (user != null) {
            return ResponseEntity.ok(user); // Devolver el primer usuario si existe alguno
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
