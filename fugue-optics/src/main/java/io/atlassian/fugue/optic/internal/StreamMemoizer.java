/* Copyright (c) 2015, Jean-Baptiste Giraudeau <jb@giraudeau.info>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package io.atlassian.fugue.optic.internal;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamMemoizer<T> {

  private class MemoizingConsumer implements Consumer<T> {

    private Consumer<? super T> consumer;

    @Override public void accept(final T t) {
      memoizedElements.add(t);
      consumer.accept(t);
    }

  }

  private final Spliterator<T> spliterator;

  private final ArrayList<T> memoizedElements;

  private final MemoizingConsumer memoizingConsumer;

  private StreamMemoizer(final Spliterator<T> spliterator) {
    this.spliterator = spliterator;
    final long estimatedSize = spliterator.estimateSize();
    this.memoizedElements = estimatedSize == Long.MAX_VALUE ? new ArrayList<>() : new ArrayList<>((int) estimatedSize);
    this.memoizingConsumer = new MemoizingConsumer();
  }

  public Spliterator<T> spliterator() {
    return new Spliterator<T>() {

      private final Spliterator<T> memoizedSpliterator = memoizedElements.spliterator();

      @Override public boolean tryAdvance(final Consumer<? super T> action) {
        memoizingConsumer.consumer = action;
        return memoizedSpliterator.tryAdvance(action) || spliterator.tryAdvance(memoizingConsumer);
      }

      @Override public Spliterator<T> trySplit() {
        return null;
      }

      @Override public long estimateSize() {
        final long estimateSize = spliterator.estimateSize();
        return estimateSize == Long.MAX_VALUE ? Long.MAX_VALUE : memoizedSpliterator.estimateSize() + estimateSize;
      }

      @Override public int characteristics() {
        return Spliterator.ORDERED;
      }
    };
  }

  public Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  public static <T> StreamMemoizer<T> memoizer(final Spliterator<T> spliterator) {
    return new StreamMemoizer<>(spliterator);
  }

  public static <T> StreamMemoizer<T> memoizer(final Stream<T> stream) {
    return memoizer(stream.spliterator());
  }

}
