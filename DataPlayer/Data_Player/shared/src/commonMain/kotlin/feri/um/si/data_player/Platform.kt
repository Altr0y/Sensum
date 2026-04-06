package feri.um.si.data_player

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform