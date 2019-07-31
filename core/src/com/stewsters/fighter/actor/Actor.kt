package com.stewsters.fighter.actor

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.stewsters.fighter.actor.pilot.Pilot
import com.stewsters.fighter.actor.weapon.Weapon
import com.stewsters.fighter.types.AircraftType

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


    val instance = ModelInstance(model)


    init {
        instance.transform.setToTranslation(position).rotate(rotation)
    }
}


