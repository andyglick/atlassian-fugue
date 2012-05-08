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
import static com.atlassian.fugue.Either.merge;
import static com.atlassian.fugue.Either.right;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EitherTest {
  @Test(expected = NullPointerException.class) public void testNullLeft() {
    Either.left(null);
  }

  @Test public void testLeftCreation() {
    final Either<Boolean, Integer> left = left(true);
    assertThat(left.isLeft(), is(true));
    assertThat(left.left().get(), is(true));
  }

  @Test(expected = NullPointerException.class) public void testNullRight() {
    right(null);
  }

  @Test public void testRightCreation() {
    final Either<Boolean, Integer> right = right(1);
    assertThat(right.isRight(), is(true));
    assertThat(right.right().get(), is(1));
  }

  @Test public void testLeftMerge() {
    assertThat(merge(Either.<String, String> left("Ponies.")), is("Ponies."));
  }

  @Test public void testRightMerge() {
    assertThat(merge(Either.<String, String> right("Unicorns.")), is("Unicorns."));
  }

  @Test public void testCondTrue() {
    assertThat(Either.cond(true, "Pegasus.", 7), is(Either.<Integer, String> right("Pegasus.")));
  }

  @Test public void testCondFalse() {
    assertThat(Either.cond(false, "Pegasus.", 7), is(Either.<Integer, String> left(7)));
  }
}
