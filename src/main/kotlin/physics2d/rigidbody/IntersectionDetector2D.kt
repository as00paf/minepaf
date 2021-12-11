package physics2d.rigidbody

import marki.renderer.Line2D
import org.joml.Vector2f
import physics2d.primitives.AABB2D
import physics2d.primitives.Box2D
import physics2d.primitives.Circle
import util.JMath

object IntersectionDetector2D {

    fun pointOnLine(point: Vector2f, line: Line2D):Boolean {
        val dy = line.getEnd().y - line.getStart().x
        val dx = line.getEnd().x - line.getStart().x
        val mag:Float = dy/dx

        val b: Float = line.getEnd().y - (mag * line.getEnd().x)

        // Check the line equation
        return point.y == mag * point.x + b

    }

    fun pointInCircle(point: Vector2f, circle: Circle): Boolean {
        val circleCenter = circle.getCenter()
        val centerToPoint = Vector2f(point).sub(circleCenter)

        return centerToPoint.lengthSquared() < circle.getRadius() * circle.getRadius()
    }

    fun pointInAABB(point: Vector2f, box: AABB2D): Boolean {
        val min = box.getMin()
        val max = box.getMax()

        return point.x <= max.x && min.x <= point.x &&
                point.y <= max.y && min.y <= point.y
    }

    fun pointInBox2D(point: Vector2f, box: Box2D): Boolean {
        // Translate the point into local space
        val pointLocalBoxSpace = Vector2f(point)
        JMath.rotate(pointLocalBoxSpace, box.getPosition(), box.getRotation().toDouble())

        val min = box.getMin()
        val max = box.getMax()

        return pointLocalBoxSpace.x <= max.x && min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.y <= max.y && min.y <= pointLocalBoxSpace.y
    }
}