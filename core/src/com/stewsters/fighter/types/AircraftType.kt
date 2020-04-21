package com.stewsters.fighter.types

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute

enum class AircraftType(
        val acceleration: Float = 0f,
        val turn: Float = 0f,
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
            model = loader.loadModel(Gdx.files.internal("F_22_Raptor.obj"))
    ),
    TILAPIA(    // NPC ships, we want to be better than them
            acceleration = 28f,
            turn = 60f,
            life = 30f,
            radius = 0.8f,
            model = modelBuilder.createCylinder(1f, 1f, 1f, 8,
                    Material(ColorAttribute.createDiffuse(Color.ORANGE)),
                    attr)
    ),
    STORAGE(
            life = 100f,
            radius = 5f,
            model = modelBuilder.createBox(10f, 10f, 10f,
                    Material(ColorAttribute.createDiffuse(Color.SALMON)),
                    attr
            )
    )

}