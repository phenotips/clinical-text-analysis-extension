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
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.edu.uq.eresearch.biolark.cr.Annotation;
import au.edu.uq.eresearch.biolark.cr.BioLarK_CR;

/**
 * Wrapper component for BioLark phenotype annotation library.
 *
 * @version $Id$
 */
@Component
public class BiolarkWrapperImpl implements BiolarkWrapper, Initializable
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

    private BioLarK_CR biolark;

    @Override
    public void initialize() throws InitializationException
    {
        String propertiesPath;
        try {
            propertiesPath = generateBiolarkResources();
            this.biolark = new BioLarK_CR(propertiesPath);
        } catch (IOException e) {
            throw new InitializationException(e.getMessage());
        }
    }

    @Override
    public List<Annotation> annotatePlain(String text, boolean longestMatch)
    {
        return this.biolark.annotate_plain(text, longestMatch);
    }

    /**
     * Creates a properties file to be used by the biolark annotation plugin.
     *
     * @return path to generated biolark properties file
     * @throws IOException in case of error in reading/writing property files
     */
    public String generateBiolarkResources() throws IOException
    {
        final File biolarkRoot =
            new File(BiolarkWrapperImpl.ROOT_DIRECTORY);
        final File biolarkProperties = new File(biolarkRoot, BiolarkWrapperImpl.PROPERTIES_FILENAME);
        final File emptyDir = new File(biolarkRoot.getAbsolutePath() + "/"
            + BiolarkWrapperImpl.IO_FILENAME);

        // Check for existing biolark files
        if (biolarkProperties.exists() && !biolarkProperties.isDirectory()) {
            return biolarkProperties.getAbsolutePath();
        }

        // Create Biolark work directories
        biolarkRoot.mkdir();
        emptyDir.mkdir();

        // Download and extract biolark files
        final String pathToArchive = "biolark_resources.jar";
        File resources = BiolarkFileUtils.downloadFile(pathToArchive, BiolarkWrapperImpl.RESOURCE_FILES_URL);
        BiolarkFileUtils.extractArchive(resources, biolarkRoot);

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

}
