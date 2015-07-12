# fugue

## Functional Extensions to Guava

Google's guava project is a solid utility library providing many useful interfaces
and utilities, and it is a very commonly added dependency for most projects.
Unfortunately, they have a strong NIH syndrome and are somewhat half-pregnant
when it comes to functional-programming. This library attempts to round out some 
of the deficiencies that a functional programmer finds when using Guava.

In particular it provides Option and Either types similar to the Scala library
as well as a Pair.

There also additional helper classes for common Function and Supplier operations.

## Issue Tracking

Issues are tracked in the [fugue](https://bitbucket.org/atlassian/fugue/issues) project on bitbucket.

## Getting fugue

Add the Atlassian public repository:


    <repositories>
        ...
        <repository>
          <id>atlassian-public</id>
          <url>https://maven.atlassian.com/content/groups/public/</url>
        </repository>
        ...
    </repositories>

And then add fugue as a dependency to your pom.xml:


    <dependencies>
        ...
        <dependency>
            <groupId>com.atlassian.fugue</groupId>
            <artifactId>fugue</artifactId>
            <version>2.2.0</version>
        </dependency>
        ...
    </dependencies>

## Guava compatibility

This library mostly only depends on core Guava functionality and should be compatible with
very old versions of Guava, and certainly with the newest versions. However, the tests may 
rely on API that is only available in later versions (at the time of writing a minimum of 
14.0 was needed to run the tests).

## Scala Integration

From 2.2 there is a fugue-scala module that adds some helper methods in Scala to convert common 
Fugue and Guava classes into their Scala equivalents and vice-versa. For instance, to convert a 
scala function `f` to a Guava `Function<A, B>` there is syntax `.asJava` available and to go the 
other way you can use `.asScala`.

To enable this syntax you need to add the following to your scope:

    import com.atlassian.fugue.converters.ScalaConverters._

## Contributors

Source code should be formatted according to the local style, which is encoded in the formatter
rules in:

    src/etc/eclipse/formatter.xml