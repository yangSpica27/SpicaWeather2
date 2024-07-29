package me.spica.spicaweather2.common

object ParticleGroupType {
    /** resists penetration  */
    const val b2_solidParticleGroup: Int = 1 shl 0

    /** keeps its shape  */
    const val b2_rigidParticleGroup: Int = 1 shl 1
}
