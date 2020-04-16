package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.io.NotSerializableException;

import static io.atlassian.fugue.Serializer.toBytes;
import static io.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PairSerializationTest {
  @Test public void serialize() throws IOException {
    final Pair<Integer, Integer> pair = Pair.pair(1, 2);
    assertThat(Serializer.<Pair<Integer, Integer>> toObject(toBytes(pair)), equalTo(pair));
  }

  @Test(expected = NotSerializableException.class) public void serializeWithNonSerializableLeft() throws IOException {
    toObject(toBytes(Pair.pair(Serializer.Unserializable.instance(), 2)));
  }

  @Test(expected = NotSerializableException.class) public void serializeWithNonSerializableRight() throws IOException {
    toObject(toBytes(Pair.pair(1, Serializer.Unserializable.instance())));
  }
}
