package org.example

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SharesBaseTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
                Assertions.assertEquals("[ ]", response.content)
            }
        }
    }

    @Test
    fun testRegister() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/admin/register?company-name=xx&price=100&amount=200").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/?company-name=xx").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
                Assertions.assertNotEquals("[ ]", response.content)
            }
        }
    }

    @Test
    fun testPriceChange() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/admin/register?company-name=xx&price=100&amount=200").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/admin/change?company-name=xx&new-price=101").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/?company-name=xx").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
                Assertions.assertTrue(response.content!!.contains("101"))
            }
        }
    }

    @Test
    fun testBuy() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/admin/register?company-name=xx&price=100&amount=200").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/buy?company-name=xx&amount=200").apply {
                Assertions.assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/buy?company-name=xx&amount=1").apply {
                Assertions.assertNotEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
