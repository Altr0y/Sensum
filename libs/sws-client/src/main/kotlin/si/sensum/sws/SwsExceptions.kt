package si.sensum.sws

sealed class SwsException(message: String) : RuntimeException(message)

class SwsUnauthorizedException(message: String = "Invalid SWS username or password") :
    SwsException(message)

class SwsHttpException(
    val statusCode: Int,
    message: String
) : SwsException(message)

class SwsTimeoutException(
    message: String = "SWS request timed out"
) : SwsException(message)

class SwsInvalidResponseException(message: String) :
    SwsException(message)