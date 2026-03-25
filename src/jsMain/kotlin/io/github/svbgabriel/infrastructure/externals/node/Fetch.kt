package io.github.svbgabriel.infrastructure.externals.node

import kotlin.js.Promise

/**
 * Node.js fetch API (available in Node.js 18+).
 */
external fun fetch(url: String, options: dynamic = definedExternally): Promise<FetchResponse>

external interface FetchResponse {
    val ok: Boolean
    val status: Int
    fun json(): Promise<dynamic>
    fun text(): Promise<String>
}
