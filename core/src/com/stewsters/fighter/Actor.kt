package com.stewsters.fighter

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

open class Actor(
        val position: Vector3,
        val rotation: Quaternion,
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
        instance.transform.setToTranslation(position).rotate(rotation)
    }
}


class Life(val max: Float, var cur: Float = max) {

    fun takeDamage(fighterGame: FighterGame, actor: Actor, float: Float) {
        Gdx.app.log("Aircraft", "Took $float damage")
        cur -= float

        if (cur <= 0) {
            die(fighterGame, actor)
        }
    }

    fun die(fighterGame: FighterGame, actor: Actor) {
        fighterGame.audio.explode()
        fighterGame.removeActors.add(actor)
        if (actor.respawnable) {
            fighterGame.respawnActors.add(actor)
        }
        Gdx.app.log("Destruction", "Target Exploded")
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


enum class Faction {
    UNALIGNED,
    ALDRONI,
    DOMINION;

    fun isEnemy(faction: Faction): Boolean {
        if (faction == UNALIGNED || this == UNALIGNED) {
            return false
        }
        return this == faction
    }
}