package com.demo.entity

import com.demo.model.UserModel
import com.demo.util.Role
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.FetchType
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,
    val username: String,
    val email: String,
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,
    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
    val courses: MutableList<Course> = mutableListOf(),
    val password: String,
)

fun User.toUserModel(): UserModel {
    return UserModel(
        id = id,
        username = username,
        email = email,
        role = role,
        password = password,
    )
}
