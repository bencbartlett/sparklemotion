package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.mapper.MappingSession
import baaahs.model.MovingHead
import baaahs.util.Clock
import baaahs.visualizer.movers.MovingHeadVisualizer

actual class MovingHeadSimulation actual constructor(
    private val movingHead: MovingHead,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    private val dmxUniverse = simulationEnv[FakeDmxUniverse::class]

    override val mappingData: MappingSession.SurfaceData?
        get() = null

    override val entityVisualizer: MovingHeadVisualizer by lazy {
        val clock = simulationEnv[Clock::class]
        MovingHeadVisualizer(movingHead, clock, dmxUniverse)
    }

    private val buffer = dmxUniverse.writer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)

    override val previewFixture: Fixture by lazy {
        Fixture(
            movingHead,
            1,
            listOf(movingHead.origin),
            movingHead.deviceType.defaultConfig,
            movingHead.name,
            PreviewTransport()
        )
    }

    override fun launch() {
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        val channelCount = reader.readShort().toInt()
        repeat(channelCount) { i ->
            buffer[i] = reader.readByte()
        }

        entityVisualizer.receivedDmxFrame()
    }

    inner class PreviewTransport : Transport {
        private val movingHeadBuffer = dmxUniverse.writer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)

        override val name: String
            get() = movingHead.name

        override fun deliverBytes(byteArray: ByteArray) {
            for (i in byteArray.indices) {
                movingHeadBuffer[i] = byteArray[i]
            }
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            val buf = ByteArrayWriter()
            for (componentIndex in 0 until componentCount) {
                buf.offset = 0
                fn(componentIndex, buf)

                val bytes = buf.toBytes()
                for (i in 0 until bytesPerComponent) {
                    movingHeadBuffer[i] = bytes[i]
                }
            }
        }
    }
}