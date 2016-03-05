name := "travelo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaEbean,
  cache,
  "org.mongodb" % "mongo-java-driver" % "2.11.2",
  "net.vz.mongodb.jackson" % "play-mongo-jackson-mapper_2.10" % "1.1.0",
  "org.bouncycastle" % "bcprov-jdk16" % "1.45"
)     
    
play.Project.playJavaSettings
