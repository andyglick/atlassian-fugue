package io.atlassian.fugue;

import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApplicativeFunction {

  final Function<Integer, String> f = Object::toString;
  final Function<Integer, Function<String, Boolean>> g = i -> s -> {
    if(i == 1) {
      return s.length() == 1;
    } else {
      return s.startsWith("2");
    }
  };

  @Test
  public void functionApplication(){
    assertThat(Functions.ap(f,g).apply(1), is(true));
  }

  @Test
  public void functionApplicationSwitching(){
    assertThat(Functions.ap(f,g).apply(2), is(true));
  }

  @Test(expected = NullPointerException.class)
  public void functionApplicationNullContext(){
    Functions.ap(f,null).apply(2);
  }

  @Test(expected = NullPointerException.class)
  public void functionApplicationNullStart(){
    Functions.ap(null,g).apply(2);
  }

  @Test(expected = NullPointerException.class)
  public void functionApplicationAllNull(){
    final Function<Integer, String> f = null;
    final Function<Integer, Function<String, Boolean>> g = null;
    Functions.ap(f,g).apply(2);
  }
}