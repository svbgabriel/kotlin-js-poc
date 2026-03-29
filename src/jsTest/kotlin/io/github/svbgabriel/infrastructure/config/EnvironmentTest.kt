package io.github.svbgabriel.infrastructure.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EnvironmentTest : FunSpec({

    test("should get default value when variable is not present") {
        val result = Environment.get("NON_EXISTENT", "default")
        result shouldBe "default"
    }

    test("should get variable value when present") {
        Environment.set("EXISTING_VAR", "value")
        val result = Environment.get("EXISTING_VAR", "default")
        result shouldBe "value"
        Environment.set("EXISTING_VAR", null)
    }

    test("should get or null") {
        Environment.getOrNull("NON_EXISTENT_NULL") shouldBe null
        
        Environment.set("EXISTING_VAR_NULL", "some value")
        Environment.getOrNull("EXISTING_VAR_NULL") shouldBe "some value"
        Environment.set("EXISTING_VAR_NULL", null)
    }

    test("should get int") {
        Environment.getInt("NON_EXISTENT_INT", 3000) shouldBe 3000
        
        Environment.set("PORT", "8080")
        Environment.getInt("PORT", 3000) shouldBe 8080
        
        Environment.set("PORT", "invalid")
        Environment.getInt("PORT", 3000) shouldBe 3000
        Environment.set("PORT", null)
    }

    test("should get boolean") {
        Environment.getBoolean("NON_EXISTENT_BOOL", true) shouldBe true
        
        Environment.set("DEBUG", "true")
        Environment.getBoolean("DEBUG", false) shouldBe true
        
        Environment.set("DEBUG", "false")
        Environment.getBoolean("DEBUG", true) shouldBe false
        
        Environment.set("DEBUG", "invalid")
        Environment.getBoolean("DEBUG", true) shouldBe true
        Environment.set("DEBUG", null)
    }
})
