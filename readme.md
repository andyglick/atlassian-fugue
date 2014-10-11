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

## Scala Integration

From 2.2 there is a fugue-scala module that adds some helper methods to convert common 
Fugue and Guava classes into their Scala equivalents. For instance, to convert a scala function
