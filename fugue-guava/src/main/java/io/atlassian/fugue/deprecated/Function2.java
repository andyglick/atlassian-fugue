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
package io.atlassian.fugue.deprecated;

import java.util.function.BiFunction;

/**
 * Represents a function that takes two parameters.
 *
 * @param <A> the type of the first argument accepted
 * @param <B> the type of the result
 * @param <C> the type of the result of application
 * @since 1.0
 * @deprecated since 2.4 use BiFunction instead
 */
@Deprecated public interface Function2<A, B, C> extends BiFunction<A, B, C> {
  /**
   * Returns the results of applying this function to the supplied arguments.
   * Like Guava's Function, this method is <em>generally expected</em>, but not
   * absolutely required, to have the following properties: * it's execution
   * does not cause any observable side effect * the computation is
   * <em>consistent with equals</em>; that is, Objects.equal(a, b) implies that
   * Objects.equal(function.apply(a), function.apply(b)).
   *
   * @param arg1 the first argument
   * @param arg2 the second argument
   * @return the result, should not be null
   * @deprecated since 2.4 use BiFunction instead
   */
  @Deprecated C apply(A arg1, B arg2);
}
