# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adheres to [Semantic Versioning](http://semver.org/).

## [2.4.0] - [unreleased]
### Added
- ScalaConverters moved to com.atlassian.fugue.converters package and 
deprecated in existing com.atlassian.fugue package, to be removed in 3.0 release

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
  
