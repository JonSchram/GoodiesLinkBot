resolvers += Resolver.typesafeRepo("releases")

resolvers += "scm-manager releases repository" at "http://maven.scm-manager.org/nexus/content/groups/public"

resolvers += "ByTeGround Maven Public Releases Repository" at "http://repository.byteground.com/maven/public/releases"

resolvers += Resolver.url("ByTeGround Ivy Public Releases Repository", url("http://repository.byteground.com/ivy/public/releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.byteground" %% "byteground-sbt-community-settings" % "3.25.0")

