package com.atlassian.fugue;

import static com.atlassian.fugue.Serializer.toBytes;
import static com.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.NotSerializableException;

import org.junit.Test;

public class OptionSerializationTest {
  @Test public void serializeSome() throws IOException {
    Option<Integer> opt = Option.some(1);
    assertThat(Serializer.<Option<Integer>> toObject(toBytes(opt)), equalTo(opt));
  }

  @Test public void serializeNone() throws IOException {
    Option<Integer> opt = Option.none();
    assertThat(Serializer.<Option<Integer>> toObject(toBytes(opt)), equalTo(opt));
  }

  @Test(expected = NotSerializableException.class) public void serializeSomeNonSerializable() throws IOException {
    toObject(toBytes(Option.some(Serializer.Unserializable.instance())));
  }
}
