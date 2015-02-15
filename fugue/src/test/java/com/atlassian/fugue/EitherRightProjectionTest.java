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
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.UtilityFunctions.reverse;
import static com.atlassian.fugue.UtilityFunctions.toStringFunction;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.atlassian.fugue.mango.Predicates;
import org.junit.Test;

import java.util.function.Function;
import com.atlassian.fugue.mango.Function.Supplier;

public class EitherRightProjectionTest {
  private final Either<Integer, String> r = right("heyaa!");
  private final Either<Integer, String> l = left(12);
  final Supplier<String> boo = new Supplier.AbstractSupplier<String>() {
    @Override public String get() {
      return "boo!";
    }
  };
  static Function<String, Either<Integer, String>> reverseToEither = new Function<String, Either<Integer, String>>() {
    @Override public Either<Integer, String> apply(final String from) {
      return right(reverse.apply(from));
    }
  };

  @Test public void isDefined() {
    assertThat(r.right().isDefined(), is(true));
  }

  @Test public void isNotDefined() {
    assertThat(l.right().isDefined(), is(false));
  }

  @Test public void isEmpty() {
    assertThat(l.right().isEmpty(), is(true));
  }

  @Test public void isNotEmpty() {
    assertThat(r.right().isEmpty(), is(false));
  }

  @Test public void either() {
    assertThat(r.right().either(), is(r));
    assertThat(l.right().either(), is(l));
  }

  @Test public void iteratorNotEmpty() {
    assertThat(r.right().iterator().next(), is("heyaa!"));
  }

  @Test public void iteratorEmpty() {
    assertThat(l.right().iterator().hasNext(), is(false));
  }

  @Test public void getOrNullDefined() {
    assertThat(r.right().getOrNull(), is("heyaa!"));
  }

  @Test public void getOrNullEmpty() {
    assertThat(l.right().getOrNull(), nullValue());
  }

  @Test public void getOrErrorDefined() {
    assertThat(r.right().getOrError(boo), is("heyaa!"));
  }

  @Test(expected = AssertionError.class) public void getOrErrorEmpty() {
    l.right().getOrError(boo);
  }

  @Test public void getOrElseDefined() {
    assertThat(r.right().getOrElse("foo"), is("heyaa!"));
  }

  @Test public void getOrElseEmpty() {
    assertThat(l.right().getOrElse("foo"), is("foo"));
  }

  @Test public void getOrElseSupplierDefined() {
    assertThat(r.right().getOrElse(boo), is("heyaa!"));
  }

  @Test public void getOrElseSupplierEmpty() {
    assertThat(l.right().getOrElse(boo), is("boo!"));
  }

  @Test public void existsDefinedTrue() {
    assertThat(r.right().exists(Predicates.<String> alwaysTrue()), is(true));
  }

  @Test public void existsDefinedFalse() {
    assertThat(r.right().exists(Predicates.<String> alwaysFalse()), is(false));
  }

  @Test public void existsNotDefinedTrue() {
    assertThat(l.right().exists(Predicates.<String> alwaysTrue()), is(false));
  }

  @Test public void existsNotDefinedFalse() {
    assertThat(l.right().exists(Predicates.<String> alwaysFalse()), is(false));
  }

  @Test public void forallDefinedTrue() {
    assertThat(r.right().forall(Predicates.<String> alwaysTrue()), is(true));
  }

  @Test public void forallDefinedFalse() {
    assertThat(r.right().forall(Predicates.<String> alwaysFalse()), is(false));
  }

  @Test public void forallNotDefinedTrue() {
    assertThat(l.right().forall(Predicates.<String> alwaysTrue()), is(true));
  }

  @Test public void forallNotDefinedFalse() {
    assertThat(l.right().forall(Predicates.<String> alwaysFalse()), is(true));
  }

  @Test public void foreachDefined() {
    assertThat(Count.countEach(r.right()), is(1));
  }

  @Test public void foreachNotDefined() {
    assertThat(Count.countEach(l.right()), is(0));
  }

  @Test public void onDefined() {
    assertThat(r.right().on(toStringFunction()), is("heyaa!"));
  }

  @Test public void onNotDefined() {
    assertThat(l.right().on(toStringFunction()), is("12"));
  }

  @Test public void mapDefined() {
    assertThat(r.right().map(reverse).right().get(), is("!aayeh"));
  }

  @Test public void mapNotDefined() {
    assertThat(l.right().map(reverse).left().get(), is(12));
  }

  @Test public void flatMapDefined() {
    assertThat(r.right().flatMap(reverseToEither).right().get(), is("!aayeh"));
  }

  @Test public void flatMapNotDefined() {
    assertThat(l.right().flatMap(reverseToEither).left().get(), is(12));
  }

  @Test public void sequenceDefined() {
    final Either<Integer, String> e = right("bar");
    assertThat(r.right().sequence(e).right().get(), is("bar"));
  }

  @Test public void sequenceNotDefined() {
    final Either<Integer, String> e = right("bar");
    assertThat(l.right().sequence(e).left().get(), is(12));
  }

  @Test public void filterDefinedTrue() {
    final Option<Either<Integer, String>> filtered = r.right().filter(Predicates.<String> alwaysTrue());
    assertThat(filtered.isDefined(), is(true));
    assertThat(filtered.get().right().isDefined(), is(true));
  }

  @Test public void filterDefinedFalse() {
    final Option<Either<Integer, String>> filtered = r.right().filter(Predicates.<String> alwaysFalse());
    assertThat(filtered.isDefined(), is(false));
  }

  @Test public void filterNotDefined() {
    final Option<Either<Integer, String>> filtered = l.right().filter(Predicates.<String> alwaysTrue());
    assertThat(filtered.isDefined(), is(false));
  }

  @Test public void applyDefinedRight() {
    final Either<Integer, Function<String, String>> func = right(reverse);
    assertThat(r.right().apply(func).right().get(), is("!aayeh"));
  }

  @Test public void applyDefinedLeft() {
    final Either<Integer, Function<String, String>> func = left(36);
    assertThat(r.right().apply(func).left().get(), is(36));
  }

  @Test public void applyNotDefinedRight() {
    final Either<Integer, Function<String, String>> func = right(reverse);
    assertThat(l.right().apply(func).left().get(), is(12));
  }

  @Test public void applyNotDefinedLeft() {
    final Either<Integer, Function<String, String>> func = left(36);
    assertThat(l.right().apply(func).left().get(), is(36));
  }

  static class MyException extends Exception {
    private static final long serialVersionUID = -1056362494708225175L;
  }

  @Test public void getOrThrowRight() throws MyException {
    assertThat(r.right().getOrThrow(new Supplier.AbstractSupplier<MyException>() {
      @Override public MyException get() {
        return new MyException();
      }
    }), is("heyaa!"));
  }

  @Test(expected = MyException.class) public void getOrThrowLeft() throws MyException {
    l.right().getOrThrow(new Supplier.AbstractSupplier<MyException>() {
      @Override public MyException get() {
        return new MyException();
      }
    });
  }
}
