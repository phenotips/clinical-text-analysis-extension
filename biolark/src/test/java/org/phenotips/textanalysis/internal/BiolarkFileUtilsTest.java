/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.textanalysis.internal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

public class BiolarkFileUtilsTest
{

    /**
     * Try calling make with file that is not a directory, and expect and exception.
     */
    @Test
    public void makeNotDirTest()
    {
        // Mock process to return 0
        Runtime mockRuntime;
        Process mockProcess;
        mockRuntime = Mockito.mock(Runtime.class);
        mockProcess = Mockito.mock(Process.class);
        Mockito.when(mockProcess.exitValue()).thenReturn(0);
        try {
            Mockito.when(mockRuntime.exec(Mockito.anyString(), Mockito.any(String[].class),
                    Mockito.any(File.class))).thenReturn(mockProcess);
        } catch (IOException e){
            // do nothing
        }

        File file = new File("file");
        boolean caughtException = false;

        try {
            BiolarkFileUtils.make(file, mockRuntime);
        } catch (NotDirectoryException e) {
            caughtException = true;
        } catch (BuildException e) {
            fail();
        }

        // Expected Exception was thrown
        assertTrue(caughtException);
    }

}
