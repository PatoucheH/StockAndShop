package be.stockandshopbackend.bll.services.security.services;

import be.stockandshopbackend.exceptions.PremiumRequiredException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Guards premium-only features. Register as "premiumSecurity" so controllers can inject it
 * directly rather than relying solely on {@code @PreAuthorize}.
 * <p>
 * {@link #require} throws instead of returning a boolean for a SpEL expression so the client
 * receives an explicit, actionable message instead of a generic 403 indistinguishable from a
 * home-membership refusal.
 * <p>
 * Admins bypass the premium requirement, consistent with {@link HomeSecurityService}.
 */
@Service("premiumSecurity")
public class PremiumSecurityService {

    private static final String PREMIUM_AUTHORITY = "PREMIUM";
    private static final String ADMIN_AUTHORITY = "ADMIN";
    private static final String DEFAULT_MESSAGE = "Cette fonctionnalité est réservée aux membres premium.";

    public void require(Authentication authentication) {
        require(authentication, DEFAULT_MESSAGE);
    }

    public void require(Authentication authentication, String message) {
        boolean allowed = authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(a -> PREMIUM_AUTHORITY.equals(a.getAuthority())
                                || ADMIN_AUTHORITY.equals(a.getAuthority()));
        if (!allowed) {
            throw new PremiumRequiredException(message);
        }
    }
}
