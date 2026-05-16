package si.sensum.sws.client

import si.sensum.sws.SwsSoapExecutor
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsSoapOperation

internal class SwsOperationExecutor(
    private val soapExecutor: SwsSoapExecutor
) {
    suspend fun <T> executeAndParse(
        session: SwsSession,
        operation: SwsSoapOperation,
        parser: (String) -> T
    ): T {
        val xml = soapExecutor.execute(
            operation = operation,
            session = session
        )

        return parser(xml)
    }
}