package org.example.springsecuritykt.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

    fun extractUsername(token: String): String? {
        return extractClaim(token, Claims::getSubject)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(emptyMap(), userDetails)
    }
}

fun generateToken(
    extraClaims: Map<String, Any>,
    userDetails: UserDetails
): String {
    val currentTimeMillis = System.currentTimeMillis()
    return Jwts
        .builder()
        .claims(extraClaims)
        .subject(userDetails.username)
        .issuedAt(Date(currentTimeMillis))
        .expiration(Date(currentTimeMillis + 1000 * 60 * 24))
        .signWith(getSignInKey())
        .compact()
}

fun isTokenExpired(token: String): Boolean {
    val expirationDate = extractClaim(token, Claims::getExpiration)
    return expirationDate.before(Date())
}

const val JWT_PUBLIC_KEY = "061235992c4f4cf9a8a44fd57e53f098002f7bf3ab4a19efb92c10362a5d27f5"

private fun getSignInKey(): SecretKey {
    val keyInBytes: ByteArray = Decoders.BASE64.decode(JWT_PUBLIC_KEY)
    return Keys.hmacShaKeyFor(keyInBytes)
}

private fun extractAllClaims(token: String): Claims {
    return Jwts
        .parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .payload
}

private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
    val claims = extractAllClaims(token)
    return claimsResolver(claims)
}