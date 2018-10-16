package com.stewsters.fighter

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import java.util.*
import kotlin.math.max
import kotlin.math.pow


class FighterGame : ApplicationAdapter() {

    //    https://stackoverflow.com/questions/17902373/split-screen-in-libgdx
    internal lateinit var cam: PerspectiveCamera
    internal lateinit var modelBatch: ModelBatch
    internal lateinit var asteroidModel: Array<Model>
    internal lateinit var environment: Environment
    internal lateinit var shapeRenderer:ShapeRenderer

    val actors = mutableListOf<Actor>()
    val newActors = mutableListOf<Actor>()
    val removeActors = mutableListOf<Actor>()
    var respawnActors = mutableListOf<Actor>()


    var splitScreen: SplitScreen = SplitScreen.ONE
    var players = mutableListOf<Actor>()

    val audio: AudioManager = AudioManager(this)

    override fun create() {
        audio.init()

        modelBatch = ModelBatch()
        shapeRenderer= ShapeRenderer()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))


//       val loader =  ObjLoader()
//        loader.loadModel(Gdx.files.internal("ship1.obj"))

        val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        val modelBuilder = ModelBuilder()

        asteroidModel = Array(3) {
            val size = (it + 1) * 5f
            modelBuilder.createSphere(size, size, size,
                    5 + it, 5 + it,
                    Material(ColorAttribute.createDiffuse(Color.GRAY)),
                    attr)
        }

        AircraftType.SWORDFISH.model = modelBuilder.createCone(1f, 1f, 0.5f, 6,
                Material(ColorAttribute.createDiffuse(Color.RED)),
                attr)

        AircraftType.TILAPIA.model = modelBuilder.createCylinder(1f, 1f, 1f, 8,
                Material(ColorAttribute.createDiffuse(Color.ORANGE)),
                attr)

        BulletType.RAILGUN.model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                attr)

        BulletType.UGS_8.model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                attr)

        MissileType.VIPER_MK2.model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                Material(ColorAttribute.createDiffuse(Color.CYAN)),
                attr
        )

        MissileType.COBRA.model = modelBuilder.createCone(0.25f, 0.25f, 0.25f, 3,
                Material(ColorAttribute.createDiffuse(Color.CYAN)),
                attr
        )

        val r = Random()
        for (x in 0..5) {
            for (y in 0..5) {
                for (z in 0..5) {
                    val x = x.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                    val y = y.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                    val z = z.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)

                    val size = r.nextInt(asteroidModel.size)
                    actors.add(Actor(
                            Vector3(x, y, z),
                            Quaternion(),
                            0f,
                            asteroidModel[size],
                            radius = (size + 1) * 2.5f,
                            collider = DamageCollider(100f)
                    ))
                }
            }
        }

        val controllers = Controllers.getControllers()

        splitScreen = when (controllers.size) {
            0, 1 -> SplitScreen.ONE
            2 -> SplitScreen.TWO
            else -> SplitScreen.FOUR
        }

        // players
        controllers.forEachIndexed { i, controller ->
            Gdx.app.log("Controller Found", controller.name)
            val aircraftType = AircraftType.SWORDFISH // if(i%2==0)  AircraftType.SWORDFISH else AircraftType.TILAPIA
            val playerStart = PlayerStart.values()[i]
            val actor = Actor(
                    playerStart.pos.cpy().scl(2f),
                    playerStart.rotation.cpy(),
                    300f,
                    aircraftType.model!!,
                    HumanPilot(controller),
                    Life(aircraftType.life),
                    aircraftType,
                    Cannon(BulletType.RAILGUN),
                    MissileRack(MissileType.VIPER_MK2),
                    radius = aircraftType.radius,
                    collider = DamageCollider(4f),
                    respawnable = true
            )
            actors.add(actor)
            players.add(actor)
        }

        // add some computers
        for (i in controllers.size until PlayerStart.values().size - 2) {
            val aircraftType = AircraftType.TILAPIA
            val playerStart = PlayerStart.values()[i]
            actors.add(
                    Actor(
                            playerStart.pos.cpy().scl(2f),
                            playerStart.rotation.cpy(),
                            300f,
                            aircraftType.model!!,
                            AiPilot(),
                            Life(aircraftType.life),
                            aircraftType,
                            primaryWeapon = Cannon(BulletType.UGS_8),
                            secondaryWeapon = MissileRack(MissileType.COBRA),
                            collider = DamageCollider(4f),
                            respawnable = true
                    )
            )
        }

        cam = when (splitScreen) {
            SplitScreen.ONE -> PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            SplitScreen.TWO -> PerspectiveCamera(67f, Gdx.graphics.width.toFloat() / 2f, Gdx.graphics.height.toFloat())
            SplitScreen.FOUR -> PerspectiveCamera(67f, Gdx.graphics.width.toFloat() / 2f, Gdx.graphics.height.toFloat() / 2f)
        }

        cam.near = 1f
        cam.far = 10000f

    }

    private fun look(actor: Actor?) {

        if (actor != null) {
            cam.position.set(actor.pos.cpy().add(Vector3(0f, -5f, 1f).mul(actor.facing)))
            cam.lookAt(actor.pos.cpy().add(Vector3(0f, 1000f, 0f).mul(actor.facing)))
            cam.up.set(Actor.up.cpy().mul(actor.facing))

        } else {
            cam.position.set(size / 2f, size / 2f, size / 2f)
            cam.lookAt(0f, 0f, 0f)
            cam.up.set(0f, 0f, 1f)
        }
        cam.update()
    }

    override fun render() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        val dt = Gdx.graphics.deltaTime


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        for (craft in actors) {
            craft.pilot?.fly(this, craft)
            with(craft) {

                if (pilot != null) {
                    val accel = pilot.getAccel()

                    facing.mul(Quaternion(Actor.up, -1f * pilot.getYaw() * dt))
                    facing.mul(Quaternion(Actor.right, pilot.getPitch() * dt))
                    facing.mul(Quaternion(Actor.forward, pilot.getRoll() * dt))
                    velocity += accel * dt // Speed up
                }

                velocity *= 1f - (0.8f * dt) // Slow down

                pos.add(Vector3(0f, velocity * dt, 0f).mul(facing))
                instance.transform.setToTranslation(pos).rotate(facing)
            }
            if (craft.expiration != null) {
                craft.expiration.check(this, craft)
            }
        }

        // this will be inefficient, we may need to fix that
        for (i in (0 until actors.size)) {
            for (j in (i + 1 until actors.size)) {
                // test collision
                if (actors[i].pos.dst2(actors[j].pos) <= (actors[i].radius + actors[j].radius).pow(2)) {
                    // collide
                    actors[i].collider?.collision(this, actors[i], actors[j])
                    actors[j].collider?.collision(this, actors[j], actors[i])
                }
            }
        }

        for (i in 0 until max(players.size, 1)) {
            val player = players.getOrNull(i)

            when (splitScreen) {
                SplitScreen.ONE -> Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
                SplitScreen.TWO -> {
                    if (i == 0) {
                        Gdx.gl.glViewport(0, 0, Gdx.graphics.width / 2, Gdx.graphics.height)
                    } else {
                        Gdx.gl.glViewport(Gdx.graphics.width / 2, 0, Gdx.graphics.width / 2, Gdx.graphics.height)
                    }
                }
                SplitScreen.FOUR -> {
                    when (i) {
                        0 -> Gdx.gl.glViewport(0, 0, Gdx.graphics.width / 2, Gdx.graphics.height / 2)
                        1 -> Gdx.gl.glViewport(Gdx.graphics.width / 2, 0, Gdx.graphics.width / 2, Gdx.graphics.height / 2)
                        2 -> Gdx.gl.glViewport(0, Gdx.graphics.height / 2, Gdx.graphics.width / 2, Gdx.graphics.height / 2)
                        else -> Gdx.gl.glViewport(Gdx.graphics.width / 2, Gdx.graphics.height / 2, Gdx.graphics.width / 2, Gdx.graphics.height / 2)
                    }
                }
            }

            look(player)


            modelBatch.begin(cam)
            for (craft in actors) {
                modelBatch.render(craft.instance, environment)
            }
            modelBatch.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.setColor(Color.GREEN)
            shapeRenderer.rect(960f - 5, 540f - 5, 10f, 10f)

            shapeRenderer.end()

        }

        // to prevent concurrent modification exceptions
        actors.addAll(newActors)
        newActors.clear()

        if (removeActors.isNotEmpty()) {
            actors.removeAll(removeActors)
            removeActors.clear()
        }

        if (respawnActors.isNotEmpty()) {
            respawnActors.forEach {

                val start = PlayerStart.values()[Random().nextInt(PlayerStart.values().size)]
                Gdx.app.log("start", start.name)
                it.pos.set(start.pos.cpy().scl(2f))
                it.facing.set(start.rotation)
                it.velocity = 300f
                it.life?.cur = it.life?.max ?: 1f

                actors.add(it)
            }
            respawnActors.clear()
        }

    }

    override fun dispose() {
        audio.dispose()
        modelBatch.dispose()
        asteroidModel.forEach { it.dispose() }
        AircraftType.values().forEach { it.model?.dispose() }
        BulletType.values().forEach { it.model?.dispose() }
        MissileType.values().forEach { it.model?.dispose() }
    }
}

const val size = 5f * 40f + 15f

enum class PlayerStart(val pos: Vector3, val rotation: Quaternion) {
    UP(Vector3(0f, 0f, size), Quaternion().setEulerAngles(0f, -90f, 0f)),
    DOWN(Vector3(0f, 0f, -size), Quaternion().setEulerAngles(0f, 90f, 0f)),
    LEFT(Vector3(-size, 0f, 0f), Quaternion().setEulerAngles(0f, 0f, -90f)),
    RIGHT(Vector3(size, 0f, 0f), Quaternion().setEulerAngles(0f, 0f, 90f)),
    FORWARDS(Vector3(0f, -size, 0f), Quaternion().setEulerAngles(0f, 0f, 0f)),
    BACKWARDS(Vector3(0f, size, 0f), Quaternion().setEulerAngles(0f, 0f, 180f)),
}

enum class SplitScreen {
    ONE,
    TWO,
    FOUR
}
