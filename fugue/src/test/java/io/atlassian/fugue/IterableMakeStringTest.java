package io.atlassian.fugue;

import org.junit.Test;

import java.util.Arrays;

import static io.atlassian.fugue.Iterables.makeString;
import static io.atlassian.fugue.Iterables.rangeUntil;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IterableMakeStringTest {

  @Test public void makeSimpleStringNoLimit() {
    assertThat(makeString(Arrays.asList(1, 2, 3), "[", ",", "]"), is("[1,2,3]"));
  }

  @Test public void makeSimpleString() {
    assertThat(makeString(Arrays.asList(1, 2, 3), "[", ",", "]", 100), is("[1,2,3]"));
  }

  @Test public void makeSimpleStringNoLimitCropped() {
    assertThat(makeString(rangeUntil(0, 100), "[", ",", "]"),
      is("[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36...]"));
  }

  @Test public void makeSimpleStringCropped() {
    assertThat(makeString(Arrays.asList(1, 22, 3), "[", ",", "]", 4), is("[1,22...]"));
  }

  @Test public void makeSimpleStringOneExtra() {
    assertThat(makeString(Arrays.asList(1, 2, 3), "[", ",", "]", 4), is("[1,2...]"));
  }

  @Test public void makeSimpleStringExactLength() {
    assertThat(makeString(Arrays.asList(1, 2, 3), "[", ",", "]", 5), is("[1,2,3]"));
  }
}
