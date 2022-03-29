package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.PixelArrayFixture
import baaahs.fixtures.PixelArrayRemoteConfig
import baaahs.fixtures.RemoteConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightRing
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.LightRingVisualizer
import baaahs.visualizer.VizPixels
import baaahs.visualizer.toVector3

actual class LightRingSimulation actual constructor(
    val lightRing: LightRing,
    private val simulationEnv: SimulationEnv,
    adapter: EntityAdapter
) : FixtureSimulation {
    // Assuming circumference is in inches, specify 1.5 LEDs per inch, or about 60 per meter.
    private val pixelCount = (lightRing.circumference * 1.5f).toInt()

    private val pixelLocations by lazy { lightRing.calculatePixelLocalLocations(pixelCount) }
    private val vizPixels by lazy {
        VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            pixelVisualizationNormal,
            lightRing.transformation,
            PixelArrayDevice.PixelFormat.default
        )
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            SacnManager.controllerTypeName,
            "wled-X${lightRing.name}X",
            lightRing.name,
            pixelLocations.size,
            pixelLocations.map { MappingSession.SurfaceData.PixelData(it) },
        )

    override val itemVisualizer: LightRingVisualizer
            by lazy { LightRingVisualizer(lightRing, vizPixels) }

    val wledSimulator by lazy {
        val wledsSimulator = simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(lightRing.name, vizPixels)
    }

    override val previewFixture: Fixture by lazy {
        PixelArrayFixture(
            lightRing,
            pixelLocations.size,
            lightRing.name,
            PixelArrayPreviewTransport(lightRing.name, vizPixels),
            PixelArrayDevice.PixelFormat.default,
            1f,
            pixelLocations
        )
    }

    override fun start() {
        wledSimulator.start()
    }

    override fun stop() {
        wledSimulator.stop()
    }

    override fun updateVisualizerWith(remoteConfig: RemoteConfig, pixelCount: Int, pixelLocations: Array<Vector3F>) {
        remoteConfig as PixelArrayRemoteConfig
        itemVisualizer.vizPixels = VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            LightBarSimulation.pixelVisualizationNormal,
            lightRing.transformation,
            remoteConfig.pixelFormat
        )
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        itemVisualizer.vizPixels?.readColors(reader)
    }

    companion object {
        val pixelVisualizationNormal = three_ext.vector3FacingForward
    }
}