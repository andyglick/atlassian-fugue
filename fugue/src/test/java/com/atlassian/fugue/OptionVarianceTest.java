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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.atlassian.fugue.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OptionVarianceTest {

  class Grand {}

  class Parent extends Grand {}

  class Child extends Parent {}

  @Test public void flatMap() {
    final Option<Parent> some = some(new Parent());
    final Function<Grand, Option<Child>> f = p -> some(new Child());
    final Option<Parent> mapped = some.<Parent> flatMap(f);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void map() {
    final Option<Parent> some = some(new Parent());
    final Function<Grand, Child> f = p -> new Child();
    final Option<Parent> mapped = some.<Parent> map(f);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void orElse() {
    final Option<Parent> some = some(new Parent());
    final Supplier<Option<Child>> f = () -> some(new Child());
    final Option<Parent> mapped = some.orElse(f);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void or() {
    final Option<Parent> some = some(new Parent());
    final Option<Child> opt = some(new Child());
    final Option<Parent> mapped = some.orElse(opt);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void getOrElseSupplier() {
    final Option<Parent> some = some(new Parent());
    final Supplier<Child> f = Child::new;
    final Parent mapped = some.getOr(f);
    assertThat(mapped, notNullValue());
  }

  @Test public void getOrElse() {
    final Option<Parent> some = some(new Parent());
    final Option<Child> opt = some(new Child());
    final Option<Parent> mapped = some.orElse(opt);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void forAll() {
    final Option<Parent> some = some(new Parent());
    final Predicate<Grand> alwaysTrue = x -> true;
    assertThat(some.forall(alwaysTrue), equalTo(true));
  }

  @Test public void exist() {
    final Option<Parent> some = some(new Parent());
    final Predicate<Grand> alwaysTrue = x -> true;
    assertThat(some.exists(alwaysTrue), equalTo(true));
  }

  @Test public void forEach() {
    final Maybe<Child> some = some(new Child());
    Count<Grand> e = new Count<>();
    some.foreach(e);
    assertThat(e.count(), equalTo(1));
  }
}
