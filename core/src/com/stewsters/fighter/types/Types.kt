package com.stewsters.fighter.types

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3

const val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
val modelBuilder = ModelBuilder()
val loader = ObjLoader()

// Some basis Vectors
val up = Vector3(0f, 0f, 1f)
val down = Vector3(0f, 0f, -1f)
val forward = Vector3(0f, 1f, 0f)
val backward = Vector3(0f, -1f, 0f)
val right = Vector3(1f, 0f, 0f)
val left = Vector3(-1f, 0f, 0f)