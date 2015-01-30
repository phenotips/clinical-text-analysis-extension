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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyTerm;
import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationClient;
import org.xwiki.component.annotation.Component;

import au.edu.uq.eresearch.biolark.cr.Annotation;
import au.edu.uq.eresearch.biolark.cr.BioLarK_CR;

/**
 * Implementation of AnnotationClient using BioLark.
 *
 * @version $Id$
 * @since 1.0M1
 */
@Component
public class BioLarkAnnotationClient implements TermAnnotationClient {

	/**
	 * Ontology used to look up Phenotype term Ids
	 */
	@Inject
	private OntologyManager ontologies;

	/**
	 * Biolark annotation plugin
	 */
	private BioLarK_CR biolark;

	public BioLarkAnnotationClient() throws AnnotationException {

		try {
			String propertiesPath = generateBiolarkResources();
			biolark = new BioLarK_CR(propertiesPath);
		} catch (IOException e) {
			throw new AnnotationException(
					"Couldn't create BioLark properties file");
		}
	}

	/**
	 * Creates a properties file to be used by the biolark annotation plugin
	 * 
	 * @throws IOException
	 */
	private String generateBiolarkResources() throws IOException {

		File biolarkRoot = new File("resources/BioLark-CR/");
		File biolarkProperties = new File(biolarkRoot, "cr.properties");
		File emptyDir = new File(biolarkRoot.getAbsolutePath() + "/empty");
		File taTmpDir = new File(biolarkRoot.getAbsolutePath() + "/ta_tmp");

		// Check for existing biolark files
		if (biolarkProperties.exists() && !biolarkProperties.isDirectory()) {
			return biolarkProperties.getAbsolutePath();
		}

		// Create Biolark work directories
		boolean success = biolarkRoot.mkdir() && emptyDir.mkdir()
				&& taTmpDir.mkdir();
		if (!success) {
			throw new IOException("Error creating biolark directories");
		}

		// Download and extract biolark files
		String pathToArchive = "biolark_resources.jar";
		URL website = new URL(
				"http://nexus.cs.toronto.edu/nexus/service/local/repositories/externals/content/org/biolark/biolark-resources/0.1/biolark-resources-0.1.jar");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(pathToArchive);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		try {
			extractArchive(new File(pathToArchive), biolarkRoot);
		} catch (ArchiveException e) {
			throw new IOException(e.getMessage());
		}

		// Create properties file
		fos = new FileOutputStream(biolarkProperties);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("logLevel=INFO\n");
		bw.write("longestMatch=FALSE\n");
		bw.write("outputFormat=text\n");
		bw.append("path=").append(biolarkRoot.getAbsolutePath());
		bw.newLine();
		bw.append("inputFolder=").append(emptyDir.getAbsolutePath());
		bw.newLine();
		bw.append("outputFolder=").append(emptyDir.getAbsolutePath());
		bw.newLine();
		fos.close();
		bw.close();

		return biolarkProperties.getAbsolutePath();
	}

	/**
	 * Unzips archive into targetDir
	 * @param archive zip archive
	 * @param targetDir
	 * @throws IOException
	 * @throws ArchiveException
	 */
	private void extractArchive(File archive, File targetDir)
			throws IOException, ArchiveException {
		final InputStream is = new FileInputStream(archive);
		final ArchiveInputStream in = new ArchiveStreamFactory()
				.createArchiveInputStream("zip", is);
		final ZipArchiveEntry entry = (ZipArchiveEntry) in.getNextEntry();
		final OutputStream out = new FileOutputStream(new File(targetDir,
				entry.getName()));
		IOUtils.copy(in, out);
		out.close();
		in.close();
	}

	@Override
	public List<TermAnnotation> annotate(String text)
			throws AnnotationException {

		List<Annotation> biolarkAnnotations = biolark.annotate_plain(text,
				false);
		List<TermAnnotation> finalAnnotations = new LinkedList<TermAnnotation>();

		// Resolve each termID against Phenotype ontology
		for (Annotation biolarkAnnotation : biolarkAnnotations) {
			String termId = FilenameUtils.getBaseName(
					biolarkAnnotation.getUri()).replace('_', ':');
			OntologyTerm term = ontologies.resolveTerm(termId);
			if (term != null) {
				long start = biolarkAnnotation.getStartOffset();
				long end = biolarkAnnotation.getEndOffset();
				finalAnnotations.add(new TermAnnotation(start, end, term));
			}
		}
		return finalAnnotations;
	}

}
