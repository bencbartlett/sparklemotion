package baaahs.app.ui.layout

import baaahs.app.ui.StyleConstants
import baaahs.app.ui.controls.Styles
import baaahs.ui.asColor
import baaahs.ui.descendants
import baaahs.ui.selector
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.border
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import mui.material.styles.Theme
import styled.StyleSheet

class LayoutStyles(val theme: Theme) : StyleSheet("app-ui-layout", isStatic = true) {
    private val transitionTime = .2.s

    val gridOuterContainer by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
    }

    val gridBackground by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
        transition(::opacity, duration = Styles.editTransitionDuration, timing = Timing.linear)
    }

    val gridContainer by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
        pointerEvents = PointerEvents.none

        children {
            pointerEvents = PointerEvents.auto
        }

        descendants(".react-resizable-handle") {
            zIndex = StyleConstants.Layers.aboveSharedGlCanvas
        }
    }

    val gridCell by css {
        display = Display.grid
        border(1.px, BorderStyle.solid, theme.palette.text.primary.asColor().withAlpha(.5))
        transition(::border, )

//        outlineWidth = 3.px
//        put("outlineStyle", "dashed")
//        outlineColor = theme.palette.text.primary.asColor().withAlpha(.5)
//        outlineOffset = 6.px

        zIndex = 10
    }

    private val emptyCellFgColor = theme.palette.text.primary.asColor()
        .withAlpha(.5).blend(
            Color(theme.palette.background.default)
        )

    private val emptyCellBgColor = theme.palette.text.primary.asColor()
        .withAlpha(.1).blend(
            Color(theme.palette.background.default)
        )
    val emptyGridCell by css {
        position = Position.absolute
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
        color = emptyCellFgColor
        border(3.px, BorderStyle.solid, theme.palette.text.primary.asColor().withAlpha(.25))
        transition(::border, transitionTime)

//        outlineWidth = 3.px
//        put("outlineStyle", "dashed")
//        outlineColor = theme.palette.text.primary.asColor().withAlpha(.5)
//        outlineOffset = 6.px

    }

    val dropPlaceholder by css {
        outlineWidth = 3.px
        put("outlineStyle", "dashed")
        outlineColor = theme.palette.text.secondary.asColor().withAlpha(.5)
        outlineOffset = (-6).px
        put("-webkit-transition", "-webkit-transform 0.2s")
        transition(::transform, 0.2.s)
    }

    val addControl by css {
        border = "2px solid pink"
        backgroundColor = Color.yellow
    }

    val controlBox by css {
        display = Display.grid
        position = Position.relative
//        width = 100.pct
//        height = 100.pct
        marginRight = 0.em

        hover {
            child(Styles.editButton.selector) {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(Styles.dragHandle.selector) {
                opacity = 1
                filter = "drop-shadow(0px 0px 2px black)"
                cursor = Cursor.move
            }
        }

        transition(::transform, duration = Styles.editTransitionDuration, timing = Timing.linear)

        child(Styles.controlButton.selector) {
            display = Display.grid
//            width = 100.pct
//            height = 100.pct

            child("button") {
                position = Position.absolute
                top = 0.px
                left = 0.px
                right = 0.px
                bottom = 0.px
            }
        }
    }

    val editModeControl by css {
        transition(::opacity, duration = Styles.editTransitionDuration, timing = Timing.linear)
    }

    val editButton by css {
        position = Position.absolute
        right = 2.px
        bottom = (-2).px + 2.em
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas

        child("svg") {
            width = .75.em
            height = .75.em
        }
    }

    val editModeOff by css {
        descendants(this@LayoutStyles, ::gridBackground) {
            pointerEvents = PointerEvents.none
            opacity = 0
        }

        descendants(this@LayoutStyles, ::editModeControl) {
            opacity = 0
            pointerEvents = PointerEvents.none
        }

        descendants(this@LayoutStyles, ::emptyGridCell) {
            color = emptyCellFgColor.withAlpha(.5)
            border = "0px inset $emptyCellFgColor"
            transition(::border, transitionTime)

            hover {
                color = emptyCellFgColor
            }
        }
    }
    val editModeOn by css {
        descendants(this@LayoutStyles, ::editModeControl) {
            opacity = 1
        }

        descendants(this@LayoutStyles, ::emptyGridCell) {
            color = emptyCellBgColor
            border = "3px inset ${emptyCellFgColor.withAlpha(.5)}"
            transition(::border, transitionTime)

            hover {
                border = "3px inset ${emptyCellFgColor}"
            }
        }
    }


    val global = CssBuilder().apply {
        ".react-grid-placeholder" {
            +dropPlaceholder
        }

        ".react-resizable-handle" {
            position = Position.absolute
            right = (-12).px
            bottom = (-12).px
        }

        ".react-resizable-hide > .react-resizable-handle" {
            display = Display.none
        }
    }
}