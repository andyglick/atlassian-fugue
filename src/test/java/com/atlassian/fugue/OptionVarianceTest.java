package com.atlassian.fugue;

import static com.atlassian.fugue.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.google.common.base.Function;
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
}
