package baaahs.mapper

import baaahs.app.ui.editor.betterSelect
import baaahs.ui.and
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_draggable.Draggable
import kotlinx.html.tabIndex
import materialui.icon
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import kotlin.math.max
import kotlin.math.min

private val MappingSessionView = xComponent<MappingSessionProps>("MappingSession") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    var selectedSurface by state<MappingSession.SurfaceData?> { null }
    var selectedPixelIndex by state<Int?> { null }
    val pixelData = selectedPixelIndex?.let { i ->
        val pixels = selectedSurface?.pixels
        if (pixels != null && i >= 0 && i < pixels.size) pixels[i] else null
    }

    val handleSelectSurface by handler(props.onSelectEntityPixel) { surface: MappingSession.SurfaceData? ->
        selectedSurface = surface
        selectedPixelIndex = null
        props.onSelectEntityPixel?.invoke(selectedSurface?.entityName, null)
        Unit
    }

    val handlePixelClick by mouseEventHandler(props.onSelectEntityPixel, pixelData) { e ->
        (e.target as? HTMLElement)?.let {
            val i = it.dataset["pixelIndex"]?.toIntOrNull()
            selectedPixelIndex = i
            props.onSelectEntityPixel?.invoke(selectedSurface?.entityName, i)
        }
    }

    val handleKeyDown by keyboardEventHandler(props.onSelectEntityPixel) { e ->
        when (e.key) {
            "ArrowLeft" -> {
                selectedPixelIndex?.let { i ->
                    val newIndex = max(i - 1, 0)
                    selectedPixelIndex = newIndex
                    props.onSelectEntityPixel?.invoke(selectedSurface?.entityName, newIndex)
                }
                e.stopPropagation()
                e.preventDefault()
            }
            "ArrowRight" -> {
                selectedPixelIndex?.let { i ->
                    val newIndex = min(i + 1, selectedSurface!!.myPixelCount - 1)
                    selectedPixelIndex = newIndex
                    props.onSelectEntityPixel?.invoke(selectedSurface?.entityName, newIndex)
                }
                e.stopPropagation()
                e.preventDefault()
            }
        }
    }

    Draggable {
        val styleForDragHandle = "MappingSessionDragHandle"
        attrs.handle = ".$styleForDragHandle"

        div(+styles.sessionInfo) {
            attrs.tabIndex = "-1" // So we can receive key events.
            attrs.onKeyDown = handleKeyDown

            div(+baaahs.app.ui.Styles.dragHandle and styleForDragHandle) {
                icon(mui.icons.material.DragIndicator)
            }

            header { +props.name }

            betterSelect<MappingSession.SurfaceData?> {
                attrs.values = listOf(null) + props.session.surfaces
                attrs.renderValueOption = { (it?.entityName ?: "-").asTextNode() }
                attrs.onChange = handleSelectSurface
            }

            selectedSurface?.let { surface ->
                ul {
                    li { +"Pixel Count: ${surface.pixelCount}" }
                    li { +"Pixels Attempted: ${surface.pixels?.count { it != null } ?: 0}" }
                    li { +"Pixels Mapped: ${surface.pixels?.filterNotNull()?.count { it.modelPosition != null } ?: 0}" }
                }

                div(+styles.pixels) {
                    val pixelCount = surface.myPixelCount
                    for (i in 0 until pixelCount) {
                        val pixel = surface.pixels?.get(i)
                        div(
                            +when {
                                pixel == null -> styles.skippedPixel
                                pixel.modelPosition == null -> styles.unmappedPixel
                                else -> styles.mappedPixel
                            } and if (i == selectedPixelIndex) styles.selectedPixel else null
                        ) {
                            attrs["data-pixel-index"] = i.toString()
                            attrs.onClick = handlePixelClick
                        }
                    }
                }

                div {
                    +"Selected Pixel: ${selectedPixelIndex ?: "None"}"
                    div {
                        if (pixelData != null) {
                            pixelData.modelPosition?.let { v ->
                                div { b { +"x: " }; +v.x.toString() }
                                div { b { +"y: " }; +v.y.toString() }
                                div { b { +"z: " }; +v.z.toString() }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val MappingSession.SurfaceData.myPixelCount get() = pixelCount ?: pixels?.size ?: 0

external interface MappingSessionProps : Props {
    var name: String
    var session: MappingSession
    var onSelectEntityPixel: ((entityName: String?, pixelIndex: Int?) -> Unit)?
}

fun RBuilder.mappingSession(handler: RHandler<MappingSessionProps>) =
    child(MappingSessionView, handler = handler)