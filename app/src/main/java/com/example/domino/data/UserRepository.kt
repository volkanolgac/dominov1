package com.example.domino.data

import com.example.domino.model.Profile
import com.example.domino.model.Settings
import com.example.domino.model.Stats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val userDao: UserDao) {
    val userProfile: Flow<UserEntity?> = userDao.getUser()

    suspend fun saveProfile(name: String, themeId: String, languageId: String) {
        val current = userDao.getUserSync() ?: UserEntity()
        userDao.insertUser(current.copy(name = name, themeId = themeId, languageId = languageId))
    }

    suspend fun updateLanguage(languageId: String) {
        val current = userDao.getUserSync() ?: UserEntity()
        userDao.insertUser(current.copy(languageId = languageId))
    }

    suspend fun updateStats(won: Boolean, finalScore: Int) {
        val current = userDao.getUserSync() ?: UserEntity()
        userDao.insertUser(
            current.copy(
                matches = current.matches + 1,
                wins = current.wins + (if (won) 1 else 0),
                losses = current.losses + (if (won) 0 else 1),
                bestScore = maxOf(current.bestScore, finalScore)
            )
        )
    }
    
    suspend fun resetStats() {
        val current = userDao.getUserSync() ?: UserEntity()
        userDao.insertUser(
            current.copy(
                matches = 0,
                wins = 0,
                losses = 0,
                bestScore = 0
            )
        )
    }
}
