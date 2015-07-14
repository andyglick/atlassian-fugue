# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adheres to [Semantic Versioning](http://semver.org/).

## [2.4.0] - [unreleased]
### Added
- ScalaConverters moved to com.atlassian.fugue.converters package and 
deprecated in existing com.atlassian.fugue package, to be removed in 3.0 release
- Iterables has added the following new methods
    * iterate
    * unfold

### Changed
- com.atlassian.fugue.converters.ScalaConverters changed syntax to 'toScala', 'toJava'

### Deprecated
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
  
