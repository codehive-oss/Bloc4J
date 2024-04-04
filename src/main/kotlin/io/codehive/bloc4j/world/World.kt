package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import io.codehive.bloc4j.game.Config
import org.joml.Vector3f
import org.joml.Vector3i
import java.util.*
import kotlin.math.floor

class World {

  private val chunkBakeQueue: PriorityQueue<Chunk> =
    PriorityQueue(Comparator.comparing(Chunk::distanceFromPlayer))
  private val chunks: HashMap<Vector3i, Chunk> = HashMap()

  private fun getChunkAt(coords: Vector3i): Chunk? {
    return chunks[coords]
  }

  fun getBlockAtSafe(pos: Vector3i): BlockType {
    val chunkX = floor(pos.x.toFloat() / 16).toInt()
    val chunkY = floor(pos.y.toFloat() / 16).toInt()
    val chunkZ = floor(pos.z.toFloat() / 16).toInt()
    val chunk = getChunkAt(Vector3i(chunkX, chunkY, chunkZ)) ?: return BlockType.AIR

    val localX = (pos.x % 16 + 16) % 16
    val localY = (pos.y % 16 + 16) % 16
    val localZ = (pos.z % 16 + 16) % 16

    return chunk.getBlockAt(
      localX,
      localY,
      localZ
    )
  }

  fun loadChunksAroundPoint(point: Vector3f) {
    val chunkX = floor(point.x / 16).toInt()
    val chunkY = floor(point.y / 16).toInt()
    val chunkZ = floor(point.z / 16).toInt()

    val toBeRendered: MutableSet<Chunk> = mutableSetOf()

    for (y in -Config.RENDER_DISTANCE..Config.RENDER_DISTANCE) {
      for (z in -Config.RENDER_DISTANCE..Config.RENDER_DISTANCE) {
        for (x in -Config.RENDER_DISTANCE..Config.RENDER_DISTANCE) {
          val coord = Vector3i(chunkX + x, chunkY + y, chunkZ + z)
          if (Vector3f(
              x.toFloat(),
              y.toFloat(),
              z.toFloat()
            ).lengthSquared() < Config.RENDER_DISTANCE * Config.RENDER_DISTANCE
          ) {
            val chunk = getChunkAt(coord) ?: Chunk(this, coord)
            toBeRendered.add(chunk)
          }
        }
      }
    }

    val toBeUnloaded = chunks.values.toSet().minus(toBeRendered)
    for (chunk in toBeUnloaded) {
      unloadChunk(chunk)
    }

    val toBeLoaded = toBeRendered.minus(chunks.values.toSet())
    for (chunk in toBeLoaded) {
      loadChunk(chunk)
    }
  }

  fun renderPendingChunks(gl: GL3, maxTimeMillis: Long) {
    val startTime = System.currentTimeMillis()
    while (!chunkBakeQueue.isEmpty() && System.currentTimeMillis() - startTime < maxTimeMillis) {
      val chunk = chunkBakeQueue.poll()
      if (!chunk.dirty) continue
      chunk.recalculate(gl)
    }
  }

  private fun loadChunk(chunk: Chunk) {
    chunks[chunk.coords] = chunk
    chunk.generate(BlockType.STONE)

    for (face in BlockFace.entries) {
      val neighbour = getChunkAt(Vector3i(face.dir).add(chunk.coords))
      if (neighbour != null) {
        neighbour.dirty = true
        chunkBakeQueue.add(neighbour)
      }
    }
    chunkBakeQueue.add(chunk)
  }


  private fun unloadChunk(chunk: Chunk) {
    chunks.remove(chunk.coords)
  }

  fun render() {
    for ((_, chunk) in chunks) {
      chunk.render()
    }
  }
}
