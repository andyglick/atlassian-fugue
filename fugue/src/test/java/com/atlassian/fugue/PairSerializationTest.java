package com.atlassian.fugue;

import static com.atlassian.fugue.Serializer.toBytes;
import static com.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.NotSerializableException;

import org.junit.Test;

public class PairSerializationTest {
  @Test public void serialize() throws IOException {
    Pair<Integer, Integer> pair = Pair.pair(1, 2);
    assertThat(Serializer.<Pair<Integer, Integer>>toObject(toBytes(pair)), equalTo(pair));
  }

  @Test(expected = NotSerializableException.class) public void serializeWithNonSerializableLeft() throws IOException {
    toObject(toBytes(Pair.pair(Serializer.Unserializable.instance(), 2)));
  }

  @Test(expected = NotSerializableException.class) public void serializeWithNonSerializableRight() throws IOException {
    toObject(toBytes(Pair.pair(1, Serializer.Unserializable.instance())));
  }
}
