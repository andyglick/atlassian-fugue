# Change Log
All notable changes to this project will be documented in this file.

This project attempts to adhere to [Semantic Versioning](http://semver.org/).

## [5.0.0] - [unreleased]
### Changed
- This version is Java 11 compatible
- This version is Platform 5 compatible
- Changed the scala version to 2.11.12
- Changed guava to version 26.0-jre

## [4.7.2] - [2018-09-19]
### Added
- added `FugueCollectors` to complement fugue types with Stream API support which includes:
  * `toEitherRight` collector (Similar to `Eithers#sequenceRight`)
  * `toEitherLeft` collector (Similar to `Eithers#sequenceLeft`)
  * `flatten` collector which flattens stream of `Option`s into stream of values filtering `None`s
  * `toTrySuccess` collector (Similar to `Try#sequence`)

## [4.7.1] - [2018-07-19]
### Changed
- Changed javadoc for `Try#filterOrElse` to mention handling of `Try#delayed`
- Support for `Try` as `Iterable`, allowing it to be used in `Pair#zip`

## [4.7.0] - [2018-07-18]
### Added
- added `TryMatchers` to complement current set of hamcrest matchers
- Try includes additional methods:
    * `filterOrElse`
    * `orElse`
    * `forEach`
    * `toOption`
    * `toStream`
- Either includes includes additional methods:
    * `filterOrElse`
- a new `fugue-extensions` module has been included that will include:
    * `Function3`, `Function4`, `Function5`, `Function6` as complements to existing JDK Function and BiFunction
    * `Predicate3`, `Predicate4`, `Predicate5`, `Predicate6` as complements to existing JDK Predicate and BiPredicate
    * Steps package that provides some "for-comprehension" styled syntax shortcuts.
        * `Steps.begin(..)` has methods for `Either`, `Option`, `Try` and `Optional` monad types
        * `then`, `filter` and `yield` is included with each step
        * Maximum of 6 steps permitted
- Try#Success, Try#Failure and Try#Delayed are serializable

## [4.6.2] - [2018-06-15]
### Changed
- changed Iterables#rangeTo(int,int) and Iterables#rangeTo(int,int,int) to properly handle max and min integers
- changed rangeTo().iterator().next() to throw NoSuchElementException when hasNext() returns false

## [4.6.1] - [2018-03-12]
### Added
- added `Pair#zip(Optional, Optional)`
- added `Eithers#sequenceRight(Iterable, Collector)`
- added `Eithers#sequenceLeft(Iterable, Collector)`
- added `Try#sequence(Iterable, Collector)`
- added `Checked#delay(Supplier)`, `Try#delayed(Supplier<Try>)` and `Try#Delayed`.
  This delays the execution of a computation that may result in an exception or return a successfully computed value.
  It works nicely with `Try#sequence(Iterable)` to bypass following computations if a previous computation has already resulted in an exception.
  
### Deprecated
- `Checked#of(Supplier)` deprecated in favour of `Checked#now(Supplier)` to make it explicit that the evaluation is immediate. 

## [4.5.1] - [2017-12-8]
### Changed
- typo in Either#fold javadoc
- slf4j version bump 1.6.0 to 1.7.25
- jsr305 version bump 3.0.0 to 3.0.2
- clover version bump 4.1.1 to 4.1.2
- support for partial recover for Try via Try#recover(Class, Function) and Try#recoverWith(Class, Function)

## [2.7.0] - [2017-10-11]
### Changed
- minimum jdk version to 1.7 to support forward compatibility with gauva v22+
- Now throws IllegalStateException instead of UnsupportedOperationException from emptyiterable.iterator and none.iterator. This is a consequence
  of swapping out guava's empty iterator for collections.emptyiterator. This change is _not_ present in fugue v3+.

## [4.5.0] - [2017-04-09]
### Added
- Added toStream to Option and Either types that will return a java.util.stream.Stream object of either a single element Stream or empty Stream

## [4.4.0] - [2016-12-13]
### Added
- Added Functions#fromConsumer(Consumer<D>) returning Function<D, Unit> to convert between consumers and functions
- Added Try.java as an implementation of an exception handling monad.
  This is similar to Either but is explicitly intended for handling a computation that may result in an exception or return a successfully computed value.
  See Try.java for further documentation. Note the methods on Try do not implicitly catch exceptions thrown by argument functions.
- Added Checked.java which contains utility functions for working with Try, for example lifting potentially throwing functions into functions that return a Try.

## [4.3.1] - [2016-09-27]
- fugue-guava added an explicit lower bound osgi instruction. Where previously maven-bundle-plugin inserted [18,19) by default now only 18 is present.


## [4.3.0] - 2016-08-17
### Added
- Right biased getOr method added to Either, that accepts a Supplier

### Deprecated
- Right biased getOrElse that accepts a Supplier deprecated in favour of getOr 


## [4.2.2] - 2016-08-09
### Added
- fugue-scala bundle manifest instructions

### Changed
- fugue-scala dependency on scala at 2.11.8

## [4.2.1] - 2016-08-09
### Changed
- fugue-scala only requires fuge-optics as a test dependency
- fugue-optics now explicitly specifying OSGI import package
- fugue-quickcheck-generators and fugue-hamcrest now have provided dependency on fugue

## [4.2.0] - 2016-08-05
### Added
- A new anonymous inner class Option$1 to deserialize Option$1 into Option$None using readResolve() for backwards compatibility

### Changed
- The original anonymous inner class Option$1 is now known as Option$None for slightly more informative stack traces

## [4.1.0] - 2016-08-05
### Added
- fugue-hamcrest module which adds hamcrest matchers for Either and Option
- fugue-quickcheck-generators module which adds junit-quickcheck generators for Either and Option

### Changed
- Option.equals now handles the situation where Option.some has a null value

## [4.0.1] - 2016-08-02
### Changed
- fugue-optics dependency on fugue changed from compile scope to provided scope.

## [4.0.0] - 2016-07-26
### Added
- Either.leftOr(Function<R, ? extends L>) returns the left if exists or the result of applying the transformer to right
- Either.rightOr(Function<L, ? extends R>) returns the right if exists or the result of applying the transformer to left
- Either.toOptional added to the Left and Right as well as right-biased
- io.atlassian.fugue.optic.* in the fugue-optics package.
- Functions.ap and Suppliers.ap host the ap from applicative for Function and Supplier

### Changed
- io.atlassian.fugue.retry.* has been moved from the fugue-guava package to fugue-retry.
- io.atlassian.fugue.deprecated.* has been moved from the fugue-guava package to fugue-deprecated. This is planned to remove in 5.0.

### Deprecated
- Either.valueOr, in favor of the symmetrically-named Either.rightOr

## [3.1.0]
### Added
- Monoid and Semigroup abstractions
- Iterables.collect(Iterable, java.util.stream.Collector) to reduce an Iterable using a Java8 Collector
- Either.forEach(Consumer<T>)
- Option.forEach(Consumer<T>)
- The Applicant interface (standalone) with one method: forEach(java.util.function.Consumer)

### Deprecated
- Effect.Applicant.foreach and all its implementations

## [3.0.0]
### Added
- JDK 1.8 is now the require minimum Java version
- Added getOr(Supplier<A>) to the Maybe interface as a replacement for the now deprecated getOrElse(Supplier<A>).
- Iterables.size returning the size of the input iterable (generally in O(n))
- Iterables.map create a new iterable by applying a function to each element
- Iterables.transform forwarding function calling map to help migration
- Iterables.filter returning a new iterable containing only those elements for which the predicate returns true
- Iterables.join transforms a nested iterable of iterables into a single iterable
- Iterables.addAll adds all of the input collection into the passed iterable
- Iterables.cycle returns an infinite iterable that cycles through the input elements (does not support removing elements)
- Iterables.makeString pretty prints the contents of an iterable
- Iterables.concat creates a single iterable containing all of the input iterables values
- Iterables.takeWhile creates a new iterable that contains only the begin elements of the input iterable
- Iterables.dropWhile creates a new iterable that skips the beginning elements of the input iterable
- Options.nullSafe transforms a null producing function into one returning an option
- Options.toOption returns a function that builds an option
- Option.fromOptional and Option.toOptional for interoperability with java.util.Optional
- Functions.contant returns a function that always produces a constant return value
- Functions.forMap returns a function that performs a map lookup returning an Option
- Functions.forMapWithDefault returns a function that performs a map lookup returning a default value when it fails
- Suppliers.memoize and Suppliers.weakMemoize provide call-by-need evaluation strategies
- Functional interface annotation to Effect interface

### Changed
- The root package has change from com to io to facilitate compatibility with previous versions of Fugue. 
- Throwables and Function2 have been moved to fugue-guava package and the io.atlassian.fugue.deprecated package.
They will be removed in 4.0.
- Usages of io.atlassian.fugue.Function2 were replaced by java.util.function.BiFunction in the base fugue package
- ImmutableMaps can now be found in the fugue-guava package under io.atlassian.fugue.extras.
- io.atlassian.fugue.retry.* can now be found in the fugue-guava package.
- Effect now extends java.util.functions.Consumer
- Iterables.mergeSorted now takes a Comparator instead of a Guava Ordering instance
- All copies of com.google.common.base.Function/Supplier/Predicate have been replaced with the equivalent classes
from Java 8
- Either.apply renamed to ap to improve consitancy of apply methods
- ScalaConverters._ now convert between Scala types and Java 8 types (Guava converters are no longer available)

### Deprecated
- Deprecated getOrElse(Supplier<A>) to be replaced with {@link #getElse(Supplier)} because 
Java 8 type inference cannot disambiguate between an overloaded method taking a generic A and
the same method taking a Supplier<A>. It will be removed in 4.0.
- Iterables.transform exists to ease migration by reimplementing the guava equivalent

### Removed
- Dependency on the Guava library.
- Either.merge, Either.cond, Either.getOrThrow, Either.sequenceRight/Left were deprecated in 1.2. They are now removed.
All of those methods were moved to the Eithers class.
- All transitive dependencies have been removed from the fugue module. This include com.tlassian.util.concurrent, guava,q
slf4j and jsr305.
- Internal usages of Preconditions.checkState have been removed. Methods returning IllegalArguementExceptions may no longer
return the same message string inside the exception.
- Option.find and Option.filterNone were deprecated in 1.1 and are now removed. Find their replacements in Options.
- Dependency on maven.atlassian.com. You no longer need to include that repository

## [2.6.1] - 2015-10-17
### Removed
- Deprecation on Funciton2. Fugue 2.x will maintain compatibility with JDK 1.6

## [2.6.0] - 2015-08-31
### Added
- Added additional right-biased method to Either:
   * toOption
   * sequence
   * apply

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
  
