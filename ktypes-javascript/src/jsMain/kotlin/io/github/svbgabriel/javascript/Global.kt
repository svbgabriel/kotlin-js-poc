package io.github.svbgabriel.javascript

import kotlin.js.Promise

/**
 * Encodes a text string as a valid component of a Uniform Resource Identifier (URI).
 * @param uriComponent A value representing an unencoded URI component.
 * @return The encoded URI component.
 */
external fun encodeURIComponent(uriComponent: String): String

/**
 * Starts the process of fetching a resource from the network.
 * @param url The resource you want to fetch.
 * @param options An object containing any custom settings that you want to apply to the request.
 * @return A [Promise] that resolves to a [FetchResponse] object.
 */
external fun fetch(url: String, options: dynamic = definedExternally): Promise<FetchResponse>

/**
 * Represents the response to a [fetch] request.
 */
external interface FetchResponse {
    /**
     * A boolean indicating whether the response was successful (status in the range 200–299).
     */
    val ok: Boolean

    /**
     * The status code of the response.
     */
    val status: Int

    /**
     * Takes a Response stream and reads it to completion. It returns a promise which resolves with the result of parsing the body text as JSON.
     */
    fun json(): Promise<dynamic>

    /**
     * Takes a Response stream and reads it to completion. It returns a promise which resolves with a string.
     */
    fun text(): Promise<String>
}
