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

import org.junit.Test;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Eithers.cond;
import static com.atlassian.fugue.Eithers.merge;
import static com.atlassian.fugue.UtilityFunctions.addOne;
import static com.atlassian.fugue.UtilityFunctions.square;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EitherTest {
  @Test(expected = NullPointerException.class) public void testNullLeft() {
    Either.left(null);
  }

  @Test public void leftCreation() {
    final Either<Boolean, Integer> left = left(true);
    assertThat(left.isLeft(), is(true));
    assertThat(left.left().get(), is(true));
  }

  @Test(expected = NullPointerException.class) public void testNullRight() {
    right(null);
  }

  @Test public void rightCreation() {
    final Either<Boolean, Integer> right = right(1);
    assertThat(right.isRight(), is(true));
    assertThat(right.right().get(), is(1));
  }

  @Test public void leftMerge() {
    assertThat(merge(Either.<String, String> left("Ponies.")), is("Ponies."));
  }

  @Test public void rightMerge() {
    assertThat(merge(Either.<String, String> right("Unicorns.")), is("Unicorns."));
  }

  @Test public void condTrue() {
    assertThat(cond(true, 7, "Pegasus."), is(Either.<Integer, String> right("Pegasus.")));
  }

  @Test public void condFalse() {
    assertThat(cond(false, 7, "Pegasus."), is(Either.<Integer, String> left(7)));
  }

  @Test public void bimapRight() {
    assertThat(Either.<Integer, Integer> right(3).bimap(square, addOne), is(Either.<Integer, Integer> right(4)));
  }

  @Test public void bimapLeft() {
    assertThat(Either.<Integer, Integer> left(7).bimap(square, addOne), is(Either.<Integer, Integer> left(49)));
  }

  @Test public void hashCodeMirrorItegerMin() {
    assertThat(~(left(Integer.MIN_VALUE).hashCode()), is(right(Integer.MIN_VALUE).hashCode()));
  }

  @Test public void hashCodeMirrorItegerMax() {
    assertThat(left(Integer.MAX_VALUE).hashCode(), is(~(right(Integer.MAX_VALUE).hashCode())));
  }

  @Test public void hashCodeMirrorItegerZero() {
    assertThat(left(0).hashCode(), is(~(right(0).hashCode())));
  }
}
