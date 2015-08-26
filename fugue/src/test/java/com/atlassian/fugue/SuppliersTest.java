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

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SuppliersTest {

  @Test public void ofInstance() {
    final Integer instance = 29;
    final Supplier<Integer> integerSupplier = Suppliers.ofInstance(instance);

    assertThat(integerSupplier.get(), is(29));

    // do it a few more times to ensure that it keeps returning what we want.
    assertThat(integerSupplier.get(), is(29));
    assertThat(integerSupplier.get(), is(29));
  }

  @Test public void alwaysTrue() {
    final Supplier<Boolean> alwaysTrue = Suppliers.alwaysTrue();

    assertThat(alwaysTrue.get(), is(true));
    assertThat(alwaysTrue.get(), is(true));
    assertThat(alwaysTrue.get(), is(true));
  }

  @Test public void alwaysFalse() {
    final Supplier<Boolean> alwaysFalse = Suppliers.alwaysFalse();

    assertThat(alwaysFalse.get(), is(false));
    assertThat(alwaysFalse.get(), is(false));
    assertThat(alwaysFalse.get(), is(false));
  }

  @Test public void alwaysNull() {
    final Supplier<Object> alwaysNull = Suppliers.alwaysNull();

    final Object nully = null;
    assertThat(alwaysNull.get(), is(nully));
    assertThat(alwaysNull.get(), is(nully));
    assertThat(alwaysNull.get(), is(nully));
  }

  @Test public void fromOptionCallsSome() {
    assertThat(Suppliers.fromOption(Option.some("test")).get(), is("test"));
  }

  @Test(expected = NoSuchElementException.class) public void fromOptionNoneThrows() {
    Suppliers.fromOption(Option.none()).get();
  }

  @Test public void fromFunctionInt() {
    assertThat(Suppliers.fromFunction(UtilityFunctions.square, 4).get(), is(16));
  }

  @Test public void fromFunctionString() {
    assertThat(Suppliers.fromFunction(UtilityFunctions.reverse, "collywobble").get(), is("elbbowylloc"));
  }

  @Test public void memoize() {
    AtomicInteger intRef = new AtomicInteger(1);
    Supplier<Integer> memoize = Suppliers.memoize(() -> intRef.get());

    assertThat(memoize.get(), is(1));
    intRef.set(2);
    assertThat(memoize.get(), is(1));
  }

  @Test public void weakMemoize() {
    AtomicInteger intRef = new AtomicInteger(Integer.valueOf(1));
    Supplier<Integer> weakMemoized = Suppliers.weakMemoize(() -> intRef.get());

    assertThat(weakMemoized.get(), is(1));
    intRef.set(Integer.valueOf(2));
    assertThat(weakMemoized.get(), is(1));
  }

}
