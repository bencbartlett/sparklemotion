import org.w3c.dom.events.Event
import org.w3c.files.File
import react.*

@JsModule("react-dropzone")
@JsName("default")
external val dropzoneImport: dynamic

var dropzone: ElementType<DropzoneProps> = dropzoneImport.default

external interface DropzoneProps : PropsWithChildren {
    var onDrop: (Array<File>) -> Unit
    var onFileDialogCancel: (Event) -> Unit
    var className: String? get() = definedExternally; set(value) = definedExternally
}

external interface CallbackProps {
    //    val acceptedFiles: []
//    val fileRejections: []
    val getInputProps: () -> dynamic
    val getRootProps: () -> dynamic
    //    val inputRef: {current: null}
    val isDragAccept: Boolean
    val isDragActive: Boolean
    val isDragReject: Boolean
    val isFileDialogActive: Boolean
    val isFocused: Boolean
//    val open: ƒ ()
//    val rootRef: {current: nul}
}

fun RElementBuilder<DropzoneProps>.renderDropzone(block: RBuilder.(rootProps: dynamic, inputProps: dynamic) -> Unit) {
    val callback = { callbackProps: CallbackProps ->
        buildElements(RBuilder()) {
            block(callbackProps.getRootProps(), callbackProps.getInputProps())
        }
    }.asDynamic()
    child(callback)
}