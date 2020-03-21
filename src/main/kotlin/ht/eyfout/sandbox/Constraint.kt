package ht.eyfout.sandbox

import kotlinx.serialization.Serializable


data class ClassDefinition(val name: String, val properties: List<PropertyDefinition>)
data class PropertyDefinition(val classDefinition: ClassDefinition, val type: HType)

interface HType

@Serializable
abstract class ConstraintProvider<T>(@Transient val func: (T?, (String) -> Unit) -> Unit) {
    fun get() = func
}

@Serializable
class MinLength(val length: Int) : ConstraintProvider<String?>({ theValue, errMessageAppender ->
    if (theValue?.length ?: 0 < length) {
        errMessageAppender("Min length")
    }
})

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


//@Serializable
class Constraint(@Transient val lambda: ClassBuilder.() -> Unit) {
    val classes: Map<String, Map<String, Array<out ConstraintProvider<*>>>>

    init {
        classes = ClassBuilder().apply(lambda).build()
    }

    class ConstraintBuilder {
        private val declaredConstraints = mutableMapOf<String, Array<out ConstraintProvider<*>>>()
        fun scalar(name: String, vararg constraints: ConstraintProvider<*>) {
            this.declaredConstraints[name] = constraints
        }

        fun build() = declaredConstraints
    }

    //    @Serializable
    class ClassBuilder {
        private val classes = mutableMapOf<String, Map<String, Array<out ConstraintProvider<*>>>>()
        fun definition(name: String, constraint: ConstraintBuilder.() -> Unit) {
            classes[name] = ConstraintBuilder().apply(constraint).build()
        }

        fun build() = classes
    }

}

fun constraintModel(constraint: Constraint.ClassBuilder.() -> Unit): Constraint {
    return Constraint(constraint)
}

