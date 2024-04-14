package io.codehive.bloc4j.world

import com.bulletphysics.collision.broadphase.BroadphaseInterface
import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.collision.shapes.CollisionShape
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.DynamicsWorld
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import io.codehive.bloc4j.entity.Entity
import io.codehive.bloc4j.game.Bloc4J
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f


class WorldPhysics {

  val dynamicsWorld: DynamicsWorld

  init {

    val broadphase: BroadphaseInterface = DbvtBroadphase()
    val collisionConfiguration = DefaultCollisionConfiguration()
    val dispatcher = CollisionDispatcher(collisionConfiguration)

    val solver = SequentialImpulseConstraintSolver()

    dynamicsWorld =
      DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration)


    // set the gravity of our world
    dynamicsWorld.setGravity(Vector3f(0f, -10f, 0f))
  }

  fun addEntityToWorld(entity: Entity) {
    val fallShape: CollisionShape = BoxShape(Vector3f(0.5f, 1f, 0.5f))

    // setup the motion state for the ball
    val fallMotionState =
      DefaultMotionState(
        Transform(
          Matrix4f(
            Quat4f(0f, 0f, 0f, 1f),
            Vector3f(entity.location.x, entity.location.y, entity.location.z),
            1.0f
          )
        )
      )


    //This we're going to give mass so it responds to gravity
    val mass = 1

    val fallInertia = Vector3f(0f, 0f, 0f)
    fallShape.calculateLocalInertia(mass.toFloat(), fallInertia)

    val fallRigidBodyCI =
      RigidBodyConstructionInfo(mass.toFloat(), fallMotionState, fallShape, fallInertia)
    val fallRigidBody = RigidBody(fallRigidBodyCI)
    dynamicsWorld.addRigidBody(fallRigidBody)
    entity.rigidBody = fallRigidBody
  }

  fun update(deltaTime: Long) {
    dynamicsWorld.stepSimulation(deltaTime / 1000f, 10)

    val loc = Bloc4J.player.location
    val rigidBody = Bloc4J.player.rigidBody
    val transform = Transform()
    Bloc4J.player.rigidBody?.getWorldTransform(transform)

    val oldVelocity = Vector3f()
    rigidBody?.getLinearVelocity(oldVelocity)
    oldVelocity.z = 1f
    Bloc4J.player.rigidBody?.setLinearVelocity(oldVelocity)

    loc.x = transform.origin.x
    loc.y = transform.origin.y
    loc.z = transform.origin.z
  }

}
