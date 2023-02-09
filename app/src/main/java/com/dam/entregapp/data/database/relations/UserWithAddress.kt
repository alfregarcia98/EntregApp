package com.dam.entregapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.User

data class UserWithAddress(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val addresses: List<Address>
)