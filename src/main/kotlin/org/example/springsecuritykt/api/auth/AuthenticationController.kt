package org.example.springsecuritykt.api.auth

import org.example.springsecuritykt.security.JwtService
import org.example.springsecuritykt.security.Role
import org.example.springsecuritykt.security.User
import org.example.springsecuritykt.security.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    val service: AuthenticationService
) {

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<RegisterResponse> {
        return ResponseEntity.ok(service.register(request))
    }

    @PostMapping("/authenticate")
    fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(service.authenticate(request))
    }
}

class RegisterRequest(
    val username: String,
    val password: String
) {}

class RegisterResponse(
    val token: String
) {}

class AuthenticationRequest(
    val username: String,
    val password: String
) {}

class AuthenticationResponse(
    val token: String
) {}

@Service
class AuthenticationService(
    val repository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtService: JwtService,
    val authenticationManager: AuthenticationManager
) {
    fun register(request: RegisterRequest): RegisterResponse {
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            role = Role.USER
        )
        repository.save(user)
        val jwt = jwtService.generateToken(user)
        return RegisterResponse(jwt)
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val user = repository.findByUsername(request.username) ?: throw UsernameNotFoundException("User ${request.username} not found")
        val jwt = jwtService.generateToken(user)
        return AuthenticationResponse(jwt)
    }
}