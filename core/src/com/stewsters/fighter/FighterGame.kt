package com.stewsters.fighter

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
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
    internal lateinit var asteroidModels: Array<Model>
    internal lateinit var environment: Environment
    internal lateinit var shapeRenderer: ShapeRenderer

    val actors = mutableListOf<Actor>()
    val newActors = mutableListOf<Actor>()
    val removeActors = mutableListOf<Actor>()
    var respawnActors = mutableListOf<Actor>()


    var splitScreen: SplitScreen = SplitScreen.ONE
    var players = mutableListOf<Actor>()

    val audio: AudioManager = AudioManager()

    override fun create() {
        audio.init()

        modelBatch = ModelBatch()
        shapeRenderer = ShapeRenderer()


        val mission = campaign.missions.first()

//       val loader =  ObjLoader()
//        loader.loadModel(Gdx.files.internal("ship1.obj"))


        // Setup of scenario
        environment = mission.place.environment

        asteroidModels = Array(3) {
            val size = (it + 1) * 5f
            modelBuilder.createSphere(size, size, size,
                    5 + it, 5 + it,
                    Material(ColorAttribute.createDiffuse(Color.GRAY)),
                    attr)
        }

//        environment = Environment()
//        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
//        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        val r = Random()
        for (x in 0..5) {
            for (y in 0..5) {
                for (z in 0..5) {
                    val x = x.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                    val y = y.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)
                    val z = z.toFloat() * 40f + (r.nextFloat() * 30f - 15f) - (3 * 40)

                    val size = r.nextInt(asteroidModels.size)
                    actors.add(Actor(
                            Vector3(x, y, z),
                            Quaternion(),
                            0f,
                            asteroidModels[size],
                            pilot=DriftPilot(),
                            radius = (size + 1) * 2.5f,
                            collider = DamageCollider(100f)
                    ))
                }
            }
        }

        val controllers = Controllers.getControllers()

        // players
        controllers.forEachIndexed { i, controller ->
            Gdx.app.log("Controller Found, Assigning ship", controller.name)
            val aircraftType = AircraftType.SWORDFISH // if(i%2==0)  AircraftType.SWORDFISH else AircraftType.TILAPIA
            val playerStart = PlayerStart.values()[i]
            val actor = Actor(
                    position = playerStart.pos.cpy().scl(2f),
                    rotation = playerStart.rotation.cpy(),
                    velocity = 300f,
                    model = aircraftType.model,
                    pilot = HumanPilot(controller),
                    life = Life(aircraftType.life),
                    aircraftType = aircraftType,
                    primaryWeapon = Cannon(BulletType.RAILGUN),
                    secondaryWeapon = MissileRack(MissileType.VIPER_MK2),
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
                            position = playerStart.pos.cpy().scl(2f),
                            rotation = playerStart.rotation.cpy(),
                            velocity = 300f,
                            model = aircraftType.model,
                            pilot = AiPilot(),
                            life = Life(aircraftType.life),
                            aircraftType = aircraftType,
                            primaryWeapon = Cannon(BulletType.UGS_8),
                            secondaryWeapon = MissileRack(MissileType.COBRA),
                            collider = DamageCollider(4f),
                            respawnable = true
                    )
            )
        }

        splitScreen = when (controllers.size) {
            0, 1 -> SplitScreen.ONE
            2 -> SplitScreen.TWO
            else -> SplitScreen.FOUR
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
            cam.position.set(actor.position.cpy().add(Vector3(0f, -5f, 1f).mul(actor.rotation)))
            cam.lookAt(actor.position.cpy().add(Vector3(0f, 1000f, 0f).mul(actor.rotation)))
            cam.up.set(Actor.up.cpy().mul(actor.rotation))

        } else {


            val actor = actors.find { it.aircraftType != null }

            if (actor != null) {
                cam.position.set(actor.position.cpy().add(Vector3(0f, -5f, 1f).mul(actor.rotation)))
                var target = (actor!!.pilot as AiPilot).lastTarget?.position
                if (target == null) {
                    target = actor.position.cpy().add(Vector3(0f, 1000f, 0f).mul(actor.rotation))
                }

                cam.lookAt(target)
                cam.up.set(Actor.up.cpy().mul(actor.rotation))

//                cam.lookAt(actor.position)
            } else {
                cam.position.set(size / 2f, size / 2f, size / 2f)
                cam.lookAt(0f, 0f, 0f)
                cam.up.set(0f, 0f, 1f)
            }


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

                    rotation.mul(Quaternion(Actor.up, -1f * pilot.getYaw() * dt))
                    rotation.mul(Quaternion(Actor.right, pilot.getPitch() * dt))
                    rotation.mul(Quaternion(Actor.forward, pilot.getRoll() * dt))
                    velocity += accel * dt // Speed up
                }

                velocity *= 1f - (0.8f * dt) // Slow down

                position.add(Vector3(0f, velocity * dt, 0f).mul(rotation))
                instance.transform.setToTranslation(position).rotate(rotation)
            }
            if (craft.expiration != null) {
                craft.expiration.check(this, craft)
            }
        }

        // this will be inefficient, we may need to fix that
        for (i in (0 until actors.size)) {
            for (j in (i + 1 until actors.size)) {
                // test collision
                if (actors[i].position.dst2(actors[j].position) <= (actors[i].radius + actors[j].radius).pow(2)) {
                    // collide
                    actors[i].collider?.collision(this, actors[i], actors[j])
                    actors[j].collider?.collision(this, actors[j], actors[i])
                }
            }
        }

        for (i in 0 until max(players.size, 1)) {
            val player = players.getOrNull(i)

            val width: Int = Gdx.graphics.width / splitScreen.horizontalDivs
            val height: Int = Gdx.graphics.height / splitScreen.verticalDivs

            val offsetX: Int = if (i % 2 == 1) width else 0
            val offsetY: Int = if (splitScreen == SplitScreen.FOUR && i > 1) height else 0

//            when (splitScreen) {
//                SplitScreen.ONE -> Gdx.gl.glViewport(0, 0, width, height)
//                SplitScreen.TWO -> {
//                    if (i == 0) {
//                        Gdx.gl.glViewport(0, 0, width, height)
//                    } else {
//                        Gdx.gl.glViewport(width, 0, width, height)
//                    }
//                }
//                SplitScreen.FOUR -> {
//                    when (i) {
//                        0 -> Gdx.gl.glViewport(0, 0, width, height)
//                        1 -> Gdx.gl.glViewport(width, 0, width, height)
//                        2 -> Gdx.gl.glViewport(0, height, width, height)
//                        else -> Gdx.gl.glViewport(width, height, width, height)
//                    }
//                }
//            }
            Gdx.gl.glViewport(offsetX, offsetY, width, height)

            look(player)


            modelBatch.begin(cam)
            for (craft in actors) {
                modelBatch.render(craft.instance, environment)
            }
            modelBatch.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(Color.DARK_GRAY)
            shapeRenderer.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.setColor(Color.GREEN)
            shapeRenderer.rect(960f - 5, 540f - 5, 10f, 10f)

            if (player != null) {
                val conj = player.rotation.cpy().conjugate()

                shapeRenderer.rect(0f, 0f, 200f, 200f)
                shapeRenderer.rect(200f, 0f, 200f, 200f)

//                shapeRenderer.rect
                for (craft in actors) {
                    if (craft.aircraftType != null && craft != player) {
                        val pointerToCraft = craft.position.cpy().sub(player.position).mul(conj)
                        val dist = pointerToCraft.dst(0f, 0f, 0f)
                        val x = pointerToCraft.x / dist
                        val y = pointerToCraft.z / dist

                        if (pointerToCraft.y > 0) { // forward arc
                            shapeRenderer.rect(100f + x * 100f, 100f + y * 100f, 1f, 1f)
                        } else {
                            shapeRenderer.rect(300f - x * 100f, 100f - y * 100f, 1f, 1f)
                        }

                    }
                }
            }

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
                it.position.set(start.pos.cpy())
                it.rotation.set(start.rotation)
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
        shapeRenderer.dispose()
        asteroidModels.forEach { it.dispose() }
        AircraftType.values().forEach { it.model.dispose() }
        BulletType.values().forEach { it.model.dispose() }
        MissileType.values().forEach { it.model.dispose() }
    }
}

const val size = (5f * 40f + 15f)

enum class PlayerStart(val pos: Vector3, val rotation: Quaternion) {
    UP(Vector3(0f, 0f, size), Quaternion().setEulerAngles(0f, -90f, 0f)),
    DOWN(Vector3(0f, 0f, -size), Quaternion().setEulerAngles(0f, 90f, 0f)),
    LEFT(Vector3(-size, 0f, 0f), Quaternion().setEulerAngles(0f, 0f, -90f)),
    RIGHT(Vector3(size, 0f, 0f), Quaternion().setEulerAngles(0f, 0f, 90f)),
    FORWARDS(Vector3(0f, -size, 0f), Quaternion().setEulerAngles(0f, 0f, 0f)),
    BACKWARDS(Vector3(0f, size, 0f), Quaternion().setEulerAngles(0f, 0f, 180f)),
}

enum class SplitScreen(val horizontalDivs: Int, val verticalDivs: Int) {
    ONE(1, 1),
    TWO(2, 1),
    FOUR(2, 2)
}
