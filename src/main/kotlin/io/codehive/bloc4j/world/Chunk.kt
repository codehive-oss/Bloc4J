package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import io.codehive.bloc4j.graphics.lib.Mesh
import org.joml.Vector3f
import org.joml.Vector3i

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
    val perlin = PerlinNoiseGenerator.newBuilder().setSeed(69420).build()

    for (y in 0..<16) {
      for (z in 0..<16) {
        for (x in 0..<16) {
          val index = y * 16 * 16 + z * 16 + x
          val value = perlin.evaluateNoise(
                this.x + x.toDouble() / 16,
                this.y + y.toDouble() / 16,
                this.z + z.toDouble() / 16,
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

  fun positionToIndex(pos: Vector3i): Int {
    return pos.y * 16 * 16 + pos.z * 16 + pos.x
  }

  fun recalculate(gl: GL3) {
    val positions: ArrayList<Float> = ArrayList()
    val uvs: ArrayList<Float> = ArrayList()
    val indices: ArrayList<Int> = ArrayList()
    var currentIndex = 0

    for (y in 0..<16) {
      for (z in 0..<16) {
        for (x in 0..<16) {
          val current = Vector3i(x, y, z)
          val index = positionToIndex(current)
          val type = data[index]
          if (type == BlockType.AIR.id) {
            continue
          }

          val offset = Vector3f(
            this.x * 16 + x.toFloat(),
            this.y * 16 + y.toFloat(),
            this.z * 16 + z.toFloat()
          )

          currentIndex = appendData(positions, uvs, indices, current, offset, currentIndex, BlockFace.TOP);
          currentIndex = appendData(positions, uvs, indices, current, offset, currentIndex, BlockFace.BOTTOM);
          currentIndex = appendData(positions, uvs, indices, current, offset, currentIndex, BlockFace.EAST);
          currentIndex = appendData(positions, uvs, indices, current, offset, currentIndex, BlockFace.WEST);
          currentIndex = appendData(positions, uvs, indices, current, offset, currentIndex, BlockFace.NORTH);
          currentIndex = appendData(positions, uvs, indices, current, offset, currentIndex, BlockFace.SOUTH);
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
    index: Vector3i,
    offset: Vector3f,
    currentIndex: Int,
    face: BlockFace
  ): Int {
    var drawFace = false
    val neighbour = Vector3i(index).add(face.dir)

    if (neighbour.x !in 0..<16) {
      // TODO
    } else if (neighbour.y !in 0..<16) {
      // TODO
    } else if (neighbour.z !in 0..<16) {
      // TODO
    } else {
      val neighbourIndex = positionToIndex(neighbour);
      drawFace = data[neighbourIndex] == BlockType.AIR.id
    }

    if (drawFace) {
      val (pos, uv, idx) = createFace(offset, currentIndex, face)
      positions.addAll(pos)
      uvs.addAll(uv)
      indices.addAll(idx)

      return currentIndex + 4
    }

    return currentIndex
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
        -0.5f + x, -0.5f + y, 0.5f + z, // bottom left
        -0.5f + x, 0.5f + y, 0.5f + z, // top left
        0.5f + x, 0.5f + y, 0.5f + z, // top right
      )
    }

    return Triple(verts, uvs, idxs)
  }
}
