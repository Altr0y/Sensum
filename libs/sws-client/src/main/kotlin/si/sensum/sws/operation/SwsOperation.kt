package si.sensum.sws.operation

import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsSoapOperation

internal object SwsOperations {

    fun simple(
        methodName: String,
        parameters: List<Pair<String, String>> = emptyList(),
        soapVersion: SoapVersion = SoapVersion.SOAP_11
    ): SwsSoapOperation {
        return SwsOperationFactory.create(
            methodName = methodName,
            soapVersion = soapVersion,
            parameters = parameters
        )
    }
}