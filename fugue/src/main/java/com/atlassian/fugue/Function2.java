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
 * Represents a function that takes two parameters.
 * 
 * @param <F1> the type of the first argument accepted
 * @param <F2> the type of the result
 * @param <T> the type of the result of application
 * 
 * @since 1.0
 */
public interface Function2<F1, F2, T> {
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
   */
  T apply(F1 arg1, F2 arg2);
}
