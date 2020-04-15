# fugue

## Functional Extensions

Java 8 has standardised some of the basic function interfaces, but does not include quite a few more tools
that a functional programmer may expect to be available. This library attempts to fill in some of the
gaps when using Java 8.

In particular it provides Option and Either types as well as a Pair.

There also additional helper classes for common Function, Supplier, and Iterable operations.

## Issue Tracking

Issues are tracked in the [fugue](https://bitbucket.org/atlassian/fugue/issues) project on bitbucket.

## Change log

Changes are documented in `changelog.md`. 

## Getting fugue

Add fugue as a dependency to your pom.xml:

    <dependencies>
        ...
        <dependency>
            <groupId>io.atlassian.fugue</groupId>
            <artifactId>fugue</artifactId>
            <version>4.7.1</version>
        </dependency>
        ...
    </dependencies>
    
For Gradle add fugue as a dependency to your `dependencies` section:

    compile 'io.atlassian.fugue:fugue:4.7.1'

## Building fugue

To build and install to local Maven repo, run from project root folder:

    $ mvn clean install

To generate javadocs:

    $ mvn clean install javadoc:javadoc

This will generate javadocs for each project module in ```<module-dir>/target/site/apidocs/```.

## Guava compatibility

In the past Guava was a core dependency. That dependency has been removed in favor of a new module
`fugue-guava`. Code requiring direct interaction with Guava types can be found there.

## Scala integration

From 2.2 there is a `fugue-scala` module that adds some helper methods in Scala to convert common 
Fugue and Guava classes into their Scala equivalents and vice-versa. For instance, to convert a
scala function `f` to a Java `Function<A, B>` there is syntax `.toJava` available and to go the
other way you can use `.toScala`.

To enable this syntax you need to add the following to your scope:

    import io.atlassian.fugue.converters.ScalaConverters._

## Steps

From 4.7.0 there is a `fugue-extensions` module that adds some `for-comprehension` styled syntax 
shortcuts for io.atlassian.fugue Option and Either, as well as Java8 Optional.

This makes the following type of syntax possible. 

```
    Option<Double> either = Steps
                    .begin(firstOKOperation())
                    .then(str -> secondNGOperation(str))
                    .then(() -> thirdOKOperation(number))
                    .then((str, number, boo) -> fourthOKOperation(str, number, boo))
                    .then((str, number, boo, str2) -> fifthOKOperation())
                    .yield((str, number, boo, str2, fifth) -> new Double(number));
```
               
Works up to 6 Steps, and breaks out on the first Left (Either), none (Option), failure (Try) 
or empty (Optional)

See the javadoc for more details.

## Android

The latest release that supports JDK 1.6 is v2.6.1.
The latest release that supports JDK 1.7 is v2.7.0. This is forward compatibile with guava for 
andriod v22+ which requires 1.7.

## Migrating from Fugue v2.x to v3.x

See `changelog.md` for a list of changes. The root package changed from com to io to allow a
more gradual inclusion of the breaking changes introduced between v2 and v3. All the functional 
interfaces that came from Guava in v2 have been replaced with their equivalents in the Java 8
util.functions package. Replacing instances of com.google.common.base.Function with
java.util.function.Function will address that change. The Fugue Iterables class in v2 added some
missing functionality to the Guava Iterables class in a complementary rather than replacement 
fashion. This required one of the two Iterables classes to be imported by it's fully qualified
name when both were needed in a single source file. Immutable maps have been moved to the
fugue-guava module along with the com.atlassian.retry package, Throwables, and the Function2
interface. The Scala type conversion code has been migrated from using, and clashing on, asScala
to toScala. Many of the previously existing deprecations have been removed.

* Replace com.google.common.base.Function with java.util.function.Function for each of: Function, 
Supplier, and Predicate
* Replace com.atlassian.fugue.Function2 with java.util.function.BiFunction
* See `changelog.md` for new implementations of functions on Iterables to reduce the places where
you need to import io.atlassian.fugue.Iterables by it's FQN
* Find io.atlassian.fugue.retry.*, ImmutableMaps, Function2, and Throwables in the fugue-guava module
* Replace usages of asScala/asJava with toScala/toJava
* When conversion between a JDK and a Guava functional interface is required use of a method reference 
on the abstract method is recommended
* See the `changelog.md` for further changes

## Migrating from Fugue v3.x to v4.x

* Find io.atlassian.fugue.retry.* in the fugue-retry module
* Find ImmutableMaps in the fugue-guava module
* Find Function2 and Throwables in the fugue-deprecated module
* New fugue-optics module 
* See the `changelog.md` for further changes

## Contributors

Source code should be formatted according to the local style, which is encoded in the formatter
rules in:

    src/etc/eclipse/formatter.xml

This can be applied by running maven-formatter-plugin for Java and maven-scalariform-plugin for
Scala:

    mvn formatter:format
    mvn scalariform:format

Source code should must be accompanied by tests covering new functionality. Run tests with:

    mvn verify

For test coverage and other Clover checks run (highly recommended):

    mvn clean clover:setup test clover:aggregate clover:clover

*If you are having any issues with the provided clover license (e.g. it has expired), please raise an
issue and a member of the team will renew it.*

**Note:** It is not recommended to install/deploy classes that are instrumented for coverage. It is
better to separate the builds for coverage verification and install/deploy.

For more details read the [Clover documentation](https://confluence.atlassian.com/display/CLOVER/Basic+usage).

Maven releases are changing. Publishing to maven central will require manual intervention go/buildeng
