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
 * A Semigroup is an appender or combiner. It is used to take two values of one
 * type and combine them into a new value of the same type.
 * <p>
 * Instances must be <strong>associative</strong> ie.
 * <p>
 * <code> forall x. forall y. forall z. sum(sum(x, y), z) == sum(x, sum(y, z))</code>
 * 
 * @since 1.2
 */
public interface Semigroup<A> {
  A append(A a, A b);
}
