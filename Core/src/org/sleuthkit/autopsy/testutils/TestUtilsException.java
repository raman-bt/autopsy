/*
 * Autopsy Forensic Browser
 *
 * Copyright 2018 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.testutils;

/**
 * Exception created from exercising parts of TestUtils.
 */
public class TestUtilsException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Main constructor that accepts a message.
     * @param message The message.
     */
    public TestUtilsException(String message) {
        super(message);
    }

    /**
     * Main constructor that accepts a message and inner exception.
     * @param message The message.
     * @param innerException The inner exception.
     */
    public TestUtilsException(String message, Throwable innerException) {
        super(message, innerException);
    }
    
}