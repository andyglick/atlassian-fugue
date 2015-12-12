package io.atlassian.fugue.optic.std;

import io.atlassian.fugue.Pair;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.Lens;
import io.atlassian.fugue.optic.PLens;

import java.util.AbstractMap;
import java.util.Map;

import static io.atlassian.fugue.Pair.pair;
import static io.atlassian.fugue.optic.PLens.pLens;

public final class PairOptics {

  private PairOptics() {}

  public static <A, B, C> PLens<Pair<A, B>, Pair<C, B>, A, C> pLeft() {
    return pLens(Pair::left, c -> ab -> pair(c, ab.right()));
  }

  public static <A, B> Lens<Pair<A, B>, A> left() {
    return new Lens<>(pLeft());
  }

  public static <A, B, C> PLens<Pair<A, B>, Pair<A, C>, B, C> pRight() {
    return pLens(Pair::right, c -> ab -> pair(ab.left(), c));
  }

  public static <A, B> Lens<Pair<A, B>, B> _right() {
    return new Lens<>(pRight());
  }

  public static <A, B> Iso<Pair<A, B>, Map.Entry<A, B>> pairToEntry() {
    return Iso.iso(p -> new AbstractMap.SimpleImmutableEntry<>(p.left(), p.right()), e -> pair(e.getKey(), e.getValue()));
  }
}
