package si.sensum.sws.operation

import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsSoapOperation

object LoginOperation {

    private const val METHOD_NAME = "Login"

    fun create(
        username: String,
        password: String
    ): SwsSoapOperation {
        return SwsOperationFactory.create(
            methodName = METHOD_NAME,
            soapVersion = SoapVersion.SOAP_11,
            parameters = listOf(
                "UserName" to username,
                "Password" to password
            )
        )
    }
}