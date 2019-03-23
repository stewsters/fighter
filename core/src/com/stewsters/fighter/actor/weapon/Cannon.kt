package com.stewsters.fighter.actor.weapon

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.FighterGame
import com.stewsters.fighter.actor.Actor
import com.stewsters.fighter.actor.DamageAndDisappearCollider
import com.stewsters.fighter.actor.Expiration
import com.stewsters.fighter.types.BulletType

// Fires projectiles
class Cannon(val bulletType: BulletType) : Launcher(bulletType.refireMS, bulletType.launchVelocity) {

    override fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, rotation: Quaternion, velocity: Float) {

        // I think we will need a sound player class, that detects the distance to each and scales the audio
        fighterGame.audio.laser()
        fighterGame.newActors.add(Actor(
                position,
                rotation,
                velocity,
                bulletType.model,
                expiration = Expiration(bulletType.expiration),
                radius = bulletType.radius,
                collider = DamageAndDisappearCollider(bulletType.damage)
        ))
    }
}