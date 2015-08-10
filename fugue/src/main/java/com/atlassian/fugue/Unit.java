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
 * An alternative to {@link Void} that is actually once inhabited (whereas Void
 * is inhabited by null, which causes NPEs).
 * 
 * @since 2.2
 */
public enum Unit {
  VALUE;

  /**
   * Provide ability to statically import {@code Unit.UNIT} and then use {@code UNIT()}
   * within your code, primarily for readability purposes.
   *
   * @return The value for Unit, ie. {@link #VALUE}
   * @since 2.5
   */
  public static Unit Unit() {
    return VALUE;
  }

}
