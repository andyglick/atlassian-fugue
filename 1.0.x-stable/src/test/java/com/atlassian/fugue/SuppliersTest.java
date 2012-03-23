package com.atlassian.fugue;


import com.google.common.base.Supplier;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SuppliersTest {
  
  @Test public void ofInstance() {
    Integer instance = 29;
    Supplier<Integer> integerSupplier = Suppliers.ofInstance(instance);
    
    assertThat(integerSupplier.get(), is(29));
    
    // do it a few more times to ensure that it keeps returning what we want.
    assertThat(integerSupplier.get(), is(29));
    assertThat(integerSupplier.get(), is(29));
  }

  @Test public void alwaysTrue() {
    Supplier<Boolean> alwaysTrue = Suppliers.alwaysTrue();
    
    assertThat(alwaysTrue.get(), is(true));
    assertThat(alwaysTrue.get(), is(true));
    assertThat(alwaysTrue.get(), is(true));
  }
  
  @Test public void alwaysFalse() {
    Supplier<Boolean> alwaysFalse = Suppliers.alwaysFalse();
    
    assertThat(alwaysFalse.get(), is(false));
    assertThat(alwaysFalse.get(), is(false));
    assertThat(alwaysFalse.get(), is(false));
  }
  
  @Test public void alwaysNull() {
    Supplier<Object> alwaysNull = Suppliers.alwaysNull();
    
    Object nully = null;
    assertThat(alwaysNull.get(), is(nully));
    assertThat(alwaysNull.get(), is(nully));
    assertThat(alwaysNull.get(), is(nully));
  }
  
  
}
