package com.example.user.service

import com.example.proto.user.*
import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import javax.naming.NameNotFoundException

@GrpcService
class Service: UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    private val listUser = mutableMapOf<String, User>()

    override suspend fun listUsers(request: ListUsersRequest): ListUsersResponse {
        val res = ListUsersResponse.newBuilder()

        listUser.toList().forEach { user ->
            res.addUsers(user.second)
        }

        return res.build()
    }

    override suspend fun createUser(request: CreateUserRequest): User {
        val ok = listUser.containsKey(request.user.name)
        if (ok) {
            return listUser[request.user.name]!!
        }

        val user = toUser(request.user.name, request.user.age)

        listUser[request.user.name] = user

        return user
    }

    override suspend fun getUser(request: GetUserRequest): User {
        val ok = listUser.containsKey(request.name)
        if (!ok) {
            throw Exception("not found user")
        }

        return listUser.getValue(request.name)
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Empty {
        listUser.remove(request.name)

        return Empty.getDefaultInstance()
    }

    override suspend fun updateUser(request: UpdateUserRequest): User {
        listUser[request.user.name] = request.user

        return request.user
    }

    private fun toUser(name: String, age: Int): User {
        return User.newBuilder()
            .setName(name)
            .setAge(age)
            .build()
    }
}
