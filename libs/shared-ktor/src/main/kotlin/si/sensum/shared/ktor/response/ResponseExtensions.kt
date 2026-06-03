package si.sensum.shared.ktor.response

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <reified T : Any> ApplicationCall.respondOk(
    noinline block: suspend () -> T
) {
    respond(
        status = HttpStatusCode.OK,
        message = block()
    )
}

suspend inline fun <reified T : Any> ApplicationCall.respondCreated(
    noinline block: suspend () -> T
) {
    respond(
        status = HttpStatusCode.Created,
        message = block()
    )
}