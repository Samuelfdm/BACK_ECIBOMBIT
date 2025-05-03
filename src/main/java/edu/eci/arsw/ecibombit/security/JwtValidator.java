package edu.eci.arsw.ecibombit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import java.util.logging.Logger;

@Component("jwtValidator")
public class JwtValidator {
    
    private static final Logger logger = Logger.getLogger(JwtValidator.class.getName());

    /**
     * Validates that the app_displayname is "Ecibombit"
     */
    public boolean hasValidAppDisplayName(Authentication authentication) {
        try {
            if (authentication == null) {
                authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null) {
                    logger.warning("Authentication object is null");
                    return false;
                }
            }

            if (!(authentication.getPrincipal() instanceof Jwt)) {
                logger.warning("Principal is not a JWT: " + authentication.getPrincipal().getClass());
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // Debug information
            String appDisplayName = jwt.getClaimAsString("app_displayname");
            logger.info("Token app_displayname: " + appDisplayName);
            
            if (!"Ecibombit".equals(appDisplayName)) {
                logger.warning("Invalid app_displayname: " + appDisplayName);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.severe("Error validating JWT: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Utility method to print all claims from a JWT for debugging
     */
    public void printAllClaims(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        logger.info("=== JWT Claims ===");
        claims.forEach((key, value) -> logger.info(key + ": " + value));
        logger.info("==================");
    }
}