package com.stewsters.fighter.actor.pilot

import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.math.angleBetween

interface Pilot {
    fun fly(fighterGame: FighterGame, actor: Actor)

    fun getPitch(): Float
    fun getYaw(): Float
    fun getRoll(): Float
    fun getAccel(): Float
}


infix fun Actor.beingAimedAtBy(target: Actor): Boolean {
    val angle = this angleTo target
    return angle < 0.174533f // 10 degrees
}


infix fun Actor.angleTo(target: Actor): Float {
    val difference = position.cpy().sub(target.position)
    val aim = Actor.forward.cpy().mul(target.rotation)
    return angleBetween(difference, aim)
}

