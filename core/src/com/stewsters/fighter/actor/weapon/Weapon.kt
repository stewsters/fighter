package com.stewsters.fighter.actor.weapon

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor


interface Weapon {
    fun fire(fighterGame: FighterGame, shooter: Actor)
    fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, rotation: Quaternion, velocity: Float)

   fun getVelocity():Float
}
