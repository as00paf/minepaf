package physics2d.rigidbody

import marki.renderer.Line2D
import org.joml.Vector2f
import physics2d.primitives.*
import util.JMath
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.sqrt

object IntersectionDetector2D {

    fun pointOnLine(point: Vector2f, line: Line2D):Boolean {
        val dy = line.getEnd().y - line.getStart().y
        val dx = line.getEnd().x - line.getStart().x
        if(dx == 0f) return JMath.compare(point.x, line.getStart().x)
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

    fun lineAndCircle(line: Line2D, circle: Circle):Boolean {
        if(pointInCircle(line.getStart(), circle) || pointInCircle(line.getEnd(), circle)) return true
        else {
            val ab = Vector2f(line.getEnd().sub(line.getStart()))

            // Project point (circle position) onto ab (line segment)
            // parametriezed position t
            val circleCenter = Vector2f(circle.getCenter())
            val centerToLineStart = Vector2f(circleCenter).sub(line.getStart())
            val t = centerToLineStart.dot(ab) / ab.dot(ab)

            if(t < 0f || t > 1f) return false


            // Find the closest point to the line segment
            val closestPoint = Vector2f(line.getStart()).add(ab.mul(t))

            return pointInCircle(closestPoint, circle)
        }
    }

    fun lineAndAABB(line: Line2D, box: AABB2D): Boolean {
        if(pointInAABB(line.getStart(), box) || pointInAABB(line.getEnd(), box)) {
            return true
        }

        val unitVec = Vector2f(line.getEnd()).sub(line.getStart())
        unitVec.normalize()
        unitVec.x = if (unitVec.x != 0f) 1f/unitVec.x else 0f
        unitVec.y = if (unitVec.y != 0f) 1f/unitVec.y else 0f

        val min = Vector2f(box.getMin())
        min.sub(line.getStart()).mul(unitVec)

        val max = Vector2f(box.getMax ())
        max.sub(line.getStart()).mul(unitVec)

        val tmin = max(min(min.x, max.x), min(min.y, max.y))
        val tmax = min(max(min.x, max.x), min(min.y, max.y))

        if(tmax < 0f || tmin > tmax) return false

        val t = if(tmin < 0f) tmax else tmin

        return t > 0f && t * t < line.lengthSquared()
    }

    fun lineAndBox2D(line: Line2D, box: Box2D) : Boolean {
        val theta = (box.rigidBody?.rotation ?: 0f).toDouble()
        val center = box.rigidBody?.position ?: Vector2f()
        val localStart = Vector2f(line.getStart())
        val localEnd = Vector2f(line.getEnd())
        JMath.rotate(localStart, center, -theta)
        JMath.rotate(localEnd, center, -theta)

        val localLine = Line2D(localStart, localEnd)
        val aabb = AABB2D.initWithMinMax(box.getMin(), box.getMax())

        return lineAndAABB(localLine, aabb)
    }

    // Raycasts
    fun raycast(circle: Circle, ray: Ray2D, result: RaycastResult?) : Boolean {
        result?.let { RaycastResult.reset(it) }

        val originToCircle = Vector2f(circle.getCenter().sub(ray.getOrigin()))
        val radiusSquared = circle.getRadius() * circle.getRadius()
        val originToCircleLengthSquared = originToCircle.lengthSquared()

        // Project the vector from the ray origin onto the direction of the ray
        val a = originToCircle.dot(ray.getDirection())
        val bSq = originToCircleLengthSquared - (a * a)
        if(radiusSquared - bSq < 0f) return false

        val f = sqrt(radiusSquared - bSq)
        var t = if(originToCircleLengthSquared < radiusSquared) {
            // Ray starts inside the circle
            a + f
        } else{
            a - f
        }

        result?.let {
            val point = Vector2f(ray.getOrigin()).add(Vector2f(ray.getDirection()).mul(t))
            val normal = Vector2f(point).sub(circle.getCenter())
            normal.normalize()
            result.init(point, normal, t , true)
        }

        return true
    }

    fun raycast(box: AABB2D, ray: Ray2D, result: RaycastResult?):Boolean {
        result?.let { RaycastResult.reset(it) }
        val unitVec = ray.getDirection()
        unitVec.normalize()
        unitVec.x = if (unitVec.x != 0f) 1f/unitVec.x else 0f
        unitVec.y = if (unitVec.y != 0f) 1f/unitVec.y else 0f

        val min = Vector2f(box.getMin())
        min.sub(ray.getOrigin()).mul(unitVec)

        val max = Vector2f(box.getMax ())
        max.sub(ray.getOrigin()).mul(unitVec)

        val tmin = max(min(min.x, max.x), min(min.y, max.y))
        val tmax = min(max(min.x, max.x), min(min.y, max.y))

        if(tmax < 0f || tmin > tmax) return false

        val t = if(tmin < 0f) tmax else tmin
        val hit = t > 0f

        if(!hit) return false

        result?.let{
            val point = Vector2f(ray.getOrigin()).add(Vector2f(ray.getDirection().mul(t)))
            val normal = Vector2f(ray.getOrigin()).sub(point)
            normal.normalize()
            result.init(point, normal, t , true)
        }

        return true
    }

    fun raycast(box: Box2D, ray: Ray2D, result: RaycastResult?):Boolean {
        result?.let { RaycastResult.reset(it) }

        val size = box.getHalfSize()
        val body = box.rigidBody ?: return false
        val xAxis = Vector2f(1f, 0f)
        val yAxis = Vector2f(0f, 1f)
        JMath.rotate(xAxis, Vector2f(0f, 0f), -body.rotation.toDouble())
        JMath.rotate(yAxis, Vector2f(0f, 0f), -body.rotation.toDouble())

        val p = Vector2f(body.position).sub(ray.getOrigin())

        // Project the direction of the ray onto each axis of the box
        val f = Vector2f(xAxis.dot(ray.getDirection()), yAxis.dot(ray.getDirection()))

        // Project p onto every axis of the box
        val e = Vector2f(xAxis.dot(p), yAxis.dot(p))

        val tArray = arrayOf(0f, 0f, 0f, 0f)
        for(i in 0..2) {
            if(JMath.compare(f.get(i), 0f)){
                // If the ray is parallel to the current axis and the origin of the ray is not inside, no hit
                if(-e.get(i) - size.get(i) > 0f || -e.get(i) + size.get(i) < 0f) {
                    return false
                }

                f.setComponent(i, 0.00001f) // Set it to small value to avoid divide by 0
            }

            tArray[i * 2 + 0] = (e.get(i) + size.get(i)) / f.get(i) // tmax for this axis
            tArray[i * 2 + 1] = (e.get(i) - size.get(i)) / f.get(i) // tmin fot this axis
        }

        val tmin = max(min(tArray[0], tArray[1]), min(tArray[2], tArray[3]))
        val tmax = min(max(tArray[0], tArray[1]), max(tArray[2], tArray[3]))

        if(tmax < 0f || tmin > tmax) return false

        val t = if(tmin < 0f) tmax else tmin
        val hit = t > 0f

        if(!hit) return false

        result?.let{
            val point = Vector2f(ray.getOrigin()).add(Vector2f(ray.getDirection().mul(t)))
            val normal = Vector2f(ray.getOrigin()).sub(point)
            normal.normalize()
            result.init(point, normal, t , true)
        }

        return true
    }

    // Circle vs Primitive
    fun circleAndLine(circle: Circle, line: Line2D):Boolean {
        return lineAndCircle(line, circle)
    }

    fun circleAndCircle(c1: Circle, c2: Circle):Boolean {
        val vecBetweenCenters = Vector2f(c1.getCenter()).sub(c2.getCenter())
        val radiiSum = c1.getRadius() + c2.getRadius()
        return vecBetweenCenters.lengthSquared() <= radiiSum * radiiSum
    }

    fun circleAndAABB(circle:Circle, box: AABB2D) : Boolean {
        val min = box.getMin()
        val max = box.getMax()
        val closesPointToCircle = Vector2f(circle.getCenter())
        if(closesPointToCircle.x < min.x) {
            closesPointToCircle.x = min.x
        }else if(closesPointToCircle.x > max.x){
            closesPointToCircle.x = max.x
        }
        if(closesPointToCircle.y < min.y) {
            closesPointToCircle.y = min.y
        }else if(closesPointToCircle.y > max.y){
            closesPointToCircle.y = max.y
        }

        val circleToBox = Vector2f(circle.getCenter()).sub(closesPointToCircle)
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius()
    }

    fun circleAndBox(circle: Circle, box: Box2D): Boolean {
        // Treat the box just like an AABB, after we rotate the stuff
        val min = Vector2f()
        val max = Vector2f(box.getHalfSize().mul(2f))

        // Create a circle in box's local space
        val body = box.rigidBody ?: return false
        val r = Vector2f(circle.getCenter() ).sub(body.position)
        JMath.rotate(r, Vector2f(0f, 0f), -body.rotation.toDouble())

        val localCirclePos = Vector2f(r).add(box.getHalfSize())

        val closesPointToCircle = Vector2f(localCirclePos)
        if(closesPointToCircle.x < min.x) {
            closesPointToCircle.x = min.x
        }else if(closesPointToCircle.x > max.x){
            closesPointToCircle.x = max.x
        }
        if(closesPointToCircle.y < min.y) {
            closesPointToCircle.y = min.y
        }else if(closesPointToCircle.y > max.y){
            closesPointToCircle.y = max.y
        }

        val circleToBox = Vector2f(localCirclePos).sub(closesPointToCircle)
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius()
    }

    // AABB vs Primitives
    fun aabbAndCircle(circle: Circle, box: AABB2D):Boolean {
        return circleAndAABB(circle, box)
    }

    fun aabbAndAabb(b1: AABB2D, b2: AABB2D):Boolean {
        val axisToTest = arrayOf(Vector2f(0f, 1f), Vector2f(1f, 0f))
        for(i in 0..axisToTest.size) {
            if(!overlapOnAxis(b1, b2, axisToTest[i])){
                return false
            }
        }

        return true
    }

    fun aabbAndBox2D(b1: AABB2D, b2: Box2D):Boolean {
        val axisToTest = arrayOf(
            Vector2f(0f, 1f), Vector2f(1f, 0f),
            Vector2f(0f, 1f), Vector2f(1f, 0f)
        )
        JMath.rotate(axisToTest[2], Vector2f(0f, 0f), b2.rigidBody.rotation.toDouble())
        JMath.rotate(axisToTest[3], Vector2f(0f, 0f), b2.rigidBody.rotation.toDouble())

        for(i in 0..axisToTest.size) {
            if(!overlapOnAxis(b1, b2, axisToTest[i])){
                return false
            }
        }

        return true
    }

    // Helpers
    private fun overlapOnAxis(b1: AABB2D, b2: AABB2D, axis: Vector2f): Boolean {
        val interval1 = getInterval(b1, axis)
        val interval2 = getInterval(b2, axis)
        return ((interval2.x <= interval2.y) && (interval1.x <= interval2.y))
    }

    private fun overlapOnAxis(b1: AABB2D, b2: Box2D, axis: Vector2f): Boolean {
        val interval1 = getInterval(b1, axis)
        val interval2 = getInterval(b2, axis)
        return ((interval2.x <= interval2.y) && (interval1.x <= interval2.y))
    }

    private fun overlapOnAxis(b1: Box2D, b2: Box2D, axis: Vector2f): Boolean {
        val interval1 = getInterval(b1, axis)
        val interval2 = getInterval(b2, axis)
        return ((interval2.x <= interval2.y) && (interval1.x <= interval2.y))
    }

    private fun getInterval(rect: AABB2D, axis: Vector2f): Vector2f {
        val result = Vector2f(0f, 0f)
        val min = rect.getMin()
        val max = rect.getMax()

        val vertices = arrayOf(
            Vector2f(min.x, min.y), Vector2f(min.x, max.y),
            Vector2f(max.x, min.y), Vector2f(max.x, max.y)
        )

        result.x = axis.dot(vertices[0])
        result.y = result.x
        for(i in 0..4){
            val projection = axis.dot(vertices[i])
            if(projection < result.x) result.x = projection
            if(projection > result.y) result.y = projection
        }

        return result
    }

    private fun getInterval(rect: Box2D, axis: Vector2f): Vector2f {
        val result = Vector2f(0f, 0f)
        val vertices = rect.getVertices()

        result.x = axis.dot(vertices[0])
        result.y = result.x
        for(i in 0..4){
            val projection = axis.dot(vertices[i])
            if(projection < result.x) result.x = projection
            if(projection > result.y) result.y = projection
        }

        return result
    }
}