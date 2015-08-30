package com.byteground.opencv

import org.opencv.core.Core
import org.opencv.core.{CvType, Mat, Scalar}
import org.specs2.Specification
import org.specs2.specification.Step

object OpenCVNativeSpecification
  extends Specification {


  final lazy val is = {
    s"This is a specification to check the native ${Core.NATIVE_LIBRARY_NAME} bindings" ^
      p ^
      s"${Core.NATIVE_LIBRARY_NAME} should" ^
      Step() ^
      "initialize correctly" ! initializeCorrectly ^
      Step() ^
      end
  }

  protected def initializeCorrectly = {
    {

      System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
      val m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0))
      //println("OpenCV Mat: " + m)
      val mr1 = m.row(1)
      mr1.setTo(new Scalar(1))
      val mc5 = m.col(5)
      mc5.setTo(new Scalar(5))
      //println("OpenCV Mat data:\n" + m.dump())
    } must not(throwA[Throwable])
  }
}