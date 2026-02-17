package io.github.svbgabriel.infrastructure.web.routes

import io.github.svbgabriel.infrastructure.web.controller.ContactController
import io.github.svbgabriel.infrastructure.externals.express.ExpressApplication
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.asyncHandler
import io.github.svbgabriel.infrastructure.web.errorMiddleware
import kotlinx.coroutines.CoroutineScope

fun ExpressApplication.configureRoutes(
    controller: ContactController,
    scope: CoroutineScope
) {
    route("/").get { _, res, _ ->
        res
            .status(HttpStatus.OK.statusCode)
            .set("Content-Type", "application/json")
            .send(
                //language=json
                """
                    {
                      "status": "UP"
                    }
                """.trimIndent()
            )
    }

    route("/api/contacts")
        .get(asyncHandler(scope) { req, res -> controller.select(req, res) })
        .post(asyncHandler(scope) { req, res -> controller.insert(req, res) })

    route("/api/contacts/:id")
        .get(asyncHandler(scope) { req, res -> controller.selectOne(req, res) })
        .delete(asyncHandler(scope) { req, res -> controller.delete(req, res) })
        .put(asyncHandler(scope) { req, res -> controller.update(req, res) })

    use(errorMiddleware)
}
