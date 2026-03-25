package io.github.svbgabriel.infrastructure.web.openapi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.js.json

class OpenApiRegistryTest : FunSpec({

    test("testRegisterMultipleMethodsOnSamePath") {
        val registry = OpenApiRegistry()
        val path = "/api/contacts"

        val getOp = Operation(summary = "Get contacts", responses = json())
        val postOp = Operation(summary = "Create contact", responses = json())

        registry.registerRoute("GET", path, getOp)
        registry.registerRoute("POST", path, postOp)

        val info = OpenApiInfo(title = "Test API", version = "1.0.0", description = "Test")
        val spec = registry.generateSpec(info)

        val paths = spec.paths.asDynamic()
        val pathItem = paths[path]

        val isNotNull = pathItem != null
        isNotNull shouldBe true
        (pathItem.get.summary as String) shouldBe "Get contacts"
        (pathItem.post.summary as String) shouldBe "Create contact"
    }

    test("testPathWithParameters") {
        val registry = OpenApiRegistry()
        val path = "/api/contacts/:id"

        val getOp = Operation(summary = "Get contact by ID", responses = json())
        registry.registerRoute("GET", path, getOp)

        val info = OpenApiInfo(title = "Test API", version = "1.0.0", description = "Test")
        val spec = registry.generateSpec(info)

        val paths = spec.paths.asDynamic()
        val openApiPath = "/api/contacts/{id}"
        val pathItem = paths[openApiPath]

        val isNotNull = pathItem != null
        isNotNull shouldBe true
        (pathItem.get.summary as String) shouldBe "Get contact by ID"
    }

    test("testMissingOperationFields") {
        val registry = OpenApiRegistry()
        val path = "/api/test"

        // Operation without optional fields
        val op = Operation(responses = json())
        registry.registerRoute("GET", path, op)

        val info = OpenApiInfo(title = "Test API", version = "1.0.0", description = "Test")
        val spec = registry.generateSpec(info)

        val paths = spec.paths.asDynamic()
        val pathItem = paths[path]

        (pathItem.get.responses != null) shouldBe true
    }

    test("testPathNormalization") {
        val registry = OpenApiRegistry()
        val path1 = "/api//contacts/"
        val path2 = "/api/contacts"

        val getOp = Operation(summary = "Get contacts", responses = json())
        val postOp = Operation(summary = "Create contact", responses = json())

        registry.registerRoute("GET", path1, getOp)
        registry.registerRoute("POST", path2, postOp)

        val info = OpenApiInfo(title = "Test API", version = "1.0.0", description = "Test")
        val spec = registry.generateSpec(info)

        val paths = spec.paths.asDynamic()
        val keys = js("Object.keys(paths)") as Array<String>

        // There should be only one route after normalization
        keys.size shouldBe 1
        keys[0] shouldBe "/api/contacts"

        val pathItem = paths["/api/contacts"]
        (pathItem.get.summary as String) shouldBe "Get contacts"
        (pathItem.post.summary as String) shouldBe "Create contact"
    }

    test("testJsNamesArePreservedInJson") {
        val registry = OpenApiRegistry()
        val path = "/api/test"
        val op = Operation(
            summary = "Test Summary",
            responses = json(),
            parameters = arrayOf(
                Parameter(name = "id", `in` = "path", required = true)
            )
        )
        registry.registerRoute("GET", path, op)

        val spec = registry.generateSpec(OpenApiInfo(title = "Test", version = "1.0.0", description = null))
        val specJson = spec.asDynamic()

        // Verify if JSON keys are as expected (not minified)
        val specKeys = js("Object.keys(specJson)") as Array<String>
        specKeys.shouldContain("openapi")
        specKeys.shouldContain("info")
        specKeys.shouldContain("paths")

        val pathItem = specJson.paths["/api/test"]
        val pathItemKeys = js("Object.keys(pathItem)") as Array<String>
        pathItemKeys.shouldContain("get")

        val getOp = pathItem.get
        val opKeys = js("Object.keys(getOp)") as Array<String>
        opKeys.shouldContain("summary")
        opKeys.shouldContain("responses")
        opKeys.shouldContain("parameters")

        val param = getOp.parameters[0]
        val paramKeys = js("Object.keys(param)") as Array<String>
        paramKeys.shouldContain("name")
        paramKeys.shouldContain("in")
        paramKeys.shouldContain("required")
    }
})
