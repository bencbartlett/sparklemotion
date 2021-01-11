package baaahs.plugin.webcam

import baaahs.document
import baaahs.window
import kotlinext.js.jsObject
import org.w3c.dom.HTMLVideoElement
import three.js.MeshBasicMaterial
import three.js.PlaneBufferGeometry
import three.js.VideoTexture

actual val DefaultVideoProvider: VideoProvider
    get() = BrowserWebCamVideoProvider

object BrowserWebCamVideoProvider : VideoProvider {
    private val videoElement = document.createElement("video").apply {
        setAttribute("style", "display:none")
        setAttribute("autoplay", "")
        setAttribute("playsinline", "")
        document.body!!.appendChild(this)
    } as HTMLVideoElement

    init {
        val texture = VideoTexture(videoElement);

        val geometry = PlaneBufferGeometry(16, 9);
        geometry.scale(0.5, 0.5, 0.5);
        val material = MeshBasicMaterial(jsObject { map = texture })

        window.navigator.mediaDevices.getUserMedia(jsObject {
            video = js(
                "({" +
                        "    width: { min: 1024, ideal: 1280, max: 1920 },\n" +
                        "    height: { min: 776, ideal: 720, max: 1080 }\n" +
                        "})"
            )
        }).then { stream ->
            // apply the stream to the video element used in the texture
            videoElement.srcObject = stream;
            videoElement.play();

        }.catch { error ->
            console.error("Unable to access the camera/webcam.", error);
        }
    }
}
