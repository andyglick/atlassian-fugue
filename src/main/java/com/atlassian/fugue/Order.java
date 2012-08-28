package com.atlassian.fugue;

import static com.atlassian.fugue.Functions.curry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.google.common.base.Function;

/**
 * Tests for ordering between two objects.
 * 
 * @since 1.2
 */
public final class Order<A> {
  private final Function<A, Function<A, Ordering>> f;

  private Order(final Function<A, Function<A, Ordering>> f) {
    this.f = f;
  }

  /**
   * First-class ordering.
   * 
   * @return A function that returns an ordering for its arguments.
   */
  public Function<A, Function<A, Ordering>> compare() {
    return f;
  }

  /**
   * Returns an ordering for the given arguments.
   * 
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return An ordering for the given arguments.
   */
  public Ordering compare(final A a1, final A a2) {
    return f.apply(a1).apply(a2);
  }

  /**
   * Returns <code>true</code> if the given arguments are equal,
   * <code>false</code> otherwise.
   * 
   * @param a1 An instance to compare for equality to another.
   * @param a2 An instance to compare for equality to another.
   * @return <code>true</code> if the given arguments are equal,
   * <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return compare(a1, a2) == Ordering.EQ;
  }

  /**
   * Returns an <code>Equal</code> for this order.
   * 
   * @return An <code>Equal</code> for this order.
   */
  public Equal<A> equal() {
    return Equal.equal(curry(new Function2<A, A, Boolean>() {
      public Boolean apply(final A a1, final A a2) {
        return eq(a1, a2);
      }
    }));
  }

  /**
   * Returns <code>true</code> if the first given argument is less than the
   * second given argument, <code>false</code> otherwise.
   * 
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return <code>true</code> if the first given argument is less than the
   * second given argument, <code>false</code> otherwise.
   */
  public boolean isLessThan(final A a1, final A a2) {
    return compare(a1, a2) == Ordering.LT;
  }

  /**
   * Returns <code>true</code> if the first given argument is greater than the
   * second given argument, <code>false</code> otherwise.
   * 
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return <code>true</code> if the first given argument is greater than the
   * second given argument, <code>false</code> otherwise.
   */
  public boolean isGreaterThan(final A a1, final A a2) {
    return compare(a1, a2) == Ordering.GT;
  }

  /**
   * Returns a function that returns true if its argument is less than the
   * argument to this method.
   * 
   * @param a A value to compare against.
   * @return A function that returns true if its argument is less than the
   * argument to this method.
   */
  public Function<A, Boolean> isLessThan(final A a) {
    return new Function<A, Boolean>() {
      public Boolean apply(final A a2) {
        return compare(a2, a) == Ordering.LT;
      }
    };
  }

  /**
   * Returns a function that returns true if its argument is greater than than
   * the argument to this method.
   * 
   * @param a A value to compare against.
   * @return A function that returns true if its argument is greater than the
   * argument to this method.
   */
  public Function<A, Boolean> isGreaterThan(final A a) {
    return new Function<A, Boolean>() {
      public Boolean apply(final A a2) {
        return compare(a2, a) == Ordering.GT;
      }
    };
  }

  /**
   * Returns the greater of its two arguments.
   * 
   * @param a1 A value to compare with another.
   * @param a2 A value to compare with another.
   * @return The greater of the two values.
   */
  public A max(final A a1, final A a2) {
    return isGreaterThan(a1, a2) ? a1 : a2;
  }

  /**
   * Returns the lesser of its two arguments.
   * 
   * @param a1 A value to compare with another.
   * @param a2 A value to compare with another.
   * @return The lesser of the two values.
   */
  public A min(final A a1, final A a2) {
    return isLessThan(a1, a2) ? a1 : a2;
  }

  /**
   * A function that returns the greater of its two arguments.
   */
  public final Function<A, Function<A, A>> max = curry(new Function2<A, A, A>() {
    public A apply(final A a, final A a1) {
      return max(a, a1);
    }
  });

  /**
   * A function that returns the lesser of its two arguments.
   */
  public final Function<A, Function<A, A>> min = curry(new Function2<A, A, A>() {
    public A apply(final A a, final A a1) {
      return min(a, a1);
    }
  });

  /**
   * Returns an order instance that uses the given equality test and ordering
   * function.
   * 
   * @param f The order function.
   * @return An order instance.
   */
  public static <A> Order<A> order(final Function<A, Function<A, Ordering>> f) {
    return new Order<A>(f);
  }

  /**
   * An order instance for the <code>boolean</code> type.
   */
  public static final Order<Boolean> booleanOrd = new Order<Boolean>(
    new Function<Boolean, Function<Boolean, Ordering>>() {
      public Function<Boolean, Ordering> apply(final Boolean a1) {
        return new Function<Boolean, Ordering>() {
          public Ordering apply(final Boolean a2) {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });

  /**
   * An order instance for the <code>byte</code> type.
   */
  public static final Order<Byte> byteOrd = new Order<Byte>(new Function<Byte, Function<Byte, Ordering>>() {
    public Function<Byte, Ordering> apply(final Byte a1) {
      return new Function<Byte, Ordering>() {
        public Ordering apply(final Byte a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>char</code> type.
   */
  public static final Order<Character> charOrd = new Order<Character>(
    new Function<Character, Function<Character, Ordering>>() {
      public Function<Character, Ordering> apply(final Character a1) {
        return new Function<Character, Ordering>() {
          public Ordering apply(final Character a2) {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });

  /**
   * An order instance for the <code>double</code> type.
   */
  public static final Order<Double> doubleOrd = new Order<Double>(new Function<Double, Function<Double, Ordering>>() {
    public Function<Double, Ordering> apply(final Double a1) {
      return new Function<Double, Ordering>() {
        public Ordering apply(final Double a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>float</code> type.
   */
  public static final Order<Float> floatOrd = new Order<Float>(new Function<Float, Function<Float, Ordering>>() {
    public Function<Float, Ordering> apply(final Float a1) {
      return new Function<Float, Ordering>() {
        public Ordering apply(final Float a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>int</code> type.
   */
  public static final Order<Integer> intOrd = new Order<Integer>(new Function<Integer, Function<Integer, Ordering>>() {
    public Function<Integer, Ordering> apply(final Integer a1) {
      return new Function<Integer, Ordering>() {
        public Ordering apply(final Integer a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>BigInteger</code> type.
   */
  public static final Order<BigInteger> bigintOrd = new Order<BigInteger>(
    new Function<BigInteger, Function<BigInteger, Ordering>>() {
      public Function<BigInteger, Ordering> apply(final BigInteger a1) {
        return new Function<BigInteger, Ordering>() {
          public Ordering apply(final BigInteger a2) {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });

  /**
   * An order instance for the <code>BigDecimal</code> type.
   */
  public static final Order<BigDecimal> bigdecimalOrd = new Order<BigDecimal>(
    new Function<BigDecimal, Function<BigDecimal, Ordering>>() {
      public Function<BigDecimal, Ordering> apply(final BigDecimal a1) {
        return new Function<BigDecimal, Ordering>() {
          public Ordering apply(final BigDecimal a2) {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });

  /**
   * An order instance for the <code>long</code> type.
   */
  public static final Order<Long> longOrd = new Order<Long>(new Function<Long, Function<Long, Ordering>>() {
    public Function<Long, Ordering> apply(final Long a1) {
      return new Function<Long, Ordering>() {
        public Ordering apply(final Long a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>short</code> type.
   */
  public static final Order<Short> shortOrd = new Order<Short>(new Function<Short, Function<Short, Ordering>>() {
    public Function<Short, Ordering> apply(final Short a1) {
      return new Function<Short, Ordering>() {
        public Ordering apply(final Short a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the {@link Ordering} type.
   */
  public static final Order<Ordering> orderingOrd = new Order<Ordering>(
    curry(new Function2<Ordering, Ordering, Ordering>() {
      public Ordering apply(final Ordering o1, final Ordering o2) {
        return o1 == o2 ? Ordering.EQ : o1 == Ordering.LT ? Ordering.LT : o2 == Ordering.LT ? Ordering.GT
          : o1 == Ordering.EQ ? Ordering.LT : Ordering.GT;
      }
    }));

  /**
   * An order instance for the {@link String} type.
   */
  public static final Order<String> stringOrd = new Order<String>(new Function<String, Function<String, Ordering>>() {
    public Function<String, Ordering> apply(final String a1) {
      return new Function<String, Ordering>() {
        public Ordering apply(final String a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
        }
      };
    }
  });

  /**
   * An order instance for the {@link StringBuffer} type.
   */
  public static final Order<StringBuffer> stringBufferOrd = new Order<StringBuffer>(
    new Function<StringBuffer, Function<StringBuffer, Ordering>>() {
      public Function<StringBuffer, Ordering> apply(final StringBuffer a1) {
        return new Function<StringBuffer, Ordering>() {
          public Ordering apply(final StringBuffer a2) {
            return stringOrd.compare(a1.toString(), a2.toString());
          }
        };
      }
    });

  /**
   * An order instance for the {@link StringBuffer} type.
   */
  public static final Order<StringBuilder> stringBuilderOrd = new Order<StringBuilder>(
    new Function<StringBuilder, Function<StringBuilder, Ordering>>() {
      public Function<StringBuilder, Ordering> apply(final StringBuilder a1) {
        return new Function<StringBuilder, Ordering>() {
          public Ordering apply(final StringBuilder a2) {
            return stringOrd.compare(a1.toString(), a2.toString());
          }
        };
      }
    });

  /**
   * An order instance for the {@link Option} type.
   * 
   * @param oa Order across the element of the option.
   * @return An order instance for the {@link Option} type.
   */
  public static <A> Order<Option<A>> optionOrd(final Order<A> oa) {
    return new Order<Option<A>>(new Function<Option<A>, Function<Option<A>, Ordering>>() {
      public Function<Option<A>, Ordering> apply(final Option<A> o1) {
        return new Function<Option<A>, Ordering>() {
          public Ordering apply(final Option<A> o2) {
            return o1.isEmpty() ? o2.isEmpty() ? Ordering.EQ : Ordering.LT : o2.isEmpty() ? Ordering.GT : oa.f.apply(
              o1.get()).apply(o2.get());
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link Either} type.
   * 
   * @param oa Order across the left side of {@link Either}.
   * @param ob Order across the right side of {@link Either}.
   * @return An order instance for the {@link Either} type.
   */
  public static <A, B> Order<Either<A, B>> eitherOrd(final Order<A> oa, final Order<B> ob) {
    return new Order<Either<A, B>>(new Function<Either<A, B>, Function<Either<A, B>, Ordering>>() {
      public Function<Either<A, B>, Ordering> apply(final Either<A, B> e1) {
        return new Function<Either<A, B>, Ordering>() {
          public Ordering apply(final Either<A, B> e2) {
            return e1.isLeft() ? e2.isLeft() ? oa.f.apply(e1.left().get()).apply(e2.left().get()) : Ordering.LT
              : e2.isLeft() ? Ordering.GT : ob.f.apply(e1.right().get()).apply(e2.right().get());
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link List} type.
   * 
   * @param oa Order across the elements of the list.
   * @return An order instance for the {@link List} type.
   */
  public static <A> Order<List<A>> listOrd(final Order<A> oa) {
    return new Order<List<A>>(new Function<List<A>, Function<List<A>, Ordering>>() {
      public Function<List<A>, Ordering> apply(final List<A> l1) {
        return new Function<List<A>, Ordering>() {
          public Ordering apply(final List<A> l2) {
            if (l1.isEmpty())
              return l2.isEmpty() ? Ordering.EQ : Ordering.LT;
            else if (l2.isEmpty())
              return l1.isEmpty() ? Ordering.EQ : Ordering.GT;
            else {
              final Ordering c = oa.compare(l1.get(0), l2.get(0));
              return c == Ordering.EQ ? listOrd(oa).f.apply(l1.subList(1, l1.size() - 1)).apply(l2.subList(1, l2.size() -1 )) : c;
            }
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link Unit} type.
   */
  public static final Order<Unit> unitOrd = order(curry(new Function2<Unit, Unit, Ordering>() {
    public Ordering apply(final Unit u1, final Unit u2) {
      return Ordering.EQ;
    }
  }));

  /**
   * An order instance for the <code>Comparable</code> interface.
   * 
   * @return An order instance for the <code>Comparable</code> interface.
   */
  public static <A extends Comparable<A>> Order<A> comparableOrd() {
    return order(new Function<A, Function<A, Ordering>>() {
      public Function<A, Ordering> apply(final A a1) {
        return new Function<A, Ordering>() {
          public Ordering apply(final A a2) {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });
  }

  /**
   * An order instance that uses {@link Object#hashCode()} for computing the
   * order and equality, thus objects returning the same hashCode are considered
   * to be equals (check {@link #hashEqualsOrd()} for an additional check on
   * {@link Object#equals(Object)}).
   * 
   * @return An order instance that is based on {@link Object#hashCode()}.
   * @see #hashEqualsOrd()
   */
  public static <A> Order<A> hashOrd() {
    return Order.<A> order(new Function<A, Function<A, Ordering>>() {
      @Override
      public Function<A, Ordering> apply(final A a) {
        return new Function<A, Ordering>() {
          @Override
          public Ordering apply(final A a2) {
            final int x = a.hashCode() - a2.hashCode();
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });
  }

  /**
   * An order instance that uses {@link Object#hashCode()} and
   * {@link Object#equals()} for computing the order and equality. First the
   * hashCode is compared, if this is equal, objects are compared using
   * {@link Object#equals()}.
   * 
   * @return An order instance that is based on {@link Object#hashCode()} and
   * {@link Object#equals()}.
   */
  public static <A> Order<A> hashEqualsOrd() {
    return Order.<A> order(new Function<A, Function<A, Ordering>>() {
      @Override
      public Function<A, Ordering> apply(final A a) {
        return new Function<A, Ordering>() {
          @Override
          public Ordering apply(final A a2) {
            final int x = a.hashCode() - a2.hashCode();
            return x < 0 ? Ordering.LT : x == 0 && a.equals(a2) ? Ordering.EQ : Ordering.GT;
          }
        };
      }
    });
  }
}