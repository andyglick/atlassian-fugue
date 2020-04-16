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
package io.atlassian.fugue;

import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;
import static io.atlassian.fugue.UtilityFunctions.reverse;
import static io.atlassian.fugue.UtilityFunctions.toStringFunction;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EitherLeftProjectionTest {
  private final Either<String, Integer> l = left("heyaa!");
  private final Either<String, Integer> r = right(12);
  final Supplier<String> boo = () -> "boo!";

  Function<String, Either<String, Integer>> reverseToEither = from -> left(reverse.apply(from));

  @Test public void isDefined() {
    assertThat(l.left().isDefined(), is(true));
  }

  @Test public void isNotDefined() {
    assertThat(r.left().isDefined(), is(false));
  }

  @Test public void isEmpty() {
    assertThat(r.left().isEmpty(), is(true));
  }

  @Test public void isNotEmpty() {
    assertThat(l.left().isEmpty(), is(false));
  }

  @Test public void either() {
    assertThat(l.left().either(), is(l));
    assertThat(r.left().either(), is(r));
  }

  @Test public void iteratorNotEmpty() {
    assertThat(l.left().iterator().next(), is("heyaa!"));
  }

  @Test public void iteratorEmpty() {
    assertThat(r.left().iterator().hasNext(), is(false));
  }

  @Test public void getOrNullDefined() {
    assertThat(l.left().getOrNull(), is("heyaa!"));
  }

  @Test public void getOrNullEmpty() {
    assertThat(r.left().getOrNull(), nullValue());
  }

  @Test public void getOrErrorDefined() {
    assertThat(l.left().getOrError(boo), is("heyaa!"));
  }

  @Test(expected = AssertionError.class) public void getOrErrorEmpty() {
    r.left().getOrError(boo);
  }

  @Test public void getOrElseDefined() {
    assertThat(l.left().getOrElse("foo"), is("heyaa!"));
  }

  @Test public void getOrElseEmpty() {
    assertThat(r.left().getOrElse("foo"), is("foo"));
  }

  @Test public void getOrElseSupplierDefined() {
    assertThat(l.left().getOr(boo), is("heyaa!"));
  }

  @Test public void getOrElseSupplierEmpty() {
    assertThat(r.left().getOr(boo), is("boo!"));
  }

  @Test public void existsDefinedTrue() {
    assertThat(l.left().exists(x -> true), is(true));
  }

  @Test public void existsDefinedFalse() {
    assertThat(l.left().exists(x -> false), is(false));
  }

  @Test public void existsNotDefinedTrue() {
    assertThat(r.left().exists(x -> true), is(false));
  }

  @Test public void existsNotDefinedFalse() {
    assertThat(r.left().exists(x -> false), is(false));
  }

  @Test public void forallDefinedTrue() {
    assertThat(l.left().forall(x -> true), is(true));
  }

  @Test public void forallDefinedFalse() {
    assertThat(l.left().forall(x -> false), is(false));
  }

  @Test public void forallNotDefinedTrue() {
    assertThat(r.left().forall(x -> true), is(true));
  }

  @Test public void forallNotDefinedFalse() {
    assertThat(r.left().forall(x -> false), is(true));
  }

  @Test public void foreachDefined() {
    assertThat(Count.countEach(l.left()), is(1));
  }

  @Test public void foreachNotDefined() {
    assertThat(Count.countEach(r.left()), is(0));
  }

  @Test public void onDefined() {
    assertThat(l.left().on(toStringFunction()), is("heyaa!"));
  }

  @Test public void onNotDefined() {
    assertThat(r.left().on(toStringFunction()), is("12"));
  }

  @Test public void mapDefined() {
    assertThat(l.left().map(reverse).left().get(), is("!aayeh"));
  }

  @Test public void mapNotDefined() {
    assertThat(r.left().map(reverse).right().get(), is(12));
  }

  @Test public void flatMapDefined() {
    assertThat(l.left().flatMap(reverseToEither).left().get(), is("!aayeh"));
  }

  @Test public void flatMapNotDefined() {
    assertThat(r.left().flatMap(reverseToEither).right().get(), is(12));
  }

  @Test public void sequenceDefined() {
    final Either<String, Integer> e = left("bar");
    assertThat(l.left().sequence(e).left().get(), is("bar"));
  }

  @Test public void sequenceNotDefined() {
    final Either<String, Integer> e = left("bar");
    assertThat(r.left().sequence(e).right().get(), is(12));
  }

  @Test public void filterDefinedTrue() {
    final Option<Either<String, Object>> filtered = l.left().filter(x -> true);
    assertThat(filtered.isDefined(), is(true));
    assertThat(filtered.get().left().isDefined(), is(true));
  }

  @Test public void filterDefinedFalse() {
    final Option<Either<String, Object>> filtered = l.left().filter(x -> false);
    assertThat(filtered.isDefined(), is(false));
  }

  @Test public void filterNotDefined() {
    final Option<Either<String, Object>> filtered = r.left().filter(x -> true);
    assertThat(filtered.isDefined(), is(false));
  }

  @Test public void filterOrElseWithUnsatisfiedHandlerDefinedTrue() {
    Either<String, Integer> filtered = l.left().filterOrElse(x -> true, () -> 300);
    assertThat(filtered, is(l));
  }

  @Test public void filterOrElseWithUnsatisfiedHandlerDefinedFalse() {
    Either<String, Integer> filtered = l.left().filterOrElse(x -> false, () -> 300);
    assertThat(filtered.left().isDefined(), is(false));
    assertThat(filtered.right().get(), is(300));
  }

  @Test public void filterOrElseWithUnsatisfiedHandlerNotDefined() {
    Either<String, Integer> filtered = r.left().filterOrElse(x -> false, () -> 300);
    assertThat(filtered, is(r));
  }

  @Test public void applyDefinedLeft() {
    final Either<Function<String, String>, Integer> func = left(reverse);
    assertThat(l.left().ap(func).left().get(), is("!aayeh"));
  }

  @Test public void applyDefinedRight() {
    final Either<Function<String, String>, Integer> func = right(36);
    assertThat(l.left().ap(func).right().get(), is(36));
  }

  @Test public void applyNotDefinedLeft() {
    final Either<Function<String, String>, Integer> func = left(reverse);
    assertThat(r.left().ap(func).right().get(), is(12));
  }

  @Test public void applyNotDefinedRight() {
    final Either<Function<String, String>, Integer> func = right(36);
    assertThat(r.left().ap(func).right().get(), is(36));
  }

  static class MyException extends Exception {
    private static final long serialVersionUID = -1056362494708225175L;
  }

  @Test public void getOrThrowLeft() throws MyException {
    assertThat(l.left().getOrThrow(MyException::new), is("heyaa!"));
  }

  @Test(expected = MyException.class) public void getOrThrowRight() throws MyException {
    r.left().getOrThrow(MyException::new);
  }
}
