
name := "concurrency-examples"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.17.2"

