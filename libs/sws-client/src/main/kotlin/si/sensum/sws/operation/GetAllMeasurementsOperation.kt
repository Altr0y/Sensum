package si.sensum.sws.operation

import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsSoapOperation

object GetAllMeasurementsOperation {

    private const val METHOD_NAME = "GetAllMeasurements"

    fun create(
        datetimeFrom: String,
        datetimeTo: String
    ): SwsSoapOperation {
        return SwsOperationFactory.create(
            methodName = METHOD_NAME,
            soapVersion = SoapVersion.SOAP_11,
            parameters = listOf(
                "datetimeFrom" to datetimeFrom,
                "datetimeTo" to datetimeTo
            )
        )
    }
}