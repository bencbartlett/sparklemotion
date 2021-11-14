package baaahs.models

import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.ObjModelLoader
import baaahs.util.Logger
import kotlin.math.PI

class SheepModel : Model() {
    override val name: String = "BAAAHS"
    private val pixelsPerPanel = run {
        getResource("baaahs-panel-info.txt")
            .split("\n")
            .map { it.split(Regex("\\s+")) }
            .associate { it[0] to it[1].toInt() * 60 }
    }

    private val objModel = ObjModelLoader.load("baaahs-model.obj") { name ->
        val expectedPixelCount = pixelsPerPanel[name]
        if (expectedPixelCount == null) {
            logger.debug { "No pixel count found for $name" }
        }
        expectedPixelCount
    }
    private val wallEyedness = (0.1f * PI / 2).toFloat()

    private val movingHeads: List<MovingHead> = arrayListOf(
        MovingHead(
            "leftEye",
            "Left Eye",
            1,
            Shenzarpy,
            origin = Vector3F(-11f, 202.361f, -24.5f),
            heading = Vector3F(0f, wallEyedness, (PI / 2).toFloat())
        ),
        MovingHead(
            "rightEye",
            "Right Eye",
            17,
            Shenzarpy,
            origin = Vector3F(-11f, 202.361f, 27.5f),
            heading = Vector3F(0f, -wallEyedness, (PI / 2).toFloat())
        )
    )

    override val allEntities: List<Entity>
        get() = objModel.allEntities + movingHeads
    override val geomVertices: List<Vector3F>
        get() = objModel.geomVertices

    companion object {
        private val logger = Logger<SheepModel>()

        fun Panel(name: String) =
            Surface(name, name, null, emptyList(), emptyList())
    }
}