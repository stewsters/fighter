package com.stewsters.fighter

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder

val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
val modelBuilder = ModelBuilder()

enum class AircraftType(
        val acceleration: Float = 1f,

        val turn: Float = 10f,
        val turnYaw: Float = turn,
        var model: Model,
        val life: Float = 1f,
        val radius: Float = 1f
) {
    SWORDFISH(
            acceleration = 30f,
            turn = 80f,
            turnYaw = 60f,
            life = 30f,
            radius = 0.8f,
            model = modelBuilder.createCone(1f, 1f, 0.5f, 6,
                    Material(ColorAttribute.createDiffuse(Color.RED)),
                    attr)),
    TILAPIA(
            acceleration = 28f,
            turn = 60f,
            life = 30f,
            radius = 0.8f,
            model = modelBuilder.createCylinder(1f, 1f, 1f, 8,
                    Material(ColorAttribute.createDiffuse(Color.ORANGE)),
                    attr)
    ); // NPC ships, we want to be better than them
}


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
    RAILGUN(launchVelocity = 40f,
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
