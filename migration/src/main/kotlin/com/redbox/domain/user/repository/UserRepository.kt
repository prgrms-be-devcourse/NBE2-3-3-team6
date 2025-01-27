package com.redbox.domain.user.repository

import com.redbox.domain.user.entity.User
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Id> {

    fun existsByEmail(email: String): Boolean

    fun findByEmailAndName(email: String, name: String): User?

    fun findByNameAndPhoneNumber(name: String, phoneNumber: String): User?
}