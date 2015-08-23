package io.atlassian.fugue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class Serializer {
  static <A> byte[] toBytes(A a) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    new ObjectOutputStream(bytes).writeObject(a);
    return bytes.toByteArray();
  }

  static <A> A toObject(byte[] bs) throws IOException {
    ByteArrayInputStream bytes = new ByteArrayInputStream(bs);
    try {
      @SuppressWarnings("unchecked")
      A result = (A) new ObjectInputStream(bytes).readObject();
      return result;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  static class Unserializable {
    static Unserializable instance() {
      return new Unserializable();
    }
  }
}
