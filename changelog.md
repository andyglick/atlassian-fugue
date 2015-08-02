# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adheres to [Semantic Versioning](http://semver.org/).

## [3.0.0] - [unreleased]
### Added
- Added getOr(Supplier<A>) to the Maybe interface as a replacement for the now deprecated getOrElse(Supplier<A>).
- Iterables.size returning the size of the input iterable (generally in O(n))
- Iterables.transform returning a new iterable with the input function applied
- Iterables.filter returning a new iterable containing only those elements for which the predicate returns true
- Iterables.flatten transforms a nested collection of iterables into a single iterable
- Iterables.addAll adds all of the input collection into the passed iterable
- Iterables.concat creates a single iterable containing all of the input iterables values
- Options.nullSafe transforms a null producing function into one returning an option
- Options.toOption returns a function that builds an option
- Option.fromOptional and Option.toOptional for interoperability with java.util.Optional
- Functions.contant returns a function that always produces a constant return value

### Changed
- Throwables and Function2 have been moved to fugue-guava package and the com.atlassian.fugue.deprecated package.
They will be removed in 4.0.
- Usages of com.atlassian.fugue.Function2 were replaced by java.util.function.BiFunction in the base fugue package
- ImmutableMaps can now be found in the fugue-guava package under com.atlassian.fugue.extras.
- com.atlassian.fugue.retry.* can now be found in the fugue-guava package.
- Effect now extends java.util.functions.Consumer
- Iterables.mergeSorted now takes a Comparator instead of a Guava Ordering instance
- All copies of com.google.common.base.Function/Supplier/Predicate have been replaced with the equivalent classes
from Java 8

### Deprecated
- Deprecated getOrElse(Supplier<A>) to be replaced with {@link #getElse(Supplier)} because 
Java 8 type inference cannot disambiguate between an overloaded method taking a generic A and
the same method taking a Supplier<A>. It will be removed in 4.0.

### Removed
- Either.merge, Either.cond, Either.getOrThrow, Either.sequenceRight/Left were deprecated in 1.2. They are now removed.
All of those methods were moved to the Eithers class.
- All transitive dependencies have been removed from the fugue module. This include com.tlassian.util.concurrent, guava,q
slf4j and jsr305.
- Internal usages of Preconditions.checkState have been removed. Methods returning IllegalArguementExceptions may no longer
return the same message string inside the exception.
- Option.find and Option.filterNone were deprecated in 1.1 and are now removed. Find their replacements in Options.

## [2.5.0] - 2015-07-31
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
  
