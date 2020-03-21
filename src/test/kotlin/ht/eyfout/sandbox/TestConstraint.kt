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
        val constraints = arrayOf(MinLength(3), MaxLength(6))
            .map { it.get() }
        val lambda = composeConstraints(constraints)
        var actual = ""
        lambda("Jupiter") { actual = it }
        Assert.assertEquals("Max", "Max length", actual)
    }

    @Test
    fun `verify min length`() {
        val constraints = arrayOf(MinLength(3), MaxLength(6))
            .map { it.get() }

        val lambda = composeConstraints(constraints)
        var actual = ""
        lambda("it") { actual = it }
        Assert.assertEquals("Min", "Min length", actual)
    }


    @Test
    fun `verify email address`() {
        val constraints = arrayOf(MinLength(3), MaxLength(6), EmailAddress)
            .map { it.get() }

        var actual = ""
        composeConstraints(constraints)("Junit") { actual = it }
        Assert.assertEquals("Email address", "Not an email address", actual)
    }

    private fun <T> composeConstraints(constraints: List<(T?, (String) -> Unit) -> Unit>) =
        { theValue: T?, collector: (String) -> Unit ->
            constraints.forEach {
                it(theValue, collector)
            }
        }

}

