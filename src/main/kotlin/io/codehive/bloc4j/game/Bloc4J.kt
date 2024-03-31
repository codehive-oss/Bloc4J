package io.codehive.bloc4j.game

import io.codehive.bloc4j.entity.Camera
import io.codehive.bloc4j.entity.Player
import io.codehive.bloc4j.input.KeyboardInput
import org.joml.Math
import org.joml.Vector3f

object Bloc4J {

  val player: Player = Player()
  var cameraEntity = Camera(player)

  fun update() {
    handleMovementInput()
    handleCameraMovement()
  }

  private fun handleMovementInput() {
    val cameraUp = Vector3f(0f, 1f, 0f)

    var moveDir = Vector3f(0f, 0f, 0f)
    if (KeyboardInput.movingForward) {
      moveDir.add(cameraEntity.front)
      moveDir.y = 0f
    }
    if (KeyboardInput.movingBackwards) {
      moveDir.add(cameraEntity.front.mul(-1f))
      moveDir.y = 0f
    }
    if (KeyboardInput.movingLeft) {
      moveDir.add(Vector3f(cameraEntity.front).cross(cameraUp).mul(-1f))
      moveDir.y = 0f
    }
    if (KeyboardInput.movingRight) {
      moveDir.add(Vector3f(cameraEntity.front).cross(cameraUp))
      moveDir.y = 0f
    }
    if (KeyboardInput.movingUp) {
      moveDir.add(Vector3f(0f, 1f, 0f))
    }
    if (KeyboardInput.movingDown) {
      moveDir.add(Vector3f(0f, -1f, 0f))
    }

    if (moveDir.lengthSquared() < 0.001) {
      return
    }

    val movementDelta = 0.05f
    moveDir = moveDir.normalize().mul(movementDelta)

    player.location.add(moveDir)
  }

  private fun handleCameraMovement() {
    // val cameraDelta = 0.01f
    // cameraEntity.target.rotation.yaw -= MouseInput.dx * cameraDelta
    // cameraEntity.target.rotation.pitch += MouseInput.dy * cameraDelta

    cameraEntity.target.rotation.pitch =
      Math.clamp(-89f, 89f, cameraEntity.target.rotation.pitch)

    cameraEntity.updateFront()
  }
}