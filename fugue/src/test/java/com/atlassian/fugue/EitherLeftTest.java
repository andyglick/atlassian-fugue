/*
   Copyright 2011 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.atlassian.fugue;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Eithers.getOrThrow;
import static com.atlassian.fugue.UtilityFunctions.bool2String;
import static com.atlassian.fugue.UtilityFunctions.int2String;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EitherLeftTest {
  private static final Boolean ORIGINAL_VALUE = true;
  final Either<Boolean, Integer> either = left(ORIGINAL_VALUE);

  @Test public void leftGet() {
    assertThat(either.left().get(), is(ORIGINAL_VALUE));
  }

  @Test public void right() {
    assertThat(either.right().isDefined(), is(false));
  }

  @Test public void isRight() {
    assertThat(either.isRight(), is(false));
  }

  @Test public void isLeft() {
    assertThat(either.isLeft(), is(true));
  }

  @Test(expected = NoSuchElementException.class) public void getRight() {
    either.getRight();
  }

  @Test public void swapIsRight() {
    assertThat(either.swap().isRight(), is(true));
  }

  @Test public void swapRightIsEitherLeft() {
    assertThat(either.swap().right().get(), is(either.left().get()));
  }

  @Test public void swapRightIsOriginal() {
    assertThat(either.swap().right().get(), is(ORIGINAL_VALUE));
  }

  @Test public void map() {
    assertThat(either.fold(bool2String, int2String), is(valueOf(ORIGINAL_VALUE)));
  }

  @Test public void mapRight() {
    assertThat(either.right().map(int2String).right().isEmpty(), is(true));
  }

  @Test public void mapLeft() {
    assertThat(either.left().map(bool2String).left().get(), is(valueOf(ORIGINAL_VALUE)));
  }

  @Test public void toStringTest() {
    assertThat(either.toString(), is("Either.Left(true)"));
  }

  @Test public void hashCodeTest() {
    assertThat(either.hashCode(), is(ORIGINAL_VALUE.hashCode()));
  }

  @Test public void equalsItself() {
    assertThat(either.equals(either), is(true));
  }

  @Test public void notEqualsNull() {
    assertThat(either.equals(null), is(false));
  }

  @Test public void leftPredicateMatches() {
    assertThat(Eithers.<Boolean, Integer> isLeft().test(either), is(true));
  }

  @Test(expected = IOException.class) public void throwsException() throws IOException {
    final Either<IOException, String> either = left(new IOException());
    getOrThrow(either);
  }

  @Test public void upcastLeftOnLeft() {
    Either<Integer, String> e = Either.left(1);
    Either<Number, String> result = Eithers.<Number, Integer, String> upcastLeft(e);
    Number expected = 1;
    assertThat(result.left().get(), is(expected));
  }

  @Test public void upcastLeftOnRight() {
    Either<Integer, String> e = Either.right("a");
    Either<Number, String> result = Eithers.<Number, Integer, String> upcastLeft(e);
    assertThat(result.getRight(), is("a"));
  }

  @Test public void flatMapLeftSubTypes() {
    class ErrorType {}
    class AnotherErrorType extends ErrorType{}

    final AnotherErrorType anotherErrorType = new AnotherErrorType();
    final Either<AnotherErrorType, Integer> l = Either.left(anotherErrorType);

    final Either<AnotherErrorType, Integer> either = Either.<ErrorType, Integer>left(new ErrorType()).left()
      .flatMap(input -> l);

    final ErrorType errorType = either.left().get();

    assertThat(errorType, Matchers.<ErrorType>is(anotherErrorType));
  }

  @Test public void flatMapLeftWithUpcastAndSubtypes() {
    class ErrorType {}
    class MyErrorType extends ErrorType{}
    class AnotherErrorType extends ErrorType{}

    final MyErrorType myErrorType = new MyErrorType();
    final AnotherErrorType anotherErrorType = new AnotherErrorType();

    final Either<MyErrorType, Integer> l = Either.left(myErrorType);
    final Either<AnotherErrorType, Integer> l2 = Either.left(anotherErrorType);

    final Either<AnotherErrorType, Integer> either = Eithers.<ErrorType, MyErrorType, Integer>upcastLeft(l).left()
      .flatMap(input -> l2);

    final ErrorType errorType = either.left().get();

    assertThat(errorType, Matchers.<ErrorType>is(anotherErrorType));
  }
}
