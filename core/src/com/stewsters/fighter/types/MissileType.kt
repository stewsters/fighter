package com.stewsters.fighter.types

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute

enum class MissileType(
        val refireMS: Long,
        val damage: Float,
        val explosionRadius: Float,
        val radius: Float = 0.3f,
        var model: Model,
        val launchVelocity: Float = 5f,
        val acceleration: Float = 20f,
        val turn: Float = 30f,
        val expiration: Long
) {
    VIPER_MK2(
            refireMS = 3000,
            damage = 20f,
            explosionRadius = 5f,
            acceleration = 35f,
            turn = 65f,
            expiration = 10000,
            model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                    Material(ColorAttribute.createDiffuse(Color.CYAN)),
                    attr
            )

    ),
    COBRA(
            refireMS = 10000,
            damage = 8f,
            explosionRadius = 5f,
            acceleration = 15f,
            expiration = 8000,
            model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                    Material(ColorAttribute.createDiffuse(Color.CYAN)),
                    attr
            )

    ), // bot weapon
    ANACONDA(
            refireMS = 20000,
            damage = 100f,
            explosionRadius = 20f,
            radius = 1f,
            acceleration = 15f,
            turn = 15f,
            expiration = 30000,
            model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                    Material(ColorAttribute.createDiffuse(Color.CYAN)),
                    attr
            )
    )

}