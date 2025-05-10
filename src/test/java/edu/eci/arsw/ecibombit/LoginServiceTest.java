package edu.eci.arsw.ecibombit;

import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.repository.UserAccountRepository;
import edu.eci.arsw.ecibombit.service.LoginService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private UserAccountRepository repository;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginOrRegister_createsNewUserWhenNotFound() {
        // Arrange
        UserDTO dto = new UserDTO("123", "testuser", "test@example.com");
        when(repository.findByOid("123")).thenReturn(null);

        ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
        when(repository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserAccount createdUser = loginService.loginOrRegister(dto);

        // Assert
        assertNotNull(createdUser);
        assertEquals("123", createdUser.getOid());
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
        assertNotNull(createdUser.getCreatedAt());

        verify(repository).save(any(UserAccount.class));
    }

    @Test
    void testLoginOrRegister_updatesExistingUser() {
        // Arrange
        UserAccount existingUser = new UserAccount();
        existingUser.setOid("456");
        existingUser.setUsername("olduser");
        existingUser.setEmail("old@example.com");

        UserDTO dto = new UserDTO("456", "newuser", "new@example.com");

        when(repository.findByOid("456")).thenReturn(existingUser);
        when(repository.save(existingUser)).thenReturn(existingUser);

        // Act
        UserAccount updatedUser = loginService.loginOrRegister(dto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("456", updatedUser.getOid());
        assertEquals("newuser", updatedUser.getUsername());
        assertEquals("new@example.com", updatedUser.getEmail());

        verify(repository).save(existingUser);
    }

    @Test
    void testGetUserByOid_returnsUser() {
        // Arrange
        UserAccount user = new UserAccount();
        user.setOid("789");
        when(repository.findByOid("789")).thenReturn(user);

        // Act
        UserAccount result = loginService.getUserByOid("789");

        // Assert
        assertNotNull(result);
        assertEquals("789", result.getOid());
        verify(repository).findByOid("789");
    }
}
