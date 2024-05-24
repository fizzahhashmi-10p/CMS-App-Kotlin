package com.demo

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Date

fun generateTestToken(email: String): String {
    return Jwts.builder()
        .claims()
        .subject(email)
        .issuedAt(Date(System.currentTimeMillis()))
        .expiration(Date(System.currentTimeMillis() + 36000))
        .and()
        .signWith(
            Keys.hmacShaKeyFor(
                "68jxj2tzH7v82cEsH-YextKPPzN5oBfZ6_Ep8ghQR5k".toByteArray(),
            ),
        )
        .compact()
}
