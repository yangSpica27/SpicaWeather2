package me.spica.spicaweather2.view.weather_bg

@Suppress("unused")
class Line {
  var x1 = 0
  var y1 = 0
  var x2 = 0
  var y2 = 0


  constructor(x1: Int, y1: Int, x2: Int, y2: Int) {
    this.x1 = x1
    this.y1 = y1
    this.x2 = x2
    this.y2 = y2
  }

  constructor(src: Line) {
    x1 = src.x1
    y1 = src.y1
    x2 = src.x2
    y2 = src.y2
  }

  operator fun set(x1: Int, y1: Int, x2: Int, y2: Int) {
    this.x1 = x1
    this.y1 = y1
    this.x2 = x2
    this.y2 = y2
  }

  fun negate() {
    x1 = -x1
    y1 = -y1
    x2 = -x2
    y2 = -y2
  }

  fun offset(dx: Int, dy: Int) {
    x1 += dx
    y1 += dy
    x2 += dx
    y2 += dy
  }

  fun equals(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    return this.x1 == x1 && this.y1 == y1 && this.x2 == x2 && this.y2 == y2
  }

  override fun equals(other: Any?): Boolean {
    if (other is Line) {
      return x1 == other.x1 && y1 == other.y1 && x2 == other.x2 && y2 == other.y2
    }
    return false
  }

  override fun toString(): String {
    return "Line($x1, $y1$x2, $y2)"
  }

  override fun hashCode(): Int {
    var result = x1
    result = 31 * result + y1
    result = 31 * result + x2
    result = 31 * result + y2
    return result
  }
}