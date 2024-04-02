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

//  init {
//    for (x in -2..1) {
//      for (y in -2..1) {
//        for (z in -2..1) {
//          val coords = Vector3i(x, y, z)
//          val chunk = Chunk(this, coords)
////          chunk.fill(BlockType.DIRT)
//          chunk.generate(BlockType.STONE)
//          chunks[coords] = chunk
//        }
//      }
//    }
//  }

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

  fun buildChunks(gl: GL3) {
    for ((_, chunk) in chunks) {
      chunk.recalculate(gl)
    }
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

  fun renderPendingChunks(gl: GL3) {
    var chunk = chunkBakeQueue.poll() ?: return
    while (!chunk.dirty && chunkBakeQueue.isEmpty()) {
      chunk = chunkBakeQueue.poll()
    }
    chunk.recalculate(gl)
  }

  private fun loadChunk(chunk: Chunk) {
    chunks[chunk.coords] = chunk
    chunkBakeQueue.add(chunk)
    chunk.generate(BlockType.STONE)
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
