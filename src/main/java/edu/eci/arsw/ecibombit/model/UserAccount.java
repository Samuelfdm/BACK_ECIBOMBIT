package edu.eci.arsw.ecibombit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "users")
public class UserAccount {
    @Id
    private String id; // Mongo ID
    private String oid; // Unique ID de EntraID (Microsoft Object ID)
    private String username; // Display Name o email
    private String email;
    private Instant createdAt;
}