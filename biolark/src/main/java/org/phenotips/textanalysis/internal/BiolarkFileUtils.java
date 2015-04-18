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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NotDirectoryException;

import org.apache.commons.io.IOUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Implementation of TermAnnotationService using BioLark.
 *
 * @version $Id$
 * @since 1.0M1
 */
public final class BiolarkFileUtils
{

    private BiolarkFileUtils()
    {
        // do nothing
    }

    /**
     * Unzips archive into targetDir.
     *
     * @param archive zip archive
     * @param targetDir where the archive will be extracted to
     * @throws IOException if extraction fails
     */
    public static void extractArchive(File archive, File targetDir) throws IOException
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
     * Downloads file located at given url and saves it with given filename in the current directory.
     *
     * @param fileName Name under which the file will be saved locally
     * @param url URL of hte file
     * @return File which was downloaded
     * @throws IOException if downloading fails
     */
    public static File downloadFile(String fileName, String url) throws IOException
    {
        final URL resourcesURL = new URL(url);
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

    /**
     * Builds project in target directory, by executing 'make clean && make'.
     *
     * @param target directory containing makefile
     * @param runtime the Runtime instance of this application
     * @throws BuildException if the build failed
     * @throws NotDirectoryException if target is not a directory
     */
    public static void make(File target, Runtime runtime) throws BuildException, NotDirectoryException
    {
        if (target.isDirectory()) {
            try {
                Process p = Runtime.getRuntime().exec("make -B", null, target);
                p.waitFor();
                if (p.exitValue() != 0) {
                    IOUtils.copy(p.getErrorStream(), System.out);
                    throw new BuildException("Build failed in " + target.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new BuildException(e.getMessage());
            } catch (InterruptedException e) {
                throw new BuildException(e.getMessage());
            }
        } else {
            throw new NotDirectoryException(target.getPath());
        }
    }
}
