package com.example.domino.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val themeId: String = "classic_green",
    val languageId: String = "tr",
    val matches: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val bestScore: Int = 0
)
