package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import io.codehive.bloc4j.graphics.lib.Mesh
import main.ru.aengine.noise.NoiseGenerator
import org.joml.Vector3f

class Chunk(
  val x: Int,
  val y: Int,
  val z: Int
) {

  lateinit var mesh: Mesh

  val data = ByteArray(16 * 16 * 16)

  fun fill(type: BlockType) {
    for (i in data.indices) {
      data[i] = type.id
    }
  }

  fun generate() {
    val generator = NoiseGenerator()
    for (y in 0..<16) {
      for (z in 0..<16) {
        for (x in 0..<16) {
          val index = y * 16 * 16 + z * 16 + x
          val value = generator.noise(
            (this.x * 16 + x).toDouble(),
            (this.y * 16 + y).toDouble(),
            (this.z * 16 + z).toDouble(),
            35
          )
          if (value > 0) {
            data[index] = BlockType.DIRT.id
          } else {
            data[index] = BlockType.AIR.id
          }
        }
      }
    }
  }

  fun recalculate(gl: GL3) {
    val positions: ArrayList<Float> = ArrayList()
    val uvs: ArrayList<Float> = ArrayList()
    val indices: ArrayList<Int> = ArrayList()
    var currentIndex = 0

    for (y in 0..<16) {
      for (z in 0..<16) {
        for (x in 0..<16) {
          val index = y * 16 * 16 + z * 16 + x
          val type = data[index]
          if (type == BlockType.AIR.id) {
            continue
          }

          val offset = Vector3f(
            this.x * 16 + x.toFloat(),
            this.y * 16 + y.toFloat(),
            this.z * 16 + z.toFloat()
          )

          val topIndex = index + 16 * 16
          if (y + 1 >= 16 || data[topIndex] == BlockType.AIR.id) {
//          if (data[topIndex] == BlockType.AIR.id) {
            appendData(positions, uvs, indices, offset, currentIndex, BlockFace.TOP)
            currentIndex += 4
          }

          val bottomIndex = index - 16 * 16
          if (y - 1 < 0 || data[bottomIndex] == BlockType.AIR.id) {
//          if (data[bottomIndex] == BlockType.AIR.id) {
            appendData(positions, uvs, indices, offset, currentIndex, BlockFace.BOTTOM)
            currentIndex += 4
          }

          val eastIndex = index + 1
          if (x + 1 >= 16 || data[eastIndex] == BlockType.AIR.id) {
//          if (data[eastIndex] == BlockType.AIR.id) {
            appendData(positions, uvs, indices, offset, currentIndex, BlockFace.EAST)
            currentIndex += 4
          }

          val westIndex = index - 1
          if (x - 1 < 0 || data[westIndex] == BlockType.AIR.id) {
//          if (data[westIndex] == BlockType.AIR.id) {
            appendData(positions, uvs, indices, offset, currentIndex, BlockFace.WEST)
            currentIndex += 4
          }

          val northIndex = index - 16
          if (z - 1 < 0 || data[northIndex] == BlockType.AIR.id) {
//          if (data[northIndex] == BlockType.AIR.id) {
            appendData(positions, uvs, indices, offset, currentIndex, BlockFace.NORTH)
            currentIndex += 4
          }

          val southIndex = index + 16
          if (z + 1 >= 16 || data[southIndex] == BlockType.AIR.id) {
//          if (data[southIndex] == BlockType.AIR.id) {
            appendData(positions, uvs, indices, offset, currentIndex, BlockFace.SOUTH)
            currentIndex += 4
          }
        }
      }
    }

    mesh = Mesh(gl, positions.toFloatArray(), uvs.toFloatArray(), indices.toIntArray())
    mesh.build()
  }

  fun render() {
    mesh.render()
  }

  private fun appendData(
    positions: ArrayList<Float>,
    uvs: ArrayList<Float>,
    indices: ArrayList<Int>,
    offset: Vector3f,
    currentIndex: Int,
    face: BlockFace
  ) {
    val (pos, uv, idx) = createFace(offset, currentIndex, face)
    positions.addAll(pos)
    uvs.addAll(uv)
    indices.addAll(idx)
  }

  private fun createFace(
    offsets: Vector3f,
    currentIndex: Int,
    face: BlockFace
  ): Triple<Array<Float>, Array<Float>, Array<Int>> {
    val x = offsets.x
    val y = offsets.y
    val z = offsets.z

    val idxs = arrayOf(
      currentIndex + 0,
      currentIndex + 1,
      currentIndex + 2,
      currentIndex + 2,
      currentIndex + 3,
      currentIndex + 0,
    )

    val uvs = arrayOf(
      0.0f, 0.0f,
      1.0f, 0.0f,
      1.0f, 1.0f,
      0.0f, 1.0f,
    )

    val verts = when (face) {
      BlockFace.TOP -> arrayOf(
        0.5f + x, 0.5f + y, 0.5f + z, // bottom right
        -0.5f + x, 0.5f + y, 0.5f + z, // bottom left
        -0.5f + x, 0.5f + y, -0.5f + z, // top left
        0.5f + x, 0.5f + y, -0.5f + z, // top right
      )

      BlockFace.BOTTOM -> arrayOf(
        -0.5f + x, -0.5f + y, 0.5f + z, // bottom left
        0.5f + x, -0.5f + y, 0.5f + z, // bottom right
        0.5f + x, -0.5f + y, -0.5f + z, // top right
        -0.5f + x, -0.5f + y, -0.5f + z, // top left
      )

      BlockFace.EAST -> arrayOf(
        0.5f + x, -0.5f + y, -0.5f + z, // bottom left
        0.5f + x, -0.5f + y, 0.5f + z, // bottom right
        0.5f + x, 0.5f + y, 0.5f + z, // top right
        0.5f + x, 0.5f + y, -0.5f + z, // top left
      )

      BlockFace.WEST -> arrayOf(
        -0.5f + x, -0.5f + y, 0.5f + z, // bottom left
        -0.5f + x, -0.5f + y, -0.5f + z, // bottom right
        -0.5f + x, 0.5f + y, -0.5f + z, // top right
        -0.5f + x, 0.5f + y, 0.5f + z, // top left
      )

      BlockFace.NORTH -> arrayOf(
        -0.5f + x, -0.5f + y, -0.5f + z, // bottom left
        0.5f + x, -0.5f + y, -0.5f + z, // bottom right
        0.5f + x, 0.5f + y, -0.5f + z, // top right
        -0.5f + x, 0.5f + y, -0.5f + z, // top left
      )

      BlockFace.SOUTH -> arrayOf(
        0.5f + x, -0.5f + y, 0.5f + z, // bottom right
        -0.5f + x, -0.5f + y, 0.5f + z, // botom left
        -0.5f + x, 0.5f + y, 0.5f + z, // top left
        0.5f + x, 0.5f + y, 0.5f + z, // top right
      )
    }

    return Triple(verts, uvs, idxs)
  }
}
