package si.sensum.shared.auth.bearer

object BearerToken {
    fun extract(authorizationHeader: String?): String? {
        if (authorizationHeader.isNullOrBlank()) return null

        val prefix = "Bearer "
        if (!authorizationHeader.startsWith(prefix, ignoreCase = true)) return null

        return authorizationHeader
            .substring(prefix.length)
            .trim()
            .takeIf { it.isNotBlank() }
    }
}