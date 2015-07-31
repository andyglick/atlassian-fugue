# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adheres to [Semantic Versioning](http://semver.org/).

## [2.5.0] - [unreleased]
### Added
- Added a static Unit() method to com/atlassian/fugue/Unit that simply returns the 
Unit.VALUE enum

### Changed
- Left.hashCode changed to avoid clashing with Right.hashCode when you have a left and a right containing the same
value

## [2.4.0] - 2015-07-15
### Added
- ScalaConverters moved to com/atlassian/fugue/converters package
- Iterables has added the following new methods
    * iterate - returns an infinite iterable built from the given iteration function
    * unfold - returns an iterable built from the seed function which is called until it returns none()

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
  
