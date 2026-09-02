package com.example.domino.model

typealias Pip = Int

data class Tile(
    val id: String,
    val a: Pip,
    val b: Pip
) {
    val isDouble: Boolean get() = a == b
    val weight: Int get() = a + b
}

enum class Side {
    LEFT,
    RIGHT
}

data class PlacedTile(
    val tile: Tile,
    val orientedA: Pip,
    val orientedB: Pip,
    val side: Side,
    val isDouble: Boolean
)

enum class PlayerId {
    HUMAN,
    AI
}

enum class Phase {
    PLAYING,
    ROUND_OVER,
    MATCH_OVER
}

enum class RoundReason {
    OUT,
    BLOCKED
}

data class RoundResult(
    val winner: PlayerId?,
    val points: Int,
    val reason: RoundReason
)

data class Settings(
    val sound: Boolean = true,
    val animations: Boolean = true,
    val haptics: Boolean = true,
    val themeId: String = "classic_green",
    val languageId: String = "tr"
)

enum class Language(val id: String, val displayName: String) {
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français"),
    JAPANESE("ja", "日本語"),
    TURKISH("tr", "Türkçe"),
    SPANISH("es", "Español"),
    CHINESE("zh", "中文"),
    HINDI("hi", "हिन्दी"),
    ITALIAN("it", "Italiano"),
    ARABIC("ar", "العربية"),
    RUSSIAN("ru", "Русский")
}

enum class AppTheme(val id: String, val displayName: String) {
    CLASSIC_GREEN("classic_green", "Classic Green"),
    NAVY_NIGHT("navy_night", "Navy Night"),
    ROYAL_BURGUNDY("royal_burgundy", "Royal Burgundy"),
    OBSIDIAN_BLACK("obsidian_black", "Obsidian Black"),
    PINK_BLOSSOM("pink_blossom", "Pink Blossom"),
    CLASSIC_WOOD("classic_wood", "Classic Wood")
}

data class Profile(
    val name: String = "",
    val avatarId: String = "avatar_1"
)

data class Stats(
    val matches: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val bestScore: Int = 0
) {
    val winRate: Int
        get() = if (matches > 0) ((wins.toDouble() / matches) * 100).toInt() else 0
}

enum class AppScreen {
    MAIN_MENU,
    GAME,
    HOW_TO_PLAY,
    SETTINGS
}
