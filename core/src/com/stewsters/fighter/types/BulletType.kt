package com.stewsters.fighter.types

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute

enum class BulletType(
        val launchVelocity: Float = 6f,
        val refireMS: Long = 1000,
        var model: Model,
        val expiration: Long = 1500,
        val radius: Float = 0.3f,
        val damage: Float = 2f
        // sound?
        // damage?
) {
    RAILGUN(
            launchVelocity = 40f,
            refireMS = 120,
            model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                    Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                    attr)),
    UGS_8(
            launchVelocity = 40f,
            refireMS = 150,
            model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                    Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                    attr)) // bot weapon, objectively worse
}