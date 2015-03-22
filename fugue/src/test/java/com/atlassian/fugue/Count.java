/*
   Copyright 2011 Atlassian

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

/**
 * Simply counts how many times it was applied.
 */
class Count<A> implements Effect<A> {
  static <A> int countEach(final Effect.Applicant<A> a) {
    final Count<A> counter = new Count<>();
    a.foreach(counter);
    return counter.count();
  }

  private int count = 0;

  public void apply(final A a) {
    count++;
  }

  public int count() {
    return count;
  }
}
