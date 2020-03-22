package ht.eyfout.sandbox

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.stringify
import org.junit.Assert
import org.junit.Test

class TestConstraintModel {

    @Test
    fun `serialize constraint provider`() {
        with(Json(JsonConfiguration.Stable, SerializersModule {
            polymorphic(ConstraintProvider::class) {
                MinLength::class with MinLength.serializer()
            }
        })) {
            println(stringify(MinLength(11)))
        }
    }

    @Test
    fun `serialize constraint model`() {
        with(constraintModel {
            definition("Work-") {
                scalar("pyID", MinLength(3), MaxLength(22), ConstraintProvider.NOOP)
            }
        }.classes.get("Work-")!!.get("pyID")!!) {
            assert(get(0) is MinLength)
            assert(get(1) is MaxLength)
        }
    }


    @Test
    fun `constraint model created without exception`() {
        val model = constraintModel {
            definition("Work-") {
                scalar("pyID", MinLength(3), MaxLength(22))
            }
            definition("Data-") {
                scalar("pzInsKey", MinLength(45), MaxLength(99))
            }
        }
        Assert.assertNotNull(model)
    }

    @Test
    fun `verify max length`() {
        val constraint = arrayOf(MinLength(3), MaxLength(6)).reduce { a, b -> a + b }
        var actual = ""
        constraint.get()("Jupiter") { actual = it }
        Assert.assertEquals("Max", "Max length", actual)
    }

    @Test
    fun `verify min length`() {
        val constraint = arrayOf(MinLength(3), MaxLength(6)).reduce { a, b -> a + b }

        var actual = ""
        constraint.get()("it") { actual = it }
        Assert.assertEquals("Min", "Min length", actual)
    }


    @Test
    fun `verify email address`() {
        val constraint = arrayOf(MinLength(3), MaxLength(6), EmailAddress).reduce { a, b -> a + b }
        var actual = ""
        constraint.get()("Junit") { actual = it }
        Assert.assertEquals("Email address", "Not an email address", actual)
    }
}

