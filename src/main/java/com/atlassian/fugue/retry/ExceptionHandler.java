/*
   Copyright 2010 Atlassian

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
package com.atlassian.fugue.retry;

/**
 * Takes action when an Exception is thrown. Examples include placing a delay in
 * execution when performing back-offs and logging errors when exceptions are
 * encountered.
 * 
 * @see ExceptionHandlers for some predefined handlers
 */
public interface ExceptionHandler {
  /**
   * Act on an exception, this method should be called by clients when an
   * exception occurs in wrapped code.
   */
  void handle(RuntimeException exception);
}
