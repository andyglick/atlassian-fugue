package com.atlassian.fugue;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

public class Iterables
{
  private Iterables() { throw new UnsupportedOperationException("This class is not instantiable."); }
  
  public static <T> Option<T> find(Iterable<? extends T> ts, Predicate<? super T> p) {

    UnmodifiableIterator<? extends T> t = Iterators.filter(ts.iterator(), p);
    if(t.hasNext()) {
        return Option.some(t.next());
    }
    return Option.none();
  }
}
