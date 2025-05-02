package edu.eci.arsw.ecibombit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("jwtValidator")
public class JwtValidator {

    // Valida que el app_displayname sea "Ecibombit"
    public boolean hasValidAppDisplayName(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }

        String appDisplayName = jwt.getClaimAsString("app_displayname");
        return "Ecibombit".equals(appDisplayName);
    }
}