package io.github.svbgabriel.infrastructure.externals.javascript

import kotlin.js.Promise

external fun encodeURIComponent(uriComponent: String): String

external fun fetch(url: String, options: dynamic = definedExternally): Promise<FetchResponse>

external interface FetchResponse {
    val ok: Boolean
    val status: Int
    fun json(): Promise<dynamic>
    fun text(): Promise<String>
}
