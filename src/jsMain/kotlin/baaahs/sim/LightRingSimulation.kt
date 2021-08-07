package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.fixtures.Fixture
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightRing
import baaahs.visualizer.LightRingVisualizer
import baaahs.visualizer.VizPixels
import baaahs.visualizer.toVector3
import three.js.Vector3

actual class LightRingSimulation actual constructor(
    val lightRing: LightRing,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    // Assuming circumference is in inches, specify 1.5 LEDs per inch, or about 60 per meter.
    private val pixelCount = (lightRing.circumference * 1.5f).toInt()

    private val pixelLocations by lazy { lightRing.calculatePixelLocations(pixelCount) }
    private val vizPixels by lazy {
        VizPixels(pixelLocations.map { it.toVector3() }.toTypedArray(), pixelVisualizationNormal)
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            SacnManager.controllerTypeName,
            "wled-X${lightRing.name}X",
            lightRing.name,
            pixelLocations.size,
            pixelLocations.map { MappingSession.SurfaceData.PixelData(it) },
        )

    override val entityVisualizer: LightRingVisualizer by lazy { LightRingVisualizer(lightRing, vizPixels) }

    val wledSimulator by lazy {
        val wledsSimulator = simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(lightRing.name, vizPixels)
    }

    override val previewFixture: Fixture by lazy {
        Fixture(
            lightRing,
            pixelCount,
            pixelLocations,
            lightRing.deviceType.defaultConfig,
            lightRing.name,
            PixelArrayPreviewTransport(lightRing.name, vizPixels)
        )
    }

    override fun launch() {
        wledSimulator.run()
    }

    override fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader) {
        val pixelCount = reader.readInt()
        val pixelLocations = (0 until pixelCount).map {
            Vector3F.parse(reader).toVector3()
        }.toTypedArray()

        entityVisualizer.vizPixels = VizPixels(pixelLocations, pixelVisualizationNormal)
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        entityVisualizer.vizPixels?.readColors(reader)
    }

    companion object {
        val pixelVisualizationNormal = Vector3(0, 0, 1)
    }
}