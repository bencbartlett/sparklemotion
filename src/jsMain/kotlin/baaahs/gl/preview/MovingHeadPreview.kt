package baaahs.gl.preview

import baaahs.device.MovingHeadDevice
import baaahs.dmx.Shenzarpy
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.NullTransport
import baaahs.fixtures.ProgramRenderPlan
import baaahs.fixtures.movingHeadFixture
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.ModelRenderEngine
import baaahs.gl.render.pickResultDeliveryStrategy
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.plugin.core.MovingHeadParams
import canvas.CanvasRenderingContext2D
import dom.html.HTMLCanvasElement
import dom.html.RenderingContextId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import web.timers.requestAnimationFrame
import kotlin.math.*


private val Float.short: String
    get() = if (isNaN()) toString() else try {
        ((this * 100).roundToInt() / 100.0).toString()
    } catch (e: Exception) {
        toString()
    }

class MovingHeadPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlContext,
    private var width: Int,
    private var height: Int,
    model: Model,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val fixtureType = MovingHeadDevice
    override val renderEngine = ModelRenderEngine(
        gl, fixtureType, resultDeliveryStrategy = gl.pickResultDeliveryStrategy()
    )
    private var movingHeadProgram: GlslProgram? = null
    private val renderTargets = model.allEntities
        .filterIsInstance<MovingHead>()
        .ifEmpty { listOf(MovingHead("Mover", baseDmxChannel = 1, adapter = Shenzarpy)) }
        .associateWith { movingHead ->
            val fixture = movingHeadFixture(movingHead, 1, movingHead.name, NullTransport, movingHead.adapter)
            renderEngine.addFixture(fixture)
        }
    private val context2d = canvas2d.getContext(RenderingContextId.canvas) as CanvasRenderingContext2D

    override fun start() {
        running = true
        render()
    }

    override fun stop() {
        running = false
    }

    override fun destroy() {
        stop()
        movingHeadProgram?.release()
        renderEngine.release()
    }

    override fun setProgram(program: GlslProgram?) {
        renderEngine.setRenderPlan(
            FixtureTypeRenderPlan(
                listOf(ProgramRenderPlan(program, renderTargets.values.toList()))
            )
        )
        movingHeadProgram = program
    }

    override fun render() {
        if (!running) return

        GlobalScope.launch { asyncRender() }
    }

    private suspend fun asyncRender() {
        if (movingHeadProgram != null) {
            preRenderCallback?.invoke()

            renderEngine.draw()
            renderEngine.finish()

            context2d.strokeStyle = "#ffffff"
            context2d.lineWidth = 2.0
            context2d.fillStyle = "#000000"
            context2d.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())

            context2d.fillStyle = "#ffffff"
            tabs("Fixture", "Pan", "Tilt", "Color Wheel", "Dimmer", 15.0)
            var y = 30.0

            renderTargets.forEach { (movingHead, renderTarget) ->
                val params = (renderTarget.fixtureResults as MovingHeadParams.ResultBuffer.FixtureResults)
                    .movingHeadParams

                tabs(
                    movingHead.name,
                    params.pan.short, params.tilt.short, params.colorWheel.short, params.dimmer.short,
                    y
                )
                y += 15.0
            }

            // get values of first renderTargets
            val firstParams = (renderTargets.values.first().fixtureResults as MovingHeadParams.ResultBuffer.FixtureResults)
                .movingHeadParams

            val centerX = context2d.canvas.width / 2
            val centerY = context2d.canvas.height / 2

            val initialX = centerX
            val initialY = centerY + ( context2d.canvas.height / 4)  * (-firstParams.tilt / 2.2)

            val targetX = centerX + (initialX-centerX)*cos(-firstParams.pan) - (initialY-centerY)*sin(-firstParams.pan);
            val targetY = centerY + (initialX-centerX)*sin(-firstParams.pan) + (initialY-centerY)*cos(-firstParams.pan);

            fun getRadians(degrees: Int): Double {
                return degrees * PI / 180;
            }

            val vectorX = targetX - centerX
            val vectorY = targetY - centerY
            val p1X = targetX + vectorX * cos(getRadians(9))
            val p1Y = targetY + vectorY * sin(getRadians(9))
            val p2X = targetX + vectorX * sin(getRadians(9))
            val p2Y = targetY + vectorY * cos(getRadians(9))
            val beamCenterX = (p1X + p2X) / 2
            val beamCenterY = (p1Y + p2Y) / 2

            context2d.beginPath();
            context2d.moveTo(centerX, centerY);
            context2d.lineTo(targetX, targetY);
            context2d.stroke();

            context2d.beginPath();
            context2d.moveTo(centerX, centerY);
            context2d.lineTo(p1X, p1Y);
            context2d.lineTo(p2X, p2Y);
            context2d.fillStyle = "rgba(255, 0, 2, 0.4)";
            context2d.fill();

            context2d.beginPath();
            context2d.ellipse(beamCenterX, beamCenterY, 30, 20, PI / 4, 0, 2 * PI);
            context2d.fillStyle = "rgba(255, 0, 2, 0.7)";
            context2d.fill();
            context2d.stroke();
        }
        requestAnimationFrame { render() }
    }

    private fun tabs(col0: String, col1: String, col2: String, col3: String, col4: String, y: Double) {
        context2d.fillText(col0, 3.0, y)
        context2d.fillText(col1, 50.0, y)
        context2d.fillText(col2, 100.0, y)
        context2d.fillText(col3, 150.0, y)
        context2d.fillText(col4, 200.0, y)
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
