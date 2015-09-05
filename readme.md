# fugue

## Functional Extensions

Java 8 along with the inclusion of functional interfaces standardised the basic function
interfaces. Google's guava project is a solid utility library providing many useful
interfaces and utilities, and it is a very commonly added dependency for most projects.
Unfortunately, they have a strong NIH syndrome and are somewhat half-pregnant when it
comes to functional-programming. This library attempts to round out some
of the deficiencies that a functional programmer finds when using Java 8 or Guava.

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
            <version>3.0.0</version>
        </dependency>
        ...
    </dependencies>
    
For Gradle add fugue as a dependency to your `dependencies` section:

    compile 'io.atlassian.fugue:fugue:3.0.0'

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

## Contributors

Source code should be formatted according to the local style, which is encoded in the formatter
rules in:

    src/etc/eclipse/formatter.xml

This can be applied by running maven-formatter-plugin for Java and maven-scalariform-plugin for
Scala:

    mvn formatter:format
    mvn scalariform:format

Source code should must be accompanied by a tests covering new functionality. Run tests with:

    mvn verify
