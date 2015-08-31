/*
   Copyright 2015 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.atlassian.fugue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Iterables.*;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Options.filterNone;
import static com.atlassian.fugue.Unit.Unit;
import static java.util.Collections.emptyList;

/**
 * {@link Monoid} instances.
 *
 * @see Semigroups
 * @since 3.0
 */
public final class Monoids {

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAddition = new Monoid<Integer>() {
    @Override public Integer append(Integer i1, Integer i2) {
      return i1 + i2;
    }

    @Override public Integer zero() {
      return 0;
    }

    @Override public Integer multiply(int n, Integer i) {
      return n <= 0 ? 0 : n * i;
    }
  };

  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplication = new Monoid<Integer>() {
    @Override public Integer append(Integer i1, Integer i2) {
      return i1 * i2;
    }

    @Override public Integer zero() {
      return 1;
    }
  };

  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAddition = new Monoid<BigInteger>() {
    @Override public BigInteger append(BigInteger a1, BigInteger a2) {
      return a1.add(a2);
    }

    @Override public BigInteger zero() {
      return BigInteger.ZERO;
    }

    @Override public BigInteger multiply(int n, BigInteger b) {
      return n <= 0 ? BigInteger.ZERO : b.multiply(BigInteger.valueOf(n));
    }
  };

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplication = new Monoid<BigInteger>() {
    @Override public BigInteger append(BigInteger a1, BigInteger a2) {
      return a1.multiply(a2);
    }

    @Override public BigInteger zero() {
      return BigInteger.ONE;
    }

    @Override public BigInteger multiply(int n, BigInteger b) {
      return n <= 0 ? BigInteger.ONE : b.pow(n);
    }
  };

  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAddition = new Monoid<Long>() {
    @Override public Long append(Long a1, Long a2) {
      return a1 + a2;
    }

    @Override public Long zero() {
      return 0L;
    }

    @Override public Long multiply(int n, Long l) {
      return n <= 0 ? 0L : l * n;
    }
  };

  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplication = new Monoid<Long>() {
    @Override public Long append(Long a1, Long a2) {
      return a1 * a2;
    }

    @Override public Long zero() {
      return 1L;
    }
  };

  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunction = new Monoid<Boolean>() {
    @Override public Boolean append(Boolean a1, Boolean a2) {
      return a1 || a2;
    }

    @Override public Boolean zero() {
      return false;
    }

    @Override public Boolean sum(Iterable<Boolean> bs) {
      return Iterables.filter(bs, b -> b).iterator().hasNext();
    }

    @Override public Boolean multiply(int n, Boolean b) {
      return n <= 0 ? false : b;
    }
  };

  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunction = new Monoid<Boolean>() {
    @Override public Boolean append(Boolean a1, Boolean a2) {
      return (a1 ^ a2);
    }

    @Override public Boolean zero() {
      return false;
    }

    @Override public Boolean multiply(int n, Boolean b) {
      return b && (n == 1);
    }
  };

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunction = new Monoid<Boolean>() {
    @Override public Boolean append(Boolean a1, Boolean a2) {
      return a1 && a2;
    }

    @Override public Boolean zero() {
      return true;
    }

    @Override public Boolean sum(Iterable<Boolean> bs) {
      return !Iterables.filter(bs, b -> !b).iterator().hasNext();
    }
  };

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> string = new Monoid<String>() {
    @Override public String append(String a1, String a2) {
      return a1.concat(a2);
    }

    @Override public String zero() {
      return "";
    }

    @Override public String sum(Iterable<String> ss) {
      StringBuilder sb = new StringBuilder();
      for (String s : ss) {
        sb.append(s);
      }
      return sb.toString();
    }
  };

  /**
   * A monoid for the Unit value.
   */
  public static final Monoid<Unit> unit = new Monoid<Unit>() {
    @Override public Unit append(Unit a1, Unit a2) {
      return Unit();
    }

    @Override public Unit zero() {
      return Unit();
    }

    @Override public Unit multiply(int n, Unit unit) {
      return Unit();
    }

    @Override public Unit multiply1p(int n, Unit unit) {
      return Unit();
    }
  };

  private Monoids() {
  }

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<Function<A, B>> function(final Monoid<B> mb) {
    return new Monoid<Function<A, B>>() {
      @Override public Function<A, B> append(Function<A, B> a1, Function<A, B> a2) {
        return a -> mb.append(a1.apply(a), a2.apply(a));
      }

      @Override public Function<A, B> zero() {
        return a -> mb.zero();
      }

      @Override public Function<A, B> sum(Iterable<Function<A, B>> fs) {
        return a -> mb.sum(map(fs, Functions.<A, B>apply(a)));
      }

      @Override public Function<A, B> multiply(int n, Function<A, B> f) {
        return a -> mb.multiply(n, f.apply(a));
      }
    };
  }

  /**
   * A monoid for lists.
   *
   * @return A monoid for lists.
   */
  public static <A> Monoid<List<A>> list() {
    return new Monoid<List<A>>() {
      @Override public List<A> append(List<A> l1, List<A> l2) {
        final List<A> sumList;
        if (l1.isEmpty()) {
          sumList = l2;

        } else if (l2.isEmpty()) {
          sumList = l1;

        } else {
          sumList = new ArrayList<>(l1.size() + l2.size());
          sumList.addAll(l1);
          sumList.addAll(l2);
        }
        return sumList;
      }

      @Override public List<A> zero() {
        return emptyList();
      }
    };
  }

  /**
   * A monoid for iterables.
   *
   * @return A monoid for iterables.
   */
  public static <A> Monoid<Iterable<A>> iterable() {
    return new Monoid<Iterable<A>>() {
      @Override public Iterable<A> append(Iterable<A> a1, Iterable<A> a2) {
        return concat(a1, a2);
      }

      @Override public Iterable<A> zero() {
        return emptyList();
      }

      @Override public Iterable<A> sum(Iterable<Iterable<A>> iterable) {
        return join(iterable);
      }
    };
  }

  /**
   * A monoid for options (that take the first available value).
   *
   * @return A monoid for options (that take the first available value).
   */
  public static <A> Monoid<Option<A>> firstOption() {
    return new Monoid<Option<A>>() {
      @Override public Option<A> append(Option<A> a1, Option<A> a2) {
        return a1.isDefined() ? a1 : a2;
      }

      @Override public Option<A> zero() {
        return none();
      }

      @Override public Option<A> sum(Iterable<Option<A>> os) {
        return first(filterNone(os)).getOrElse(none());
      }
    };
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOption() {
    return new Monoid<Option<A>>() {
      @Override public Option<A> append(Option<A> a1, Option<A> a2) {
        return a2.isDefined() ? a2 : a1;
      }

      @Override public Option<A> zero() {
        return none();
      }
    };
  }

  /**
   * A monoid for options that combine inner value with a semigroup.
   *
   * @return A monoid for options that combine inner value with a semigroup.
   */
  public static <A> Monoid<Option<A>> option(Semigroup<A> semigroup) {
    return new Monoid<Option<A>>() {
      @Override public Option<A> append(Option<A> o1, Option<A> o2) {
        return o1.fold(() -> o2, a1 -> o2.fold(() -> o1, a2 -> some(semigroup.append(a1, a2))));
      }

      @Override public Option<A> zero() {
        return none();
      }

      @Override public Option<A> sum(Iterable<Option<A>> os) {
        Iterable<A> memoized = Iterables.memoize(Options.flatten(os));
        return first(memoized).fold(() -> Option.<A>none(), a -> some(semigroup.sumNel(a, drop(1, memoized))));
      }

      @Override public Option<A> multiply(int n, Option<A> as) {
        return n <= 0 ? none() : as.fold(() -> none(), a -> some(semigroup.multiply1p(n - 1, a)));
      }
    };
  }

  /**
   * A monoid Sums up values inside either {@see Semigroups#either}.
   * Monoid of right values provide the identity element of the resulting monoid.
   *
   * @param lS semigroup for left values
   * @param rM monoid for right values.
   * @return A monoid Sums up values inside either.
   */
  public static <L, R> Monoid<Either<L, R>> either(Semigroup<L> lS, Monoid<R> rM) {
    Either<L, R> zero = right(rM.zero());
    return new Monoid<Either<L, R>>() {

      @Override public Either<L, R> append(Either<L, R> e1, Either<L, R> e2) {
        return e1.<Either<L, R>>fold(l1 -> e2.<Either<L, R>>fold(l2 -> left(lS.append(l1, l2)), r2 -> e1),
          r1 -> e2.<Either<L, R>>fold(l2 -> e2, r2 -> right(rM.append(r1, r2))));
      }

      @Override public Either<L, R> zero() {
        return zero;
      }
    };
  }

}
