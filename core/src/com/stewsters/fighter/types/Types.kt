package com.stewsters.fighter.types

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder

const val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
val modelBuilder = ModelBuilder()
val loader = ObjLoader()


