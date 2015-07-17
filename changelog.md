# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adheres to [Semantic Versioning](http://semver.org/).

## [3.0.0] - [unreleased]
### Added
- Added getElse to the Maybe interface as a replacement for the now deprecated getOrElse.

### Deprecated
- Deprecated getOrElse(Supplier<A>) to be replaced with {@link #getElse(Supplier)} because 
Java 8 type inference cannot disambiguate between an overloaded method taking a generic A and
the same method taking a Supplier<A>.

## [2.4.0] - [unreleased]
### Added
- ScalaConverters moved to com.atlassian.fugue.converters package and 
deprecated in existing com.atlassian.fugue package, to be removed in 3.0 release
- Iterables has added the following new methods
    * iterate
    * unfold

### Changed
- com.atlassian.fugue.converters.ScalaConverters changed syntax to 'toScala', 'toJava'

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
  
