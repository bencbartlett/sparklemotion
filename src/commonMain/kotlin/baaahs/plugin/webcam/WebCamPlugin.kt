package baaahs.plugin.webcam

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.data.SingleUniformFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import kotlinx.serialization.SerialName

class WebCamPlugin(private val videoProvider: VideoProvider) : Plugin {
    override val packageName: String = BeatLinkPlugin.id
    override val title: String = "Web Cam"

    // We'll just make one up-front. We only ever want one (because equality
    // is using object identity), and there's no overhead.
    internal val webCamDataSource = WebCamDataSource()

    override val dataSourceBuilders: List<DataSourceBuilder<out DataSource>>
        get() = listOf(
            object : DataSourceBuilder<DataSource> {
                override val resourceName: String get() = "WebCam"
                override val contentType: ContentType get() = ContentType.Color
                override val serializerRegistrar: SerializerRegistrar<DataSource>
                    get() = objectSerializer("baaahs.WebCam:WebCam", webCamDataSource)

                override fun build(inputPort: InputPort): DataSource = webCamDataSource
            }
        )

    @SerialName("baaahs.WebCam:WebCam")
    inner class WebCamDataSource internal constructor() : DataSource {
        override val pluginPackage: String get() = id
        override val title: String get() = "Web Cam"
        override fun getType(): GlslType = GlslType.Sampler2D
        override val contentType: ContentType get() = ContentType.Color

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
            return object : Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed =
                        SingleUniformFeed(glslProgram, this@WebCamDataSource, id) { uniform ->
                            uniform.set(beatSource.getBeatData().fractionTillNextBeat(clock))
                        }
                }

                override fun release() {
                    super.release()
                }
            }
        }
    }

    companion object {
        val id = "baaahs.WebCam"
    }

    class Builder : PluginBuilder {
        override val id: String = WebCamPlugin.id

        override fun build(pluginContext: PluginContext): Plugin {
            return WebCamPlugin(DefaultVideoProvider)
        }
    }
}