package si.sensum.sws.operation

import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsSoapOperation

object LogoutOperation {

    private const val METHOD_NAME = "Logout"

    fun create(): SwsSoapOperation {
        return SwsOperationFactory.create(
            methodName = METHOD_NAME,
            soapVersion = SoapVersion.SOAP_11
        )
    }
}