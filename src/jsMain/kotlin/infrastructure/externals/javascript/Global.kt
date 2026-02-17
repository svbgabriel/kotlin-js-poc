package io.github.svbgabriel.infrastructure.externals.javascript

external fun encodeURIComponent(uriComponent: String): String

fun deleteProperty(obj: dynamic, property: String) {
    js("delete obj[property]")
}
