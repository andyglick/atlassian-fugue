# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adheres to [Semantic Versioning](http://semver.org/).

## [3.0.0] - [unreleased]
### Added
- Added getOr(Supplier<A>) to the Maybe interface as a replacement for the now deprecated getOrElse(Supplier<A>).
This works around an ambiguity in method resolution in Java 8 for overloaded methods taking either generic values or
functional interfaces.

### Deprecated
- Deprecated getOrElse(Supplier<A>) to be replaced with {@link #getElse(Supplier)} because 
Java 8 type inference cannot disambiguate between an overloaded method taking a generic A and
the same method taking a Supplier<A>.

## [2.4.0] - 2015-07-15
### Added
- ScalaConverters moved to com/atlassian/fugue/converters package
- Iterables has added the following new methods
    * iterate
    * unfold

### Changed
- com/atlassian/fugue/converters/ScalaConverters syntax is now 'toScala' and 'toJava'

### Deprecated
- ScalaConverters, along with it's 'asScala' as 'asJava' deprecated in existing 
com/atlassian/fugue package, to be removed in 3.0 release
- com/atlassian/fugue/Throwables.java Java 7 close with resource and mutlticatch 
covers most of the need for Throwables
- com/atlassian/fugue/Function2.java Java 8 BiFunction will replace all uses of this
interface in future

## [2.3.1] - 2015-07-11
### Changed
- Either flatMap changed to be covariant on the other side of projection

## [2.3.0] - 2015-07-06
### Added
- Added additional right-biased methods to Either:
    * getOrElse
    * exists
    * forall
    * foreach
    * filter
    * valueOr
    * orElse
    * getOrNull
    * getOrThrow
    * getOrError
  
