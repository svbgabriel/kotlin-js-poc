package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.dotenv.Dotenv
import io.github.svbgabriel.dotenv.DotenvOptions
import io.github.svbgabriel.express.ExpressApplication
import io.github.svbgabriel.express.ExpressUrlEncodedOptions
import io.github.svbgabriel.express.Request
import io.github.svbgabriel.express.Response
import io.github.svbgabriel.express.expressApplication
import io.github.svbgabriel.express.expressMiddleware
import io.github.svbgabriel.swagger.swaggerUi
import io.github.svbgabriel.swagger.OpenApiInfo
import io.github.svbgabriel.swagger.Operation
import io.github.svbgabriel.infrastructure.web.openapi.OpenApiRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic

class ExpressContext(
    private val req: Request,
    private val res: Response,
    private val jsonSerializer: Json = Json { ignoreUnknownKeys = true }
) : WebContext {
    override val params: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        val p = req.params
        val keys = js("Object.keys(p)") as Array<String>
        keys.forEach { map[it] = p[it].toString() }
        map
    }

    override val query: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        val q = req.query
        val keys = js("Object.keys(q)") as Array<String>
        keys.forEach { map[it] = q[it].toString() }
        map
    }

    override val headers: Map<String, List<String>> by lazy {
        val map = mutableMapOf<String, List<String>>()
        val h = req.headers
        val keys = js("Object.keys(h)") as Array<String>
        keys.forEach {
            val value = h[it]
            if (value is Array<*>) {
                map[it] = value.map { v -> v.toString() }
            } else {
                map[it] = listOf(value.toString())
            }
        }
        map
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T : Any> receive(serializer: KSerializer<T>): T {
        val body = req.body
        val headers = req.headers
        val isBodyUndefined = body == null
        val isEmptyObject = js("typeof body === 'object' && body !== null && Object.keys(body).length === 0") as Boolean
        val hasNoContent = headers["content-length"] == "0" || headers["content-length"] == undefined

        if (isBodyUndefined || (isEmptyObject && hasNoContent)) {
            throw BadRequestException("Request body is missing")
        }

        return jsonSerializer.decodeFromDynamic(serializer, body)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T : Any> respond(status: HttpStatus, body: T, serializer: KSerializer<T>) {
        res.status(status.statusCode)
        if (body is String) {
            res.send(body)
        } else {
            res.json(jsonSerializer.encodeToDynamic(serializer, body))
        }
    }

    override suspend fun respondStatus(status: HttpStatus) {
        res.status(status.statusCode).send(null)
    }

    override suspend fun respondError(status: HttpStatus, message: String) {
        res.status(status.statusCode).json(kotlin.js.json(
            "error" to true,
            "message" to message
        ))
    }
}

class ExpressWebApplication(
    val expressApp: ExpressApplication = expressApplication(),
    private val scope: CoroutineScope
) : AbstractWebApplication() {

    override var useDefaultMiddlewares: Boolean = true

    private var middlewaresApplied = false

    override fun doApplyDotenv() {
        val options = DotenvOptions(
            path = dotenv.path,
            encoding = dotenv.encoding,
            debug = dotenv.debug,
            override = dotenv.override,
            quiet = dotenv.quiet
        )

        Dotenv.config(options)
    }

    private fun applyDefaultMiddlewares() {
        if (useDefaultMiddlewares && !middlewaresApplied) {
            expressApp.use(expressMiddleware.json())
            expressApp.use(expressMiddleware.urlencoded(ExpressUrlEncodedOptions(extended = true)))
            middlewaresApplied = true
        }
    }

    override fun use(handler: suspend (WebContext) -> Unit) {
        applyDefaultMiddlewares()
        expressApp.use { req, res, _ ->
            val context = ExpressContext(req, res)
            scope.launch {
                try {
                    handler(context)
                } catch (e: Throwable) {
                    handleException(context, e)
                }
            }
        }
    }

    override fun route(path: String, builder: RoutingBuilder.() -> Unit) {
        RoutingBuilder(this, path).apply(builder)
    }

    private val _onRouteRegistered = mutableListOf<(method: String, path: String, openApi: Operation?) -> Unit>()

    override fun onRouteRegistered(callback: (method: String, path: String, openApi: Operation?) -> Unit) {
        _onRouteRegistered.add(callback)
    }

    override fun serveSwagger(path: String, info: OpenApiInfo, registry: OpenApiRegistry) {
        applyDefaultMiddlewares()

        // Serve Swagger UI
        expressApp.use(path, swaggerUi.serve)

        // Serve the raw spec as JSON for tests/debugging
        val jsonPath = if (path.endsWith("/")) "${path}json" else "$path/json"
        expressApp.get(jsonPath) { _, res, _ ->
            val spec = registry.generateSpec(info)
            res.json(spec)
        }

        expressApp.get(path) { req, res, next ->
            val spec = registry.generateSpec(info)
            try {
                swaggerUi.setup(spec)(req, res, next)
            } catch (e: Exception) {
                next(e)
            }
        }
    }

    override fun registerRoute(method: String, path: String, openApi: Operation?, handler: suspend WebContext.() -> Unit) {
        applyDefaultMiddlewares()
        _onRouteRegistered.forEach { it(method, path, openApi) }
        val expressHandler: (Request, Response, (Any?) -> Unit) -> Unit = { req, res, _ ->
            val context = ExpressContext(req, res)
            scope.launch {
                try {
                    handler(context)
                } catch (e: Throwable) {
                    handleException(context, e)
                }
            }
        }

        when (method.uppercase()) {
            "GET" -> expressApp.get(path, expressHandler)
            "POST" -> expressApp.post(path, expressHandler)
            "PUT" -> expressApp.put(path, expressHandler)
            "DELETE" -> expressApp.delete(path, expressHandler)
            "PATCH" -> expressApp.patch(path, expressHandler)
        }
    }

    override fun listen(port: Int, callback: () -> Unit): WebServer {
        applyDefaultMiddlewares()
        val server = expressApp.listen(port, callback)
        val webServer = object : WebServer {
            override fun close(callback: () -> Unit) {
                server.close(callback)
            }

            override suspend fun shutdown() {
                onShutdownHooks.forEach { it() }
                suspendCancellableCoroutine { continuation ->
                    server.close {
                        continuation.resume(Unit)
                    }
                }
            }
        }
        val dynamicWebServer = webServer.asDynamic()
        dynamicWebServer.address = { server.asDynamic().address() }
        return webServer
    }
}
