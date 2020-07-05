package baaahs.ui

import baaahs.Logger
import org.w3c.dom.events.Event
import react.RMutableRef
import react.useMemo
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Get functional component from [func]
 */
fun <P : react.RProps> xComponent(
    name: String,
    func: XBuilder.(props: P) -> Unit
): react.FunctionalComponent<P> {
    val component = { props: P ->
        react.buildElements {
            val xBuilder = XBuilder()
            xBuilder.func(props)
            this.childList.addAll(xBuilder.childList)
            xBuilder.renderFinished()
        }
    }
    component.asDynamic().displayName = name
    return component
}

class XBuilder : react.RBuilder() {
    private var firstTime = false
    private var dataIndex = 0
    private var sideEffectIndex = 0

    private val context = react.useMemo({
        firstTime = true
        Context()
    }, emptyArray())

    private val counter = react.useState { 0 }
    private var internalCounter = 0

    init {
        react.useEffectWithCleanup(emptyList()) {
            return@useEffectWithCleanup {
                context.sideEffects.forEach { it.runCleanups() }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> ref(): RMutableRef<T> = react.useRef(null as T)

    fun <T> memo(vararg watch: Any?, block: () -> T): T {
        return useMemo(block, watch)
    }

    fun <T> state(valueInitializer: () -> T): ReadWriteProperty<Any?, T> {
        @Suppress("UNREACHABLE_CODE")
        return if (firstTime) {
            val data = Data(valueInitializer()) {
                counter.second(++internalCounter)
            }
            context.data.add(data)
            return data
        } else {
            @Suppress("UNCHECKED_CAST")
            return context.data[dataIndex++] as Data<T>
        }
    }

    fun observe(item: Observable) {
        sideEffect("observe", item) {
            val observer = object : Observer {
                override fun notifyChanged() = forceRender()
            }
            item.addObserver(observer)
            withCleanup {
                item.removeObserver(observer)
            }
        }
    }

    fun sideEffect(name: String, vararg watch: Any?, callback: SideEffect.() -> Unit) {
        return if (firstTime) {
            val sideEffect = SideEffect(watch)
            context.sideEffects.add(sideEffect)
            sideEffect.callback()
        } else {
            val sideEffect = context.sideEffects[sideEffectIndex++]
            sideEffect.firstTime = false
            if (areSame(watch, sideEffect.lastWatchValues)) {
                logger.debug {
                    "Not running side effect $name " +
                            "(${watch.truncateStrings(12)} == ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
            } else {
                logger.debug {
                    "Running side effect $name " +
                            "(${watch.truncateStrings(12)} != ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
                sideEffect.runCleanups()
                sideEffect.lastWatchValues = watch
                sideEffect.callback()
            }
        }
    }

    fun <T : Function<*>> handler(name: String, vararg watch: Any?, block: T): T {
        val handler = context.handlers.getOrPut(name) { Handler(watch, block) }
        return if (areSame(watch, handler.lastWatchValues)) {
            handler.block.unsafeCast<T>()
        } else {
            handler.block = block
            block
        }
    }

    fun eventHandler(name: String, vararg watch: Any?, block: (Event) -> Unit): (Event) -> Unit {
        return handler(name, watch = *watch, block = block)
    }

    fun eventHandler(block: Function<*>): (Event) -> Unit {
        @Suppress("UNCHECKED_CAST")
        return handler("event handler", block, block = block as (Event) -> Unit)
    }

    fun forceRender() {
        counter.second(++internalCounter)
    }

    internal fun renderFinished() {
        firstTime = false
    }

    private fun areSame(currentWatchValues: Array<out Any?>, priorWatchValues: Array<out Any?>): Boolean {
        return currentWatchValues.zip(priorWatchValues).all { (a, b) ->
            if (a is String && b is String) {
                a == b
            } else if (a is Number && b is Number) {
                a == b
            } else {
                a === b
            }
        }
    }

    private class Context {
        val data: MutableList<Data<*>> = mutableListOf()
        val sideEffects: MutableList<SideEffect> = mutableListOf()
        val handlers: MutableMap<String, Handler> = mutableMapOf()
    }

    private class Handler(
        internal var lastWatchValues: Array<out Any?>,
        var block: Function<*>
    )

    private class Data<T>(initialValue: T, private val onChange: () -> Unit): ReadWriteProperty<Any?, T> {
        var value: T = initialValue

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            logger.debug { "${property.name} := ${value?.toString()?.truncate(12)}" }
            if (this.value != value) {
                this.value = value
                onChange()
            }
        }
    }

    class SideEffect(
        internal var lastWatchValues: Array<out Any?>
    ) {
        internal var firstTime: Boolean = true
        internal var cleanups: MutableList<() -> Unit>? = null

        fun withCleanup(cleanup: () -> Unit) {
            if (firstTime) {
                if (cleanups == null) cleanups = mutableListOf()
                cleanups!!.add(cleanup)
            }
        }

        internal fun runCleanups() {
            cleanups?.forEach { it.invoke() }
            cleanups?.clear()
            firstTime = true // re-collect cleanups
        }
    }

    companion object {
        private val logger = Logger("Preact")
    }
}
