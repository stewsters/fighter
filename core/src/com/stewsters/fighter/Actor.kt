package com.stewsters.fighter

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

open class Actor(
        val pos: Vector3,
        val facing: Quaternion,
        var velocity: Float = 0f,
        model: Model,
        val pilot: Pilot? = null,
        val life: Life? = null,
        val aircraftType: AircraftType? = null,
        val primaryWeapon: Weapon? = null,
        val secondaryWeapon: Weapon? = null,
        val faction: Faction = Faction.UNALIGNED,
        val expiration: Expiration? = null,
        val radius: Float = 1f,
        val collider: Collider?,
        val respawnable: Boolean = false
) {

    companion object {
        val up = Vector3(0f, 0f, 1f)
        val forward = Vector3(0f, 1f, 0f)
        val right = Vector3(1f, 0f, 0f)
        val left = Vector3(-1f, 0f, 0f)
    }

    val instance = ModelInstance(model)


    init {
        instance.transform.setToTranslation(pos).rotate(facing)
    }
}

interface Collider {
    fun collision(fighterGame: FighterGame, actor: Actor)
}

class DamageCollider(val damage: Float) : Collider {
    override fun collision(fighterGame: FighterGame, actor: Actor) {
        actor.life?.takeDamage(fighterGame, actor, damage)
    }
}

class DamageAndDisappearCollider(val damage: Float) : Collider {
    override fun collision(fighterGame: FighterGame, actor: Actor) {
        actor.life?.takeDamage(fighterGame, actor, damage)
    }
}


class Life(val max: Float, var cur: Float = max) {

    fun takeDamage(fighterGame: FighterGame, actor: Actor, float: Float) {
        Gdx.app.log("Aircraft", "Took $float damage")
        cur -= float

        if (cur <= 0) {
            fighterGame.audio.explode()
            fighterGame.removeActors.add(actor)
            if(actor.respawnable){
                fighterGame.respawnActors.add(actor)
            }
            Gdx.app.log("Destruction", "Target Exploded")
        }
    }

}

class Expiration(ttlMS: Long) {

    val timeOfDeath: Long = System.currentTimeMillis() + ttlMS

    fun check(fighterGame: FighterGame, actor: Actor) {
        if (System.currentTimeMillis() > timeOfDeath) {
            fighterGame.removeActors.add(actor)
        }
    }

}


interface Weapon {
    fun fire(fighterGame: FighterGame, shooter: Actor)
    fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, facing: Quaternion, velocity: Float)
}

abstract class Launcher(val refireMS: Long, val launchVelocity: Float) : Weapon {
    var lastFired: Long = 0
    var rightNext = true

    override fun fire(fighterGame: FighterGame, shooter: Actor) {
        val millis = System.currentTimeMillis()
        if (lastFired + refireMS < millis) {

            val offset: Vector3
            if (rightNext) {
                offset = Actor.right.cpy().add(Actor.forward).mul(shooter.facing)
            } else {
                offset = Actor.left.cpy().add(Actor.forward).mul(shooter.facing)
            }

            rightNext = !rightNext

            project(
                    fighterGame,
                    shooter,
                    shooter.pos.cpy().add(offset),
                    shooter.facing.cpy(),
                    shooter.velocity + launchVelocity
            )

            lastFired = millis
        }
    }

}

// Fires projectiles
class Cannon(val bulletType: BulletType) : Launcher(bulletType.refireMS, bulletType.launchVelocity) {

    override fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, facing: Quaternion, velocity: Float) {

        // I think we will need a sound player class, that detects the distance to each and scales the audio
        fighterGame.audio.laser()
        fighterGame.newActors.add(Actor(
                position,
                facing,
                velocity,
                bulletType.model!!,
                expiration = Expiration(bulletType.expiration),
                radius = bulletType.radius,
                collider = DamageAndDisappearCollider(2f)
        ))
    }
}

// Fires homing ordinance
class MissileRack(val missileType: MissileType) : Launcher(
        missileType.refireMS,
        missileType.launchVelocity
) {
    override fun project(fighterGame: FighterGame, shooter: Actor, position: Vector3, facing: Quaternion, velocity: Float) {

        // find a target
        val target: Actor? = fighterGame.actors.asSequence()
//                .filter { it.faction.isEnemy(shooter.faction) }
                .filter { it.aircraftType != null && it != shooter }
                .minBy { shooter.pos.dst2(it.pos) }

        if (target == null) {
            // fail to fire
            fighterGame.audio.beepFail()
            return
        }
        // fire
        fighterGame.audio.launch()

        fighterGame.newActors.add(Actor(
                pos = position,
                facing = facing,
                velocity = velocity,
                model = missileType.model!!,
                pilot = MissileGuidance(target, missileType),
                life = Life(1f),
                expiration = Expiration(10000),
                radius = missileType.radius,
                collider = DamageCollider(10f)
        ))

    }
}


enum class Faction {
    UNALIGNED,
    IMPERIAL,
    REBEL;

    fun isEnemy(faction: Faction): Boolean {
        if (faction == UNALIGNED || this == UNALIGNED) {
            return false
        }
        return this == faction
    }
}