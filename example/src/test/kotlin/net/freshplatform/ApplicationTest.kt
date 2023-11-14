package net.freshplatform

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import net.freshplatform.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
//        application {
//            configureRouting()
//        }
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World!", bodyAsText())
//        }
    }
}
