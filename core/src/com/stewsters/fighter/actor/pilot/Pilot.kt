package com.stewsters.fighter.actor.pilot

import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.math.angleBetween
import com.stewsters.fighter.types.forward

interface Pilot {
    fun fly(fighterGame: FighterGame, actor: Actor):PilotControl
}


infix fun Actor.beingAimedAtBy(target: Actor): Boolean {
    val angle = this angleTo target
    return angle < 0.174533f // 10 degrees
}

infix fun Actor.angleTo(target: Actor): Float {
    val difference = position.cpy().sub(target.position)
    val aim = forward.cpy().mul(target.rotation)
    return angleBetween(difference, aim)
}
