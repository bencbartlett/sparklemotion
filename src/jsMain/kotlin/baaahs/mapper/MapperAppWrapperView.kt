package baaahs.mapper

import baaahs.app.ui.AllStyles
import baaahs.client.SceneEditorClient
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.styles.Theme
import mui.material.styles.useTheme
import react.Props
import react.RBuilder
import react.RHandler

private val MapperAppWrapperView = xComponent<MapperAppWrapperProps>("MapperAppWrapper") { props ->
    val theme = useTheme<Theme>()

    val myAppContext = memo(theme) {
        jso<MapperAppContext> {
            this.sceneEditorClient = props.sceneEditorClient
            this.plugins = plugins
            this.allStyles = AllStyles(theme)
            this.clock = clock
        }
    }

    mapperAppContext.Provider {
        attrs.value = myAppContext

        mapperApp {
            attrs.mapperUi = props.mapperUi
        }
    }
}

external interface MapperAppWrapperProps : Props {
    var sceneEditorClient: SceneEditorClient.Facade
    var mapperUi: JsMapperUi
}

fun RBuilder.mapperAppWrapper(handler: RHandler<MapperAppWrapperProps>) =
    child(MapperAppWrapperView, handler = handler)