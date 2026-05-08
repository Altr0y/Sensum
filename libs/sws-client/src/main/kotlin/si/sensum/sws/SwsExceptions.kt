package si.sensum.sws

sealed class SwsLoginException(message: String) : RuntimeException(message)

class SwsUnauthorizedException(message: String = "Invalid SWS username or password") :
    SwsLoginException(message)

class SwsHttpException(
    val statusCode: Int,
    message: String
) : SwsLoginException(message)

class SwsInvalidResponseException(message: String) :
    SwsLoginException(message)