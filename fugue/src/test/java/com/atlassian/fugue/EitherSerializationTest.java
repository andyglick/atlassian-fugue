package com.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.io.NotSerializableException;

import static com.atlassian.fugue.Serializer.toBytes;
import static com.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EitherSerializationTest {
  @Test public void serializeLeft() throws IOException {
    final Either<Integer, String> e = Either.left(1);
    assertThat(Serializer.<Either<Integer, String>> toObject(toBytes(e)), equalTo(e));
  }

  @Test public void serializeRight() throws IOException {
    final Either<String, Integer> e = Either.right(1);
    assertThat(Serializer.<Either<String, Integer>> toObject(toBytes(e)), equalTo(e));
  }

  @Test(expected = NotSerializableException.class) public void serializeLeftNonSerializable() throws IOException {
    toObject(toBytes(Either.left(Serializer.Unserializable.instance())));
  }

  @Test(expected = NotSerializableException.class) public void serializeRightNonSerializable() throws IOException {
    toObject(toBytes(Either.right(Serializer.Unserializable.instance())));
  }
}
