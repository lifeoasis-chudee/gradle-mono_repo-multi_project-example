package com.example.graphql.service

import com.example.graphql.grpc.UserGrpcService
import com.example.proto.user.User
import org.springframework.stereotype.Service

@Service
class UserService(private val userGrpcService: UserGrpcService) {

    suspend fun listUsers(): List<User> {
        return userGrpcService.listUsers()
    }

    suspend fun getUserByName(name: String): User {
        return userGrpcService.getUserByName(name)
    }

    suspend fun createUser(name: String, age: Int): User {
        return userGrpcService.createUser(name, age)
    }

    suspend fun updateUser(name: String, age: Int): User {
        return userGrpcService.updateUser(name, age)
    }

    suspend fun deleteUser(name: String): Boolean {
        userGrpcService.deleteUser(name)
        return true
    }
}
