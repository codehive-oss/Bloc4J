package io.codehive.bloc4j.lib

import com.jogamp.opengl.GL.*
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class Texture(
  private val gl: GL3,
  private val imageFile: String,
) {

  private var texture = GLBuffers.newDirectIntBuffer(1)
  private var loaded = false

  fun load() {
    gl.glGenTextures(1, texture)
    gl.glBindTexture(GL_TEXTURE_2D, texture[0])

    gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

    gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

    val image = ImageIO.read(javaClass.getResourceAsStream(imageFile))
    val data = toByteBuffer(image)

    gl.glTexImage2D(
      GL_TEXTURE_2D,
      0,
      GL_RGB,
      image.width,
      image.height,
      0,
      GL_RGBA,
      GL_UNSIGNED_BYTE,
      data
    )
    loaded = true
  }

  fun bind(target: Int) {
    check(loaded) { "Attempted binding texture before loading it" }
    gl.glBindTexture(target, texture[0])
  }

  private fun toByteBuffer(image: BufferedImage): ByteBuffer {
    val width = image.width
    val height = image.height
    val bytes = ByteArray(width * height * 4)
    var i = 0
    for (y in 0..<height) {
      for (x in 0..<width) {
        val c = Color(image.getRGB(x, y))
        bytes[i++] = c.red.toByte()
        bytes[i++] = c.green.toByte()
        bytes[i++] = c.blue.toByte()
        bytes[i++] = c.alpha.toByte()
      }
    }
    return GLBuffers.newDirectByteBuffer(bytes)
  }

}
