package ht.eyfout.sandbox

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable


data class ClassDefinition(val name: String, val properties: List<PropertyDefinition>)
data class PropertyDefinition(val classDefinition: ClassDefinition, val type: HType)

interface HType

@Polymorphic
@Serializable
open class ConstraintProvider<T>(@Transient val func: (T?, (String) -> Unit) -> Unit) {
    companion object {
        val NOOP = ConstraintProvider<Any?> { _, _ -> }
    }

    fun get() = func

    //TODO: Cache previously calculated constraint joins for reuse
    operator fun plus(other: ConstraintProvider<T>) = ConstraintProvider { a: T?, b: (String) -> Unit ->
        this.func(a, b)
        other.func(a, b)
    }
}

@Serializable
class MinLength(val length: Int) : ConstraintProvider<String?>({ theValue, errMessageAppender ->
    if (theValue?.length ?: 0 < length) {
        errMessageAppender("Min length")
    }
})

@Serializable
data class MaxLength(val length: Int) : ConstraintProvider<String?>({ theValue, errMessageAppender ->
    if (theValue?.length ?: 0 > length) {
        errMessageAppender("Max length")
    }
})

object EmailAddress : ConstraintProvider<String?>({ theValue, errMessageAppender ->
    if (theValue?.contains("@") == false) {
        errMessageAppender("Not an email address")
    }
})


class ConstraintModel(@Transient val lambda: ConstraintModelBuilder.() -> Unit) {
    val classes: Map<String, Map<String, Array<out ConstraintProvider<*>>>>

    init {
        classes = ConstraintModelBuilder().apply(lambda).build()
    }

    class ConstraintBuilder {
        private val declaredConstraints = mutableMapOf<String, Array<out ConstraintProvider<*>>>()
        fun scalar(name: String, vararg constraints: ConstraintProvider<*>) {
            this.declaredConstraints[name] = constraints
        }

        fun build() = declaredConstraints
    }

    class ConstraintModelBuilder {
        private val classes = mutableMapOf<String, Map<String, Array<out ConstraintProvider<*>>>>()
        fun definition(name: String, constraint: ConstraintBuilder.() -> Unit) {
            classes[name] = ConstraintBuilder().apply(constraint).build()
        }

        fun build() = classes
    }

}

fun constraintModel(constraintModel: ConstraintModel.ConstraintModelBuilder.() -> Unit): ConstraintModel {
    return ConstraintModel(constraintModel)
}

