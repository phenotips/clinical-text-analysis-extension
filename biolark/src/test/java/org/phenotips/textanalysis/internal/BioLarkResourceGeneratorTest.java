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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.anyString;

public class BioLarkResourceGeneratorTest
{
    /**
     * Root directory of biolark resource files
     */
    private final static File biolarkRoot = new File(
        BioLarkResourceGenerator.ROOT_DIRECTORY);

    /**
     * Biolark properties file
     */
	private final static File biolarkProperties = new File(
		BioLarkResourceGeneratorTest.biolarkRoot, 
		BioLarkResourceGenerator.PROPERTIES_FILENAME);

    /**
     * Directory with biolark inputs and outputs
     */
	private final static File biolarkIODir = new File(
		BioLarkResourceGeneratorTest.biolarkRoot,
		BioLarkResourceGenerator.IO_FILENAME);

	private BioLarkResourceGenerator generator;
    /**
     * Configures mocking and clear biolark directory
     *
     * @throws IOException
     */
    @BeforeClass
    public static void oneTimeSetUp() throws IOException
    {
    	BioLarkResourceGenerator generator = new BioLarkResourceGenerator();
    	doNothing().when(generator.extractArchive(Matchers.any(File.class), Matchers.any(File.class));
    	Mockito.when(generator.extractArchive(Matchers.any(File.class),
            Matchers.any(File.class))
    	Mockito.when(generator.downloadFile(Mockito.anyString(), Mockito.anyString())).th
        // mock extracting files
        PowerMockito.doNothing().when(BioLarkResourceGenerator.class);
        BioLarkResourceGenerator.extractArchive(Matchers.any(File.class),
            Matchers.any(File.class));

        // mock downloading files
        PowerMockito.when(
            BioLarkResourceGenerator.downloadFile(Matchers.any(String.class),
                Matchers.any(String.class))).thenReturn(new File("."));

        deleteFiles();
    }

    /**
     * Deletes biolark resource files
     *
     * @throws IOException
     */
    @After
    public void tearDown() throws IOException
    {
        //deleteFiles();
    }

    /**
     * Tests whether generateResources creates the needed directories and downloads the files
     *
     * @throws Exception
     */
    @Test
    public void testGenerateResources() throws Exception
    {
//        try {
//            String expected =
//                BioLarkResourceUtilsTest.biolarkProperties.getAbsolutePath();
//            String actual = BioLarkResourceUtils.generateBiolarkResources();
//
//            assertEquals("Should return correct path", expected, actual);
//
//            // check that properties file was generated
//            assertTrue("Properties file should exist",
//                Files.exists(BioLarkResourceUtilsTest.biolarkProperties
//                    .toPath()));
//            assertFalse("Properties file should not be a directory",
//                Files.isDirectory(BioLarkResourceUtilsTest.biolarkProperties
//                    .toPath()));
//            assertTrue("File should not be empty",
//                BioLarkResourceUtilsTest.biolarkProperties.length() > 0);
//
//            // check that biolark folders were created
//            assertTrue("'empty' directory should exist",
//                Files.exists(BioLarkResourceUtilsTest.biolarkIODir.toPath()));
//            assertTrue("'empty'should be a directory, not a file",
//                Files.isDirectory(BioLarkResourceUtilsTest.biolarkProperties
//                    .toPath()));
//
//            // check that files were downloaded
//            PowerMockito.verifyStatic(times(0));
//            BioLarkResourceUtils.downloadFile(Matchers.any(String.class),
//                Matchers.any(String.class));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("IOException when generating files");
//        }
    }

    /**
     * C.
     */
    public void testGenerateResourcesCached()
    {
//        // create properties file
//        File biolarkRoot = new File(BioLarkResourceUtils.ROOT_DIRECTORY);
//        if (!biolarkRoot.exists()) {
//            biolarkRoot.mkdirs();
//        }
//        File properties =
//            new File(biolarkRoot, BioLarkResourceUtils.PROPERTIES_FILENAME);
//
//        try {
//            // create properties file
//            properties.createNewFile();
//
//            // test output
//            String expected =
//                BioLarkResourceUtilsTest.biolarkProperties.getAbsolutePath();
//            String actual = BioLarkResourceUtils.generateBiolarkResources();
//            assertEquals("Should return correct path", expected, actual);
//
//            // check that files are not redownloaded
//            PowerMockito.verifyStatic(times(0));
//            BioLarkResourceUtils.downloadFile(Matchers.any(String.class),
//                Matchers.any(String.class));
//
//        } catch (IOException e) {
//            fail("IOException when generating files");
//        }
    }

    /**
     * Removes biolark properties file and IO directory.
     */
    private static void deleteFiles()
    {
//        try {
//            Files.deleteIfExists(BioLarkResourceUtilsTest.biolarkProperties
//                .toPath());
//            FileUtils.deleteDirectory(BioLarkResourceUtilsTest.biolarkIODir);
//        } catch (IOException e) {
//            // do nothing, directory didn't exist to begin with
//        }
    }

}
