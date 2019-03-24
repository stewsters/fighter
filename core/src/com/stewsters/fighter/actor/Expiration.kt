package com.stewsters.fighter.actor

import com.stewsters.fighter.FighterGame

class Expiration(ttlMS: Long) {

    val timeOfDeath: Long = System.currentTimeMillis() + ttlMS

    fun check(fighterGame: FighterGame, actor: Actor) {
        if (System.currentTimeMillis() > timeOfDeath) {
            fighterGame.removeActors.add(actor)
        }
    }

}