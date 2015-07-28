import android.Keys._
android.Plugin.androidBuild

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.10.3"
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
scalacOptions += "-target:jvm-1.7"

libraryDependencies ++= Seq(
    "org.scaloid" % "scaloid_2.10" % "3.3-8",
    "joda-time" % "joda-time" % "2.8.1",
    "org.joda" % "joda-convert" % "1.7"
)

run <<= run in Android
install <<= install in Android
uninstall <<= uninstall in Android
proguard <<= proguard in Android

apkbuildExcludes in Android ++= Seq("META-INF/LICENSE.txt", "META-INF/NOTICE.txt")
retrolambdaEnable in Android := false

proguardCache in Android ++= Seq("org.scaloid")
proguardOptions in Android ++= Seq(
    "-keep class * extends java.util.ListResourceBundle {" +
    "   protected java.lang.Object[][] getContents();" +
    "}",

    "-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {" +
    "    public static final *** NULL;" +
    "}",

    "-keepnames @com.google.android.gms.common.annotation.KeepName class *",

    "-keepclassmembernames class * {" +
    "    @com.google.android.gms.common.annotation.KeepName *;" +
    "}",

    "-keepnames class * implements android.os.Parcelable {" +
    "    public static final ** CREATOR;" +
    "}",

    "-keepnames class org.joda.time.Minutes ",

    "-dontwarn android.app.Activity",
    "-dontwarn android.app.AppOpsManager",
    "-dontwarn android.app.Fragment",
    "-dontwarn android.app.Notification",
    "-dontwarn android.app.Notification$BigTextStyle",
    "-dontwarn android.app.Notification$Builder",
    "-dontwarn android.app.Notification$Style",
    "-dontwarn android.app.Presentation",
    "-dontwarn android.content.pm.PackageInstaller",
    "-dontwarn android.content.pm.PackageInstaller$SessionInfo",
    "-dontwarn android.content.pm.PackageManager",
    "-dontwarn android.hardware.display.DisplayManager",
    "-dontwarn android.hardware.display.VirtualDisplay",
    "-dontwarn android.net.ConnectivityManager",
    "-dontwarn android.os.Message",
    "-dontwarn android.view.accessibility.CaptioningManager",
    "-dontwarn android.view.accessibility.CaptioningManager$CaptionStyle",
    "-dontwarn android.view.Display",
    "-dontwarn android.view.View",
    "-dontwarn android.view.ViewTreeObserver",
    "-dontwarn android.webkit.WebSettings",
    "-dontwarn android.webkit.WebView",
    "-dontwarn android.widget.FrameLayout",
    "-dontwarn com.google.android.gms.*",
    "-dontwarn com.google.android.gms.cast.CastPresentation",
    "-dontwarn com.google.android.gms.internal.zzig",
    "-dontwarn javax.xml.bind.DatatypeConverter"
    // ,
    // "-dontobfuscate",
    // "-dontoptimize",
    // "-dontpreverify"
)
