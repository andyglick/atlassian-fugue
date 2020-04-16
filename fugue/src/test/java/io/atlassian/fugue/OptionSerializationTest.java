package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Base64;

import static io.atlassian.fugue.Serializer.toBytes;
import static io.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class OptionSerializationTest {
  @Test public void serializeSome() throws IOException {
    final Option<Integer> opt = Option.some(1);
    assertThat(Serializer.<Option<Integer>> toObject(toBytes(opt)), equalTo(opt));
  }

  @Test public void serializeNone() throws IOException {
    final Option<Integer> opt = Option.none();
    assertThat(Serializer.<Option<Integer>> toObject(toBytes(opt)), equalTo(opt));
  }

  @Test(expected = NotSerializableException.class) public void serializeSomeNonSerializable() throws IOException {
    toObject(toBytes(Option.some(Serializer.Unserializable.instance())));
  }

  @Test public void deserializeAnonymousNoneReadResolve() throws IOException {
    final byte[] serialized = Base64.getDecoder().decode(
      "rO0ABXNyABtpby5hdGxhc3NpYW4uZnVndWUuT3B0aW9uJDHki4wrMT+ZGgIAAHhyABlpby5hdGxhc3NpYW4uZnVndWUuT3B0aW9ubO2YatZzMVECAAB4cA==");
    assertThat(toObject(serialized), sameInstance(Option.none()));
  }
}
