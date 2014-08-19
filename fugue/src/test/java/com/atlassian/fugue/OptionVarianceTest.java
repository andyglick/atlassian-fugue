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

import static com.atlassian.fugue.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;

public class OptionVarianceTest {

  class Grand {};

  class Parent extends Grand {};

  class Child extends Parent {};

  @Test public void flatMap() {
    Option<Parent> some = some(new Parent());
    Function<Grand, Option<Child>> f = new Function<Grand, Option<Child>>() {
      @Override public Option<Child> apply(Grand p) {
        return some(new Child());
      }
    };
    Option<Parent> mapped = some.<Parent> flatMap(f);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void map() {
    Option<Parent> some = some(new Parent());
    Function<Grand, Child> f = new Function<Grand, Child>() {
      @Override public Child apply(Grand p) {
        return new Child();
      }
    };
    Option<Parent> mapped = some.<Parent> map(f);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void orElse() {
    Option<Parent> some = some(new Parent());
    Supplier<Option<Child>> f = new Supplier<Option<Child>>() {
      @Override public Option<Child> get() {
        return some(new Child());
      }
    };
    Option<Parent> mapped = some.orElse(f);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void or() {
    Option<Parent> some = some(new Parent());
    Option<Child> opt = some(new Child());
    Option<Parent> mapped = some.orElse(opt);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void getOrElseSupplier() {
    Option<Parent> some = some(new Parent());
    Supplier<Child> f = new Supplier<Child>() {
      @Override public Child get() {
        return new Child();
      }
    };
    Parent mapped = some.getOrElse(f);
    assertThat(mapped, notNullValue());
  }

  @Test public void getOrElse() {
    Option<Parent> some = some(new Parent());
    Option<Child> opt = some(new Child());
    Option<Parent> mapped = some.orElse(opt);
    assertThat(mapped.get(), notNullValue());
  }

  @Test public void forAll() {
    Option<Parent> some = some(new Parent());
    Predicate<Grand> p = Predicates.alwaysTrue();
    assertThat(some.forall(p), equalTo(true));
  }

  @Test public void exist() {
    Option<Parent> some = some(new Parent());
    Predicate<Grand> p = Predicates.alwaysTrue();
    assertThat(some.exists(p), equalTo(true));
  }

  @Test public void forEach() {
    Maybe<Child> some = some(new Child());
    Count<Grand> e = new Count<Grand>();
    some.foreach(e);
    assertThat(e.count(), equalTo(1));
  }
}
