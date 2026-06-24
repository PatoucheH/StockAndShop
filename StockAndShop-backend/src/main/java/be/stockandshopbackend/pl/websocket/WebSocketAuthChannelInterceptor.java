package be.stockandshopbackend.pl.websocket;

import be.stockandshopbackend.bll.services.security.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Authenticates WebSocket sessions by reading a JWT from the STOMP CONNECT frame.
 *
 * <p>Browsers cannot set custom HTTP headers during the WebSocket upgrade handshake,
 * so the token cannot be validated at the HTTP level as it is for REST requests.
 * The client therefore includes it in the first STOMP frame's headers instead.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                String token = authHeader.substring(7);
                // An expired/invalid token must not throw here: an uncaught exception crashes the
                // broker's clientInboundChannel and forces Spring to close the whole WS session.
                try {
                    String username = jwtService.extractUsername(token);
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    if(jwtService.validateToken(token, user)){
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        accessor.setUser(auth);
                    }
                } catch (JwtException | UsernameNotFoundException e) {
                    log.debug("WebSocket CONNECT with invalid/expired token: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}
