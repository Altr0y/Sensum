package si.sensum.demo.api

class ApiSession {
    private var token: String? = null

    fun saveToken(value: String) {
        token = value
    }

    fun requireToken(): String {
        return token ?: error("Not logged in")
    }

    fun clear() {
        token = null
    }
}