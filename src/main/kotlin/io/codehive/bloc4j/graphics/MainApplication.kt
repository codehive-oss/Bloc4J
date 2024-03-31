package io.codehive.bloc4j.graphics

import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.MouseEvent
import com.jogamp.opengl.GL.*
import com.jogamp.opengl.GL3
import io.codehive.bloc4j.game.Bloc4J
import io.codehive.bloc4j.graphics.lib.GraphicsApplication
import io.codehive.bloc4j.graphics.lib.ShaderProgram
import io.codehive.bloc4j.graphics.lib.Texture
import io.codehive.bloc4j.graphics.lib.Window
import io.codehive.bloc4j.world.Chunk
import org.joml.Matrix4f
import org.joml.Vector2i
import org.joml.Vector3f

class MainApplication : GraphicsApplication {
  private var shouldQuit = false

  private lateinit var shaderProgram: ShaderProgram
  private lateinit var texture: Texture

  var chunks: Array<Chunk> = Array(27) {
    Chunk(0, 0, 0)
  }

  init {

    var i = 0
    for (x in -1..1) {
      for (y in -1..1) {
        for (z in -1..1) {
          chunks[i] = Chunk(x, y, z)
          chunks[i].generate()
          i++
        }
      }
    }
  }

  override fun init(gl: GL3) {
    gl.glEnable(GL_DEPTH_TEST)

    for (chunk in chunks) {
      chunk.recalculate(gl)
    }

    gl.glEnable(GL_CULL_FACE)
    gl.glCullFace(GL_FRONT)

    shaderProgram = ShaderProgram(gl, "/shader.vert", "/shader.frag")
    shaderProgram.build()
    shaderProgram.use()

    texture = Texture(gl, "/textures/atlas.png")
    texture.load()
  }

  override fun display(gl: GL3, frame: Int) {
    Bloc4J.update()

    gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
    gl.glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    texture.bind(GL_TEXTURE_2D)

    val model = Matrix4f()

    val cameraLoc = Bloc4J.cameraEntity.target.location

    val view = Matrix4f()
      .lookAt(
        cameraLoc.toVec3f(),
        cameraLoc.toVec3f().add(Bloc4J.cameraEntity.front),
        Vector3f(0f, 1f, 0f)
      )
      .translate(
        (-cameraLoc.x),
        (-cameraLoc.y),
        (-cameraLoc.z)
      )


    val projection =
      Matrix4f().perspective(
        Math.toRadians(90.toDouble()).toFloat(),
        Window.width.toFloat() / Window.height,
        0.1f,
        100f
      )

    shaderProgram.use()
    shaderProgram.setMat4f("view", view)
    shaderProgram.setMat4f("projection", projection)
    shaderProgram.setMat4f("model", model)

    for (chunk in chunks) {
      chunk.render()
    }
  }

  override fun reshape(gl: GL3, dimensions: Vector2i) {
    gl.glViewport(0, 0, dimensions.x, dimensions.y)
  }

  override fun keyPressed(keyEvent: KeyEvent) {
    when (keyEvent.keyCode) {
      KeyEvent.VK_ESCAPE -> shouldQuit = true
    }
  }

  override fun mouseMoved(mouseEvent: MouseEvent) {
  }

  override fun end(gl: GL3) {
    shaderProgram.delete()
  }

  override fun shouldQuit(): Boolean {
    return shouldQuit
  }
}
