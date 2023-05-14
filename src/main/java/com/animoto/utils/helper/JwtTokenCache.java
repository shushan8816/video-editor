package com.animoto.utils.helper;

import com.animoto.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtTokenCache {

    private final Map<String, String> jwtTokenBlacklist;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isTokenBlacklisted(String token) {
        return jwtTokenBlacklist.containsKey(token);
    }

    private long getExpiryForToken(Date date) {
        long secondAtExpiry = date.toInstant().getEpochSecond();
        long secondAtLogout = Instant.now().getEpochSecond();
        return Math.max(0, secondAtExpiry - secondAtLogout);
    }

    public void markLogoutToken(String token) {
        String email = jwtTokenProvider.extractClaim(token, Claims::getSubject);

        if (isTokenBlacklisted(token)) {
            log.info(String.format("Log out token for user [%s] is already present in the cache", email));
        } else {
            Date tokenExpiryDate = jwtTokenProvider.extractClaim(token, Claims::getExpiration);
            long expiryForToken = this.getExpiryForToken(tokenExpiryDate);
            log.info(String.format("Logout token cache set for [%s] with a expiry of [%s] seconds. Token is due expiry at [%s]", email, expiryForToken, tokenExpiryDate));
            jwtTokenBlacklist.put(email, token);
            log.info("Logout successful");
        }
    }
}
