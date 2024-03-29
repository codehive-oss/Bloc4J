package io.codehive.bloc4j.game

import io.codehive.bloc4j.entitiy.Player
import io.codehive.input.KeyboardInput

object Bloc4J {

  val player: Player = Player()
  var cameraEntity = player

  fun update() {
    handleMovementInput()
  }

  private fun handleMovementInput() {
    val movementDelta = 0.1
    if (KeyboardInput.movingForward) {
      player.location.z -= movementDelta
    }
    if (KeyboardInput.movingBackwards) {
      player.location.z += movementDelta
    }
    if (KeyboardInput.movingLeft) {
      player.location.x -= movementDelta
    }
    if (KeyboardInput.movingRight) {
      player.location.x += movementDelta
    }
    if (KeyboardInput.movingUp) {
      player.location.y += movementDelta
    }
    if (KeyboardInput.movingDown) {
      player.location.y -= movementDelta
    }
  }

}
