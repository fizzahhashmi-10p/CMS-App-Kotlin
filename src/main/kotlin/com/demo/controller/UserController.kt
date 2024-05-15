package com.demo.controller

import com.demo.dto.UserDTO
import com.demo.model.UserModel
import com.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
public class UserController(private val userService: UserService) {
    @PostMapping
    fun createUser(
        @RequestBody user: UserDTO,
    ): ResponseEntity<Any> {
        return ResponseEntity(userService.save(user), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllUsers(): List<UserModel> {
        return userService.fetchAll()
    }
}
