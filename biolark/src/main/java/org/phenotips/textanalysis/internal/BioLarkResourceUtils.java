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

import org.xwiki.component.annotation.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Implementation of AnnotationClient using BioLark.
 *
 * @version $Id$
 * @since 1.0M1
 */
@Component
public final class BioLarkResourceUtils
{
    /** Relative path to the directory containing biolark resources. */
    public static final String ROOT_DIRECTORY = "resources/BioLark-CR/";

    /** Name of biolark properties file. */
    public static final String PROPERTIES_FILENAME = "cr.properties";

    /** Name of directory for biolark generated input/output files, which will be ignored. */
    public static final String IO_FILENAME = "empty";

    /** Name of directory for biolark generated input/output files, which will be ignored. */
    public static final String RESOURCE_FILES_URL =
        "http://nexus.cs.toronto.edu/nexus/service/local/repositories/externals/"
            + "content/org/biolark/biolark-resources/0.1/biolark-resources-0.1.jar";

    private BioLarkResourceUtils()
    {
        // Private constructor prevents instantiation of utility class
    }

    /**
     * Creates a properties file to be used by the biolark annotation plugin.
     *
     * @return path to generated biolark properties file
     * @throws IOException in case of error in reading/writing property files
     */
    public static String generateBiolarkResources() throws IOException
    {
        final File biolarkRoot = new File(BioLarkResourceUtils.ROOT_DIRECTORY);
        final File biolarkProperties =
            new File(biolarkRoot, BioLarkResourceUtils.PROPERTIES_FILENAME);
        final File emptyDir =
            new File(biolarkRoot.getAbsolutePath() + "/"
                + BioLarkResourceUtils.IO_FILENAME);

        // Check for existing biolark files
        if (biolarkProperties.exists() && !biolarkProperties.isDirectory()) {
            return biolarkProperties.getAbsolutePath();
        }

        // Create Biolark work directories
        biolarkRoot.mkdir();
        emptyDir.mkdir();

        // Download and extract biolark files
        final String pathToArchive = "biolark_resources.jar";
        File resources =
            downloadFile(pathToArchive, BioLarkResourceUtils.RESOURCE_FILES_URL);
        extractArchive(resources, biolarkRoot);

        // Create properties file
        final FileWriter bw = new FileWriter(biolarkProperties);
        bw.write("logLevel=INFO\n");
        bw.write("longestMatch=FALSE\n");
        bw.write("outputFormat=text\n");
        bw.append("path=").append(biolarkRoot.getAbsolutePath());
        bw.append(System.lineSeparator());
        bw.append("inputFolder=").append(emptyDir.getAbsolutePath());
        bw.append(System.lineSeparator());
        bw.append("outputFolder=").append(emptyDir.getAbsolutePath());
        bw.append(System.lineSeparator());
        bw.close();

        return biolarkProperties.getAbsolutePath();
    }

    /**
     * Unzips archive into targetDir.
     *
     * @param archive zip archive
     * @param targetDir
     * @throws IOException
     */
    public static void extractArchive(File archive, File targetDir)
        throws IOException
    {
        try {
            ZipFile zipFile = new ZipFile(archive.getAbsolutePath());
            zipFile.extractAll(targetDir.getAbsolutePath());

            // zip4j overwrites executable permissions, so we add them manually after extracting
            makeExecutable(targetDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads file at given url and saves it with given filename in the current directory
     *
     * @param fileName
     * @param url
     * @return File which was downloaded
     * @throws IOException
     */
    public static File downloadFile(String fileName, String url)
        throws IOException
    {
        final URL resourcesURL =
            new URL(BioLarkResourceUtils.RESOURCE_FILES_URL);
        final ReadableByteChannel rbc =
            Channels.newChannel(resourcesURL.openStream());
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        return new File(fileName);
    }

    /**
     * Gives the target or it's contents executable permissions. Operates recursively.
     *
     * @param target File or directory to modify
     */
    public static void makeExecutable(File target)
    {
        if (target.isFile()) {
            target.setExecutable(true);
        } else {
            for (File file : target.listFiles()) {
                makeExecutable(file);
            }
        }
    }
}
