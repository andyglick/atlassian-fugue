package io.atlassian.fugue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static io.atlassian.fugue.Iterables.concat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.core.Is.is;

public class IterablesConcatTest {

  @Test public void concatTwo() {
    assertThat(concat(Arrays.asList(1, 2), Arrays.asList(3, 4)), contains(1, 2, 3, 4));
  }

  @Test public void concatOne() {
    assertThat(concat(Arrays.asList(1, 2)), contains(1, 2));
  }

  @Test public void concatOneEmpty() {
    assertThat(concat(Arrays.asList(1, 2), Collections.emptyList()), contains(1, 2));
  }

  @Test public void concatEmptyWithOne() {
    assertThat(concat(Collections.emptyList(), Arrays.asList(1, 2)), contains(1, 2));
  }

  @Test public void concatEmpty() {
    assertThat(concat(Collections.emptyList(), Collections.emptyList()), emptyIterable());
  }

  @Test public void concatNothing() {
    assertThat(concat(), emptyIterable());
  }

  @Test(expected = NullPointerException.class) public void concatNullEmpty() {
    assertThat(concat(null, Collections.emptyList()), emptyIterable());
  }

  @Test(expected = NullPointerException.class) public void concatEmptyNull() {
    assertThat(concat(Collections.emptyList(), null), emptyIterable());
  }

  @Test public void concatToString() {
    assertThat(concat(Arrays.asList(1, 2)).toString(), is("[1, 2]"));
  }

}
