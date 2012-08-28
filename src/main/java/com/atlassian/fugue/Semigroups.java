// these have been borrowed with kind permission from the FJ library.
package com.atlassian.fugue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Some standard Semigroup instances.
 * 
 * @since 1.2
 */
public class Semigroups {
  private Semigroups() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  /**
   * Constructs a semigroup from the given function.
   * 
   * @param sum The function to construct this semigroup with.
   * @return A semigroup from the given function.
   */
  public static <A> Semigroup<A> semigroup(final Function2<A, A, A> sum) {
    return new Semigroup<A>() {
      @Override
      public A append(A a, A b) {
        return sum.apply(a, b);
      };
    };
  }

  /**
   * Constructs a semigroup from the given function.
   * 
   * @param sum The function to construct this semigroup with.
   * @return A semigroup from the given function.
   */
  public static <A> Semigroup<A> semigroup(final Function<A, Function<A, A>> sum) {
    return new Semigroup<A>() {
      @Override
      public A append(A a, A b) {
        return sum.apply(a).apply(b);
      };
    };
  }

  /**
   * Returns a function that sums the given value according to the semigroup.
   */
  public static <A> Function<A, A> sum(final A a1, final Semigroup<A> semi) {
    return new Function<A, A>() {
      @Override
      public A apply(A a2) {
        return semi.append(a1, a2);
      }
    };
  }

  /**
   * Returns a function that sums according to the semigroup.
   */
  public static <A> Function<A, Function<A, A>> sum(final Semigroup<A> semi) {
    return new Function<A, Function<A, A>>() {
      @Override
      public Function<A, A> apply(A from) {
        return sum(from, semi);
      }
    };
  }

  /**
   * A semigroup that adds integers.
   */
  public static final Semigroup<Integer> intAdditionSemigroup = new Semigroup<Integer>() {
    public Integer append(final Integer i1, final Integer i2) {
      return i1 + i2;
    }
  };

  /**
   * A semigroup that adds doubles.
   */
  public static final Semigroup<Double> doubleAdditionSemigroup = new Semigroup<Double>() {
    public Double append(final Double d1, final Double d2) {
      return d1 + d2;
    }
  };

  /**
   * A semigroup that multiplies integers.
   */
  public static final Semigroup<Integer> intMultiplicationSemigroup = new Semigroup<Integer>() {
    public Integer append(final Integer i1, final Integer i2) {
      return i1 * i2;
    }
  };

  /**
   * A semigroup that multiplies doubles.
   */
  public static final Semigroup<Double> doubleMultiplicationSemigroup = new Semigroup<Double>() {
    public Double append(final Double d1, final Double d2) {
      return d1 * d2;
    }
  };

  /**
   * A semigroup that yields the maximum of integers.
   */
  // public static final Semigroup<Integer> intMaximumSemigroup =
  // semigroup(Ord.intOrd.max);

  /**
   * A semigroup that yields the minimum of integers.
   */
  // public static final Semigroup<Integer> intMinimumSemigroup =
  // semigroup(Ord.intOrd.min);

  /**
   * A semigroup that adds big integers.
   */
  public static final Semigroup<BigInteger> bigintAdditionSemigroup = new Semigroup<BigInteger>() {
    public BigInteger append(final BigInteger i1, final BigInteger i2) {
      return i1.add(i2);
    }
  };

  /**
   * A semigroup that multiplies big integers.
   */
  public static final Semigroup<BigInteger> bigintMultiplicationSemigroup = new Semigroup<BigInteger>() {
    public BigInteger append(final BigInteger i1, final BigInteger i2) {
      return i1.multiply(i2);
    }
  };

  /**
   * A semigroup that yields the maximum of big integers.
   */
  // public static final Semigroup<BigInteger> bigintMaximumSemigroup =
  // semigroup(Ord.bigintOrd.max);

  /**
   * A semigroup that yields the minimum of big integers.
   */
  // public static final Semigroup<BigInteger> bigintMinimumSemigroup =
  // semigroup(Ord.bigintOrd.min);

  /**
   * A semigroup that adds big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalAdditionSemigroup = new Semigroup<BigDecimal>() {
    public BigDecimal append(final BigDecimal i1, final BigDecimal i2) {
      return i1.add(i2);
    }
  };

  /**
   * A semigroup that multiplies big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalMultiplicationSemigroup = new Semigroup<BigDecimal>() {
    public BigDecimal append(final BigDecimal i1, final BigDecimal i2) {
      return i1.multiply(i2);
    }
  };

  /**
   * A semigroup that yields the maximum of big decimals.
   */
  // public static final Semigroup<BigDecimal> bigDecimalMaximumSemigroup =
  // semigroup(Ord.bigdecimalOrd.max);

  /**
   * A semigroup that yields the minimum of big decimals.
   */
  // public static final Semigroup<BigDecimal> bigDecimalMinimumSemigroup =
  // semigroup(Ord.bigdecimalOrd.min);

  /**
   * A semigroup that adds longs.
   */
  public static final Semigroup<Long> longAdditionSemigroup = new Semigroup<Long>() {
    public Long append(final Long x, final Long y) {
      return x + y;
    }
  };

  /**
   * A semigroup that multiplies longs.
   */
  public static final Semigroup<Long> longMultiplicationSemigroup = new Semigroup<Long>() {
    public Long append(final Long x, final Long y) {
      return x * y;
    }
  };

  /**
   * A semigroup that yields the maximum of longs.
   */
  // public static final Semigroup<Long> longMaximumSemigroup =
  // semigroup(Ord.longOrd.max);

  /**
   * A semigroup that yields the minimum of longs.
   */
  // public static final Semigroup<Long> longMinimumSemigroup =
  // semigroup(Ord.longOrd.min);

  /**
   * A semigroup that ORs booleans.
   */
  public static final Semigroup<Boolean> disjunctionSemigroup = new Semigroup<Boolean>() {
    public Boolean append(final Boolean b1, final Boolean b2) {
      return b1 || b2;
    }
  };

  /**
   * A semigroup that XORs booleans.
   */
  public static final Semigroup<Boolean> exclusiveDisjunctionSemiGroup = new Semigroup<Boolean>() {
    public Boolean append(final Boolean p, final Boolean q) {
      return p && !q || !p && q;
    }
  };

  /**
   * A semigroup that ANDs booleans.
   */
  public static final Semigroup<Boolean> conjunctionSemigroup = new Semigroup<Boolean>() {
    public Boolean append(final Boolean b1, final Boolean b2) {
      return b1 && b2;
    }
  };

  /**
   * A semigroup that appends strings.
   */
  public static final Semigroup<String> stringSemigroup = new Semigroup<String>() {
    public String append(final String s1, final String s2) {
      return s1 + s2;
    }
  };

  /**
   * A semigroup that appends string buffers.
   */
  public static final Semigroup<StringBuffer> stringBufferSemigroup = new Semigroup<StringBuffer>() {
    public StringBuffer append(final StringBuffer s1, final StringBuffer s2) {
      return new StringBuffer(s1).append(s2);
    }
  };

  /**
   * A semigroup that appends string builders.
   */
  public static final Semigroup<StringBuilder> stringBuilderSemigroup = new Semigroup<StringBuilder>() {
    public StringBuilder append(final StringBuilder s1, final StringBuilder s2) {
      return new StringBuilder(s1).append(s2);
    }
  };

  /**
   * A semigroup for functions.
   * 
   * @param sb The smeigroup for the codomain.
   * @return A semigroup for functions.
   */
  public static <A, B> Semigroup<Function<A, B>> functionSemigroup(final Semigroup<B> sb) {
    return new Semigroup<Function<A, B>>() {
      public Function<A, B> append(final Function<A, B> f1, final Function<A, B> f2) {
        return new Function<A, B>() {
          public B apply(final A a) {
            return sb.append(f1.apply(a), f2.apply(a));
          }
        };
      }
    };
  }

  /** A semigroup for lists. */
  public static <A> Semigroup<List<A>> listSemigroup() {
    return new Semigroup<List<A>>() {
      public List<A> append(final List<A> a1, final List<A> a2) {
        return ImmutableList.<A> builder().addAll(a1).addAll(a2).build();
      }
    };
  }

  /** A semigroup for iterables. */
  public static <A> Semigroup<Iterable<A>> iterableSemigroup() {
    return new Semigroup<Iterable<A>>() {
      public Iterable<A> append(final Iterable<A> a1, final Iterable<A> a2) {
        return com.google.common.collect.Iterables.concat(a1, a2);
      }
    };
  }

  /**
   * A semigroup for optional values.
   ** 
   * @return A semigroup for optional values.
   */
  public static <A> Semigroup<Option<A>> optionSemigroup() {
    return new Semigroup<Option<A>>() {
      public Option<A> append(final Option<A> a1, final Option<A> a2) {
        return a1.isDefined() ? a1 : a2;
      }
    };
  }

  /**
   * A semigroup for optional values that take the first available value.
   * 
   * @return A semigroup for optional values that take the first available
   * value.
   */
  public static <A> Semigroup<Option<A>> firstOptionSemigroup() {
    return new Semigroup<Option<A>>() {
      public Option<A> append(final Option<A> a1, final Option<A> a2) {
        return a1.orElse(a2);
      }
    };
  }

  /**
   * A semigroup for optional values that take the last available value.
   * 
   * @return A semigroup for optional values that take the last available value.
   */
  public static <A> Semigroup<Option<A>> lastOptionSemigroup() {
    return new Semigroup<Option<A>>() {
      public Option<A> append(final Option<A> a1, final Option<A> a2) {
        return a2.orElse(a1);
      }
    };
  }

  /**
   * A semigroup for sets.
   * 
   * @return a semigroup for sets.
   */
  public static <A> Semigroup<Set<A>> setSemigroup() {
    return new Semigroup<Set<A>>() {
      public Set<A> append(final Set<A> a, final Set<A> b) {
        return Sets.union(a, b);
      }
    };
  }
}
