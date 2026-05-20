package si.sensum.shared.ktor.response

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun ApplicationCall.respondOk(block: suspend () -> Any) {
    respond(HttpStatusCode.OK, block())
}

suspend fun ApplicationCall.respondCreated(block: suspend () -> Any) {
    respond(HttpStatusCode.Created, block())
}