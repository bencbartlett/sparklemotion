package baaahs.app.ui.editor

import baaahs.control.ButtonGroupControl
import baaahs.control.MutableButtonGroupControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.inputlabel.inputLabel
import materialui.components.radio.radio
import materialui.components.radiogroup.radioGroup
import org.w3c.dom.HTMLInputElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val ButtonGroupPropsEditorView =
    xComponent<ButtonGroupPropsEditorProps>("ButtonGroupPropsEditor") { props ->
        div(+EditableStyles.propertiesSection) {
            textFieldEditor {
                attrs.label = "Title"
                attrs.helperText = "Not actually displayed anywhere"
                attrs.autoFocus = true
                attrs.getValue = { props.mutableButtonGroupControl.title }
                attrs.setValue = { value -> props.mutableButtonGroupControl.title = value }
                attrs.editableManager = props.editableManager
            }
        }

        div(+EditableStyles.propertiesSection) {
            formControl {
                inputLabel {
                    attrs.component = "legend"
                    +"Direction"
                }

                radioGroup {
                    attrs.value(props.mutableButtonGroupControl.direction.name)
                    attrs.onChangeFunction = {
                        val value = (it.target as HTMLInputElement).value
                        props.mutableButtonGroupControl.direction = ButtonGroupControl.Direction.valueOf(value)
                        props.editableManager.onChange()
                    }

                    ButtonGroupControl.Direction.values().forEach {
                        formControlLabel {
                            attrs.value = it.name
                            attrs.control { radio {} }
                            attrs.label { +it.name }
                        }
                    }
                }
            }
        }
    }

external interface ButtonGroupPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableButtonGroupControl: MutableButtonGroupControl
}

fun RBuilder.buttonGroupPropsEditor(handler: RHandler<ButtonGroupPropsEditorProps>) =
    child(ButtonGroupPropsEditorView, handler = handler)