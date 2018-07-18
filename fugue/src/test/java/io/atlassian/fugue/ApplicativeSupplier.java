package io.atlassian.fugue;

import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApplicativeSupplier {

  final Supplier<String> f = () -> "input";
  final Supplier<Function<String, Integer>> g = () -> String::length;

  @Test public void supplierApplication() {
    assertThat(Suppliers.ap(f, g).get(), is(5));
  }

  @Test(expected = NullPointerException.class) public void supplierApplicationNullInput() {
    Suppliers.ap(null, g).get();
  }

  @Test(expected = NullPointerException.class) public void supplierApplicationNullTransform() {
    Suppliers.ap(f, null).get();
  }

  @Test(expected = NullPointerException.class) public void supplierApplicationAllNull() {
    final Supplier<String> f = null;
    final Supplier<Function<String, Integer>> g = null;
    Suppliers.ap(f, g).get();
  }
}