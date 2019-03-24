package com.stewsters.fighter.actor

enum class Faction {
    UNALIGNED,
    ALDRONI,
    DOMINION;

    fun isEnemy(faction: Faction): Boolean {
        if (faction == UNALIGNED || this == UNALIGNED) {
            return false
        }
        return this != faction
    }
}