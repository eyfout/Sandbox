package ht.eyfout.sandbox

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import org.junit.Assert
import org.junit.Test

class TestConstraint {

    @Test
    fun `serialize constraint provider`() {
        with(Json(JsonConfiguration.Stable)) {
            println(stringify(MinLength(11)))
        }
    }

    @Test
    fun `serialize constraint model`() {
        with(Json(JsonConfiguration.Stable)) {

            val model = stringify(constraintModel {
                definition("JohnDoe") {
                    scalar("pyID", MinLength(3), MaxLength(22))
                }
            })
            println(model)
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

