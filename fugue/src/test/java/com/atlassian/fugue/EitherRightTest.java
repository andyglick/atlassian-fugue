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

import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Eithers.getOrThrow;
import static com.atlassian.fugue.UtilityFunctions.bool2String;
import static com.atlassian.fugue.UtilityFunctions.int2String;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.google.common.base.Function;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.annotation.Nullable;

public class EitherRightTest {
  private static final Integer ORIGINAL_VALUE = 1;
  final Either<Boolean, Integer> either = right(ORIGINAL_VALUE);

  @Test public void rightGet() {
    assertThat(either.right().get(), is(ORIGINAL_VALUE));
  }

  @Test public void rightIsDefined() {
    assertThat(either.right().isDefined(), is(true));
  }

  @Test public void leftIsDefined() {
    assertThat(either.left().isDefined(), is(false));
  }

  @Test public void isRight() {
    assertThat(either.isRight(), is(true));
  }

  @Test public void isLeft() {
    assertThat(either.isLeft(), is(false));
  }

  @Test public void getRight() {
    assertThat(either.getRight(), is(1));
  }

  @Test(expected = NoSuchElementException.class) public void getLeft() {
    either.left().get();
  }

  @Test public void swapIsLeft() {
    assertThat(either.swap().isLeft(), is(true));
  }

  @Test public void swapLeftIsEitherRight() {
    assertThat(either.swap().left().get(), is(either.right().get()));
  }

  @Test public void swapLeftIsOriginal() {
    assertThat(either.swap().left().get(), is(ORIGINAL_VALUE));
  }

  @Test public void map() {
    assertThat(either.fold(bool2String, int2String), is(valueOf(ORIGINAL_VALUE)));
  }

  @Test public void mapLeft() {
    assertThat(either.left().map(bool2String).left().isEmpty(), is(true));
  }

  @Test public void mapRight() {
    assertThat(either.right().map(int2String).right().get(), is(valueOf(ORIGINAL_VALUE)));
  }

  @Test public void toStringTest() {
    assertThat(either.toString(), is("Either.Right(1)"));
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

  @Test public void rightPredicateMatches() {
    assertThat(Eithers.<Boolean, Integer> isRight().apply(either), is(true));
  }

  @Test public void notThrowsException() throws IOException {
    final Either<IOException, String> either = right("boo yaa!");
    assertThat(getOrThrow(either), is("boo yaa!"));
  }

  @Test public void upcastRightOnRight() {
    Either<String, Integer> e = Either.right(1);
    Either<String, Number> result = Eithers.<String, Number, Integer> upcastRight(e);
    Number expected = 1;
    assertThat(result.getRight(), is(expected));
  }

  @Test public void upcastRightOnLeft() {
    Either<String, Integer> e = Either.left("a");
    Either<String, Number> result = Eithers.<String, Number, Integer> upcastRight(e);
    assertThat(result.left().get(), is("a"));
  }

  @Test public void flatMapRightSubTypes() {
    class Type {}
    class AnotherType extends Type{}

    final AnotherType anotherType = new AnotherType();
    final Either<Boolean, AnotherType> r = Either.right(anotherType);

    final Either<Boolean, AnotherType> either = Either.<Boolean, Type>right(new Type()).right()
      .flatMap(new Function<Type, Either<Boolean, AnotherType>>() {
        @Nullable
        @Override
        public Either<Boolean, AnotherType> apply(@Nullable final Type input) {
          return r;
        }
      });

    final Type type = either.right().get();

    assertThat(type, Matchers.<Type>is(anotherType));
  }

  @Test public void flatMapRightWithUpcastAndSubtypes() {
    class Type {}
    class MyType extends Type {}
    class AnotherType extends Type{}

    final MyType myType = new MyType();
    final AnotherType anotherType = new AnotherType();

    final Either<Boolean, MyType> r = Either.right(myType);
    final Either<Boolean, AnotherType> r2 = Either.right(anotherType);

    final Either<Boolean, AnotherType> either = Eithers.<Boolean, Type, MyType>upcastRight(r).right()
      .flatMap(new Function<Type, Either<Boolean, AnotherType>>() {
        @Nullable
        @Override
        public Either<Boolean, AnotherType> apply(@Nullable final Type input) {
          return r2;
        }
      });

    final Type errorType = either.right().get();

    assertThat(errorType, Matchers.<Type>is(anotherType));
  }
}
