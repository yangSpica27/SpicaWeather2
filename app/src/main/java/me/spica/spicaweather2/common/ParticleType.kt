package me.spica.spicaweather2.common

object ParticleType {
  const val b2_waterParticle: Int = 0

  /** removed after next step  */
  const val b2_zombieParticle: Int = 1 shl 1

  /** zero velocity  */
  const val b2_wallParticle: Int = 1 shl 2

  /** with restitution from stretching  */
  const val b2_springParticle: Int = 1 shl 3

  /** with restitution from deformation  */
  const val b2_elasticParticle: Int = 1 shl 4

  /** with viscosity  */
  const val b2_viscousParticle: Int = 1 shl 5

  /** without isotropic pressure  */
  const val b2_powderParticle: Int = 1 shl 6

  /** with surface tension  */
  const val b2_tensileParticle: Int = 1 shl 7

  /** mixing color between contacting particles  */
  const val b2_colorMixingParticle: Int = 1 shl 8

  /** call b2DestructionListener on destruction  */
  const val b2_destructionListener: Int = 1 shl 9
}
