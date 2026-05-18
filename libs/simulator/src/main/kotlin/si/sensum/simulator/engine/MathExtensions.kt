package si.sensum.simulator.engine

import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

fun Random.nextGaussian(): Double {
    var u: Double
    var v: Double
    var s: Double
    do {
        u = nextDouble() * 2.0 - 1.0
        v = nextDouble() * 2.0 - 1.0
        s = u * u + v * v
    } while (s >= 1.0 || s == 0.0)
    return u * sqrt(-2.0 * ln(s) / s)
}