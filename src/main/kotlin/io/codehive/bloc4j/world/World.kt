package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import org.joml.Vector3i
import kotlin.math.floor

class World {
  private val chunks: ArrayList<Chunk> = ArrayList()

  init {
    for (x in -2..1) {
      for (y in -2..1) {
        for (z in -2..1) {
          val chunk = Chunk(this, x, y, z)
          chunk.fill(BlockType.DIRT)
          chunks.add(chunk)
        }
      }
    }
  }

  private fun getChunkAt(x: Int, y: Int, z: Int): Chunk? {
    return chunks.find { chunk -> chunk.x == x && chunk.y == y && chunk.z == z }
  }

  fun getBlockAtSafe(pos: Vector3i): BlockType {
    val chunkX = floor(pos.x.toFloat() / 16).toInt()
    val chunkY = floor(pos.y.toFloat() / 16).toInt()
    val chunkZ = floor(pos.z.toFloat() / 16).toInt()
    val chunk = getChunkAt(chunkX, chunkY, chunkZ) ?: return BlockType.AIR

    val localX = (pos.x % 16 + 16) % 16
    val localY = (pos.y % 16 + 16) % 16
    val localZ = (pos.z % 16 + 16) % 16

    return chunk.getBlockAt(
      localX,
      localY,
      localZ
    )
  }

  fun buildChunks(gl: GL3) {
    for (chunk in chunks) {
      chunk.recalculate(gl)
    }
  }

  fun render() {
    for (chunk in chunks) {
      chunk.render()
    }
  }

}
