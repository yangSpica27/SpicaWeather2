package me.spica.spicaweather2.tools;

/**
 * @ClassName DegreeUtil
 * @Description 角度转化工具类
 * @Author Spica2 7
 * @Date 2023/2/7 9:37
 */
public class DegreeUtil {
  /**
   * 角度转换成弧度
   *
   * @return
   */
  public static double toRadians(double angle) {
    double rad = angle / 180 * Math.PI;
    return rad;
  }

  /**
   * 弧度转换成角度
   *
   * @param rad
   * @return
   */
  public static double toDegrees(double rad) {
    double angle = rad * 180 / Math.PI;
    return angle;
  }

  /**
   * 根据坐标得到弧度
   *
   * @param x
   * @param y
   * @return
   */
  public static double getCoordinateRadians(double x, double y) {
    double rad = Math.atan2(y, x);
    if (rad < 0) {
      rad = toRadians(toDegrees(rad) + 180);
    }
    return rad;
  }

  public static double getCoordinateRadians2(double x, double y) {
    double rad = Math.atan2(y, x);
    if (rad > 0) {
      rad = toRadians(180-toDegrees(rad));
    }
    return rad;
  }

  /**
   * 根据斜边长度，弧度，求角度邻边的直角边长度
   *
   * @param hypotenuse
   * @param rad
   * @return
   */
  public static double getCosSideLength(double hypotenuse, double rad) {
    double cosSideLength = hypotenuse * Math.cos(rad);
    return cosSideLength;
  }

  /**
   * 根据斜边长度，弧度，求角度对边的直角边长度
   *
   * @param hypotenuse
   * @param rad
   * @return
   */
  public static double getSinSideLength(double hypotenuse, double rad) {
    double sinSideLength = hypotenuse * Math.sin(rad);
    return sinSideLength;
  }

  /**
   * 根据直角边，斜边，求两条边夹角的弧度
   *
   * @param cosSideLength
   * @param hypotenuse
   * @return
   */
  public static double getCosRadians(double cosSideLength, double hypotenuse) {
    double rad = Math.acos(cosSideLength / hypotenuse);
    return rad;
  }

  /**
   * 根据直角边，斜边，求直角边对角的弧度
   *
   * @param sinSideLength
   * @param hypotenuse
   * @return
   */
  public static double getSinRadians(double sinSideLength, double hypotenuse) {
    double rad = Math.asin(sinSideLength / hypotenuse);
    return rad;
  }

}
