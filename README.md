# 🚨 beacon

`beacon` provides Java utilities for **BEACON link dump format**, a data interchange format for large numbers of uniform links.

## Latest release

[![Release](https://jitpack.io/v/thunken/beacon.svg?style=flat-square)](https://github.com/thunken/beacon/releases)

To add a dependency on this project using Gradle, Maven, sbt, or Leiningen, we recommend using [JitPack](https://jitpack.io/#thunken/beacon/v1.0.1). The Maven group ID is `com.github.thunken`, and the artifact ID is `beacon`.

For example, for Maven, first add the JitPack repository to your build file:
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

And then add the dependency:
```xml
	<dependency>
	    <groupId>com.github.thunken</groupId>
	    <artifactId>beacon</artifactId>
	    <version>v1.0.1</version>
	</dependency>
```

## Documentation

* Javadoc: https://thunken.github.io/beacon/
  * Note: the current Javadoc for this project is incomplete. We rely on [Lombok](https://projectlombok.org/) to generate boilerplate code, and Lombok does not plug into Javadoc. Generated methods and constructors are not included, and the Javadoc for other methods and constructors may be incomplete. See [delombok](https://projectlombok.org/features/delombok) and [beacon#1](https://github.com/thunken/beacon/issues/1) for more information.
* BEACON specification: https://gbv.github.io/beaconspec/beacon.html

## Related projects

* https://github.com/gbv/beacon.jar, abandoned.
