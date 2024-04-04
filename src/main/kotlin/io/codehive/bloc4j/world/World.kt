package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import io.codehive.bloc4j.game.Config
import org.joml.Vector3f
import org.joml.Vector3i
import java.util.*
import kotlin.math.floor

class World {

  val mengerData: Array<Array<Array<Boolean>>> = Array(9) { Array(9) { Array(9) { true } } }

  init {
    menger()
  }

  private fun menger() {
    val cubeLength = mengerData[0].size
    val subCubeLength = cubeLength / 3
    for (x in 0 until cubeLength) {
      for (y in 0 until cubeLength) {
        for (z in 0 until cubeLength) {
          if (x in subCubeLength..<subCubeLength * 2 && y in subCubeLength..<subCubeLength * 2
            || x in subCubeLength..<subCubeLength * 2 && z in subCubeLength..<subCubeLength * 2
            || y in subCubeLength..<subCubeLength * 2 && z in subCubeLength..<subCubeLength * 2
          ) {
            mengerData[x][y][z] = false
          }
        }
      }
    }
  }


  private val chunkBakeQueue: PriorityQueue<Chunk> =
    PriorityQueue(Comparator.comparing(Chunk::distanceFromPlayer))
  private val chunks: HashMap<Vector3i, Chunk> = HashMap()

  private fun getChunkAt(coords: Vector3i): Chunk? {
    return chunks[coords]
  }

  fun getBlockAtSafe(pos: Vector3i): BlockType {
    val chunkX = floor(pos.x.toFloat() / Config.CHUNK_SIZE).toInt()
    val chunkY = floor(pos.y.toFloat() / Config.CHUNK_SIZE).toInt()
    val chunkZ = floor(pos.z.toFloat() / Config.CHUNK_SIZE).toInt()
    val chunk = getChunkAt(Vector3i(chunkX, chunkY, chunkZ)) ?: return BlockType.AIR

    val localX = (pos.x % Config.CHUNK_SIZE + Config.CHUNK_SIZE) % Config.CHUNK_SIZE
    val localY = (pos.y % Config.CHUNK_SIZE + Config.CHUNK_SIZE) % Config.CHUNK_SIZE
    val localZ = (pos.z % Config.CHUNK_SIZE + Config.CHUNK_SIZE) % Config.CHUNK_SIZE

    return chunk.getBlockAt(
      localX,
      localY,
      localZ
    )
  }

  fun loadChunksAroundPoint(point: Vector3f) {
    val chunkX = floor(point.x / Config.CHUNK_SIZE).toInt()
    val chunkY = floor(point.y / Config.CHUNK_SIZE).toInt()
    val chunkZ = floor(point.z / Config.CHUNK_SIZE).toInt()

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
    chunk.generate()

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
