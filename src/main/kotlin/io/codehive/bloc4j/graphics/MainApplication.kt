package io.codehive.bloc4j.graphics

import com.jogamp.newt.event.KeyEvent
import com.jogamp.opengl.GL.*
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import io.codehive.bloc4j.game.Bloc4J
import io.codehive.bloc4j.graphics.lib.GraphicsApplication
import io.codehive.bloc4j.graphics.lib.ShaderProgram
import io.codehive.bloc4j.graphics.lib.Texture
import io.codehive.bloc4j.graphics.lib.Window
import org.joml.Matrix4f
import org.joml.Vector2i
import java.nio.IntBuffer

class MainApplication : GraphicsApplication {
  private var shouldQuit = false

  private lateinit var vao: IntBuffer
  private lateinit var shaderProgram: ShaderProgram
  private lateinit var texture: Texture

  private var vertices: FloatArray = floatArrayOf(
    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
    0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
    -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
    0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
    0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
    0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
    -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

    -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
    -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
    -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
    0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
    0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
    0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
    0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
    0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
    0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
    -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
    -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
    0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
    0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
    -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
    -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
  )

  private var indices: IntArray = intArrayOf(
    0, 1, 2,
    2, 3, 0
  )

  override fun init(gl: GL3) {
    gl.glEnable(GL_DEPTH_TEST)

    vao = GLBuffers.newDirectIntBuffer(1)
    gl.glGenVertexArrays(1, vao)
    gl.glBindVertexArray(vao.get(0))

    val ebo = GLBuffers.newDirectIntBuffer(1)
    gl.glGenBuffers(1, ebo)
    gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0])

    val indicesBuffer = GLBuffers.newDirectIntBuffer(indices)
    gl.glBufferData(
      GL_ELEMENT_ARRAY_BUFFER, (indicesBuffer.capacity() * Integer.BYTES).toLong(),
      indicesBuffer, GL_STATIC_DRAW
    )

    val vbo = GLBuffers.newDirectIntBuffer(1)
    gl.glGenBuffers(1, vbo)
    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0])

    val floatBuffer = GLBuffers.newDirectFloatBuffer(vertices)
    gl.glBufferData(
      GL_ARRAY_BUFFER, (floatBuffer.capacity() * java.lang.Float.BYTES).toLong(), floatBuffer,
      GL_STATIC_DRAW
    )

    shaderProgram = ShaderProgram(gl, "/shader.vert", "/shader.frag")
    shaderProgram.build()
    shaderProgram.use()

    gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
    gl.glEnableVertexAttribArray(0)

    gl.glVertexAttribPointer(
      1, 2, GL_FLOAT, false, 5 * Float.SIZE_BYTES,
      (3 * Float.SIZE_BYTES).toLong()
    )
    gl.glEnableVertexAttribArray(1)

    texture = Texture(gl, "/textures/dirt.png")
    texture.load()
  }

  override fun display(gl: GL3, frame: Int) {
    Bloc4J.update()

    gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
    gl.glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    texture.bind(GL_TEXTURE_2D)

    val model = Matrix4f()

    val cameraLoc = Bloc4J.cameraEntity.location
    val view = Matrix4f().translate(
      (-cameraLoc.x).toFloat(),
      (-cameraLoc.y).toFloat(),
      (-cameraLoc.z).toFloat()
    )

    val projection =
      Matrix4f().perspective(
        Math.toRadians(45.toDouble()).toFloat(),
        Window.width.toFloat() / Window.height,
        0.1f,
        100f
      )

    shaderProgram.use()
    shaderProgram.setMat4f("model", model)
    shaderProgram.setMat4f("view", view)
    shaderProgram.setMat4f("projection", projection)

    gl.glBindVertexArray(vao[0])

    gl.glDrawArrays(GL_TRIANGLES, 0, 36)
//    gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
  }

  override fun reshape(gl: GL3, dimensions: Vector2i) {
    gl.glViewport(0, 0, dimensions.x, dimensions.y)
  }

  override fun keyPressed(keyEvent: KeyEvent) {
    when (keyEvent.keyCode) {
      KeyEvent.VK_ESCAPE -> shouldQuit = true
    }
  }

  override fun end(gl: GL3) {
    shaderProgram.delete()
    gl.glDeleteVertexArrays(1, vao)
  }

  override fun shouldQuit(): Boolean {
    return shouldQuit
  }
}
