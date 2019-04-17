package baaahs

actual class Visualizer actual constructor(sheepModel: SheepModel, dmxUniverse: FakeDmxUniverse) {
    actual fun start() {
    }

    actual fun showPanel(panel: SheepModel.Panel): JsPanel {
        TODO("JVM Visualizer.showPanel not implemented")
    }

    actual fun addEye(eye: SheepModel.MovingHead) {
    }

    actual val mediaDevices: MediaDevices
        get() = TODO("JVM Visualizer.mediaDevices not implemented")
    actual var onStartMapper: () -> Unit
        get() = TODO("JVM Visualizer.onStartMapper not implemented")
        set(value) {}

    actual fun setMapperRunning(b: Boolean) {
    }

}

actual class JsPixels actual constructor(jsPanel: JsPanel) : Pixels {
    override fun set(colors: Array<Color>) {
        TODO("JVM Visualizer.set not implemented")
    }

    override val count: Int
        get() = TODO("JVM Visualizer.count not implemented")
}

actual class JsPanel