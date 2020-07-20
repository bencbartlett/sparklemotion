package baaahs

import baaahs.OpenShow.OpenScene.OpenPatchSet
import baaahs.dmx.Shenzarpy
import baaahs.glsl.GlslRenderer
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.show.Show

class ShowRunner(
    private val model: Model<*>,
    private var show: Show,
    private var showState: ShowState,
    private val showManager: ShowManager,
    private val beatSource: BeatSource,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    internal val clock: Clock,
    private val glslRenderer: GlslRenderer,
    pubSub: PubSub.Server,
    private val surfaceManager: SurfaceManager
) {
    private var openShow: OpenShow = OpenShow(show, showManager)
    private var nextPatchSet: OpenPatchSet? = showState.findPatchSet(openShow)

    private val showStateChannel = pubSub.publish(Topics.showState, showState) { showState ->
        this.showState = showState
        nextPatchSet = showState.findPatchSet(openShow)
    }

    private var currentPatchSet: OpenPatchSet? = null
    private var currentRenderPlan: RenderPlan? = null

    private var requestedGadgets: LinkedHashMap<String, Gadget> = linkedMapOf()

    // TODO: Get beat sync working again.
    // Continuous from [0.0 ... 3.0] (0 is first beat in a measure, 3 is last)
    val currentBeat: Float
        get() = beatSource.getBeatData().beatWithinMeasure(clock)

    private fun getDmxBuffer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
        dmxUniverse.writer(baseChannel, channelCount)

    // TODO: Get moving heads working again.
    fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
        val baseChannel = Config.DMX_DEVICES[movingHead.name] ?: error("no DMX device for ${movingHead.name}")
        val movingHeadBuffer = Shenzarpy(getDmxBuffer(baseChannel, 16))

        movingHeadManager.listen(movingHead) { updated ->
            println("Moving head ${movingHead.name} moved to ${updated.x} ${updated.y}")
            movingHeadBuffer.pan = updated.x / 255f
            movingHeadBuffer.tilt = updated.y / 255f
        }

        return movingHeadBuffer
    }

    fun nextFrame(dontProcrastinate: Boolean = true) {
        // Unless otherwise instructed, = generate and send the next frame right away,
        // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
        if (dontProcrastinate) housekeeping()

        currentRenderPlan?.let {
            it.render(glslRenderer)
            sendFrame()
        }

        if (!dontProcrastinate) housekeeping()
    }

    private fun housekeeping() {
        var remapToSurfaces = surfaceManager.housekeeping()

        // Maybe switch to a new show.
        nextPatchSet?.let { startingPatchSet ->
            switchTo(startingPatchSet)

            currentPatchSet = nextPatchSet
            nextPatchSet = null
            remapToSurfaces = true
        }

        if (remapToSurfaces) {
            surfaceManager.clearRenderPlans()

            currentRenderPlan?.let {
                surfaceManager.applyRenderPlan(it)
            }
        }
    }

    fun switchTo(newShow: Show) {
        openShow = showManager.swapAndRelease(openShow, newShow)
        show = newShow

        nextPatchSet = showState.findPatchSet(openShow)
    }

    private fun switchTo(newPatchSet: OpenPatchSet) {
        currentRenderPlan = prepare(newPatchSet)

        logger.info {
            "New show ${newPatchSet.title} created; " +
                    "${surfaceManager.getSurfaceCount()} surfaces " +
                    "and ${requestedGadgets.size} gadgets"
        }

//        TODO gadgetManager.sync(requestedGadgets.toList(), gadgetsState)
        requestedGadgets.clear()
    }

    private fun prepare(newPatchSet: OpenPatchSet): RenderPlan {
        val glslContext = glslRenderer.gl
        return newPatchSet.createRenderPlan(glslContext)
    }

    fun sendFrame() {
        surfaceManager.sendFrame()
        dmxUniverse.sendFrame()
    }

    fun shutDown() {
        // TODO gadgetManager.clear()
    }

    data class SurfacesChanges(val added: Collection<SurfaceReceiver>, val removed: Collection<SurfaceReceiver>)

    interface SurfaceReceiver {
        val surface: Surface
        fun send(pixels: Pixels)
    }

    companion object {
        val logger = Logger("ShowRunner")
    }
}