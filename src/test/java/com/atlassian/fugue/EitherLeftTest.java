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

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Eithers.getOrThrow;
import static com.atlassian.fugue.UtilityFunctions.bool2String;
import static com.atlassian.fugue.UtilityFunctions.int2String;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.Test;

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

  @Test public void getLeft() {
    assertThat(either.getLeft(), is(true));
  }

  @Test public void swap() {
    final Either<Integer, Boolean> swapped = either.swap();
    assertThat(swapped.isRight(), is(true));
    assertThat(swapped.right().get(), is(either.left().get()));
    assertThat(swapped.right().get(), is(ORIGINAL_VALUE));
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
    assertThat(Eithers.<Boolean, Integer> isLeft().apply(either), is(true));
  }

  @Test(expected = IOException.class) public void throwsException() throws IOException {
    final Either<IOException, String> either = left(new IOException());
    getOrThrow(either);
  }
}
