import com.byteground.Platform
import com.byteground.sbt.SbtByTeGround.autoImport._
import com.byteground.sbt.SbtNative.autoImport._
import com.byteground.sbt._
import sbt.Keys._
import sbt._

object BuildSettings {
  def opencvProject(name: String) = bytegroundProject("opencv-" + name).settings(
    scalaVersion := "2.10.4"
  ).enablePlugins(SbtTest)
}

object Build extends Build {
  import BuildSettings._

  val native = opencvProject("native").settings(
    nativePlatforms := Seq(Platform.LINUX_64, Platform.MACOSX_64, Platform.WINDOWS_32, Platform.WINDOWS_64),
    mappings in (Compile, packageBin) ++= {
      (unmanagedJars in Compile).value.flatMap { jar =>
        val targetFolder = target.value / jar.data.base
        IO.unzip(jar.data, targetFolder)
          .filterNot(_.name == "MANIFEST.MF")
          .pair(Path.relativeTo(targetFolder))
      }
    }
  ).enablePlugins(SbtNativeDependency, SbtNativeBundle)

  val root = bytegroundProject("opencv", isRoot = true).aggregate(native)
}