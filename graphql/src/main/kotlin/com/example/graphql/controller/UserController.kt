package com.example.graphql.controller

import com.example.graphql.service.UserService
import com.example.proto.user.User
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserController(private var userService: UserService) {

    @QueryMapping
    suspend fun listUsers(): List<User> {
        return userService.listUsers()
    }

    @QueryMapping
    suspend fun getUserByName(@Argument name: String): User {
        println("query getUserByName $name")
        return userService.getUserByName(name)
    }

    @MutationMapping
    suspend fun createUser(@Argument name: String, @Argument age: Int): User {
        println("mutation createUser $name $age")
        return userService.createUser(name, age)
    }

    @MutationMapping
    suspend fun updateUser(@Argument name: String, @Argument age: Int): User {
        println("mutation updateUser $name $age")
        return userService.updateUser(name, age)
    }

    @MutationMapping
    suspend fun deleteUser(@Argument name: String): Boolean {
        println("mutation deleteUser $name")
        return userService.deleteUser(name)
    }
}
