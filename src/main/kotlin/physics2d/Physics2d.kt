package physics2d

import marki.GameObject
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World
import org.joml.Vector2f
import physics2d.components.Box2DCollider
import physics2d.components.CircleCollider
import physics2d.components.RigidBody2D
import physics2d.enums.BodyType

class Physics2d {

    private val gravity: Vec2 = Vec2(0f, -10f)
    private val world = World(gravity)
    private var physicsTime = 0f
    private val physicsTimeStep = 1f/60f
    private val velocityIterations = 8
    private val positionIterations = 3

    fun add(go: GameObject) {
        val rb =  go.getComponent(RigidBody2D::class.java)
        if(rb != null && rb.rawBody == null) {
            val tranform = go.transform

            val bodyDef = BodyDef()
            bodyDef.angle = Math.toRadians(tranform.rotation.toDouble()).toFloat()
            bodyDef.position.set(tranform.position.x, tranform.position.y)
            bodyDef.angularDamping = rb.angularDamping
            bodyDef.linearDamping = rb.linearDamping
            bodyDef.fixedRotation = rb.fixedRotation
            bodyDef.bullet = rb.continuousCollision

            bodyDef.type = when(rb.bodyType) {
                BodyType.Static -> org.jbox2d.dynamics.BodyType.STATIC
                BodyType.Dynamic -> org.jbox2d.dynamics.BodyType.DYNAMIC
                BodyType.Kinematic -> org.jbox2d.dynamics.BodyType.KINEMATIC
            }

            val shape = PolygonShape()
            val collider = go.getComponent(CircleCollider::class.java) ?: go.getComponent(Box2DCollider::class.java)
            if(collider is CircleCollider) {
                shape.radius = collider.radius
            } else if (collider is Box2DCollider) {
                val halfSize = Vector2f(collider.halfSize.mul(0.5f))
                val offset = collider.offset
                val origin = Vector2f(collider.origin)
                shape.setAsBox(halfSize.x, halfSize.y, Vec2(origin.x, origin.y), 0f)

                val pos = bodyDef.position
                val xPos = pos.x + offset.x
                val yPos = pos.y + offset.y
                bodyDef.position.set(xPos, yPos)
            }

            val body = world.createBody(bodyDef)
            rb.rawBody = body
            body.createFixture(shape, rb.mass)
        }
    }

    fun update(dt: Float) {
        physicsTime += dt
        if(physicsTime >= 0f){
            physicsTime -= physicsTimeStep
            world.step(physicsTimeStep, velocityIterations, positionIterations)
        }
    }
}