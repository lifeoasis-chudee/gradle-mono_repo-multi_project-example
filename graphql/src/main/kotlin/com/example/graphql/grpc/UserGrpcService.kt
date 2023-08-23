package com.example.graphql.grpc

import com.example.proto.user.*
import io.grpc.StatusRuntimeException
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

@Service
class UserGrpcService {

    @GrpcClient("user-grpc")
    private val userStub: UserServiceGrpc.UserServiceBlockingStub? = null

    suspend fun listUsers(): List<User> {
        if (userStub == null) {
            throw Exception("userStub not load")
        }
        return userStub.listUsers(ListUsersRequest.newBuilder().build()).usersList
    }

    suspend fun getUserByName(name: String): User {
        if (userStub == null) {
            throw Exception("userStub not load")
        }

        return try {
            val req = GetUserRequest.newBuilder()
                .setName(name)
                .build()

            userStub.getUser(req)
        } catch (e: StatusRuntimeException) {
            println(e.message)
            throw Exception("user not found")
        }
    }

    suspend fun createUser(name: String, age: Int): User {
        if (userStub == null) {
            throw Exception("userStub not load")
        }

        val user = User.newBuilder()
            .setName(name)
            .setAge(age)
            .build()
        println(user)

        val req = CreateUserRequest.newBuilder()
            .setUser(user)
            .build()

        return userStub.createUser(req)
    }

    suspend fun deleteUser(name: String) {
        val req = DeleteUserRequest.newBuilder()
            .setName(name)
            .build()

        userStub?.deleteUser(req)
    }

    suspend fun updateUser(name: String, age: Int): User {
        if (userStub == null) {
            throw Exception("userStub not load")
        }

        val user = User.newBuilder()
            .setName(name)
            .setAge(age)
            .build()

        val req = UpdateUserRequest.newBuilder()
            .setUser(user)
            .build()

        return userStub.updateUser(req)
    }
}
