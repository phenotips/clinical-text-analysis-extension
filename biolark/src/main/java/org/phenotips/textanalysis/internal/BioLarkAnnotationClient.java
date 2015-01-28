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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

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

		// String biolarkRootPath = "resources/BioLark-CR/";
		String propertiesPath = "resources/BioLark-CR/cr.properties";
		try {
			generateBiolarkProperties(propertiesPath);
		} catch (IOException e) {
			throw new AnnotationException(
					"Couldn't create BioLark properties file");
		}
		biolark = new BioLarK_CR(propertiesPath);
	}

	/**
	 * Creates a properties file to be used by the biolark annotation plugin
	 * 
	 * @param filepath path at which the file will be created
	 * @throws IOException
	 */
	private void generateBiolarkProperties(String filepath) throws IOException {

		//Create properties file
		File biolarkProperties = new File(filepath);
		String biolarkRoot = biolarkProperties.getParentFile()
				.getAbsolutePath();

		// Write to file
		FileOutputStream fos = new FileOutputStream(biolarkProperties);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("logLevel=INFO\n");
		bw.write("longestMatch=FALSE\n");
		bw.write("outputFormat=text\n");
		bw.append("path=").append(biolarkRoot);
		bw.newLine();
		bw.append("inputFolder=").append(biolarkRoot).append("empty\n");
		bw.append("outputFolder=").append(biolarkRoot).append("empty\n");
		bw.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TermAnnotation> annotate(String text)
			throws AnnotationException {

		List<Annotation> biolarkAnnotations = biolark.annotate_plain(text, false);
		List<TermAnnotation> finalAnnotations = new LinkedList<TermAnnotation>();

		// Resolve each termID against Phenotype ontology
		for (Annotation biolarkAnnotation : biolarkAnnotations) {
			String termId = FilenameUtils.getBaseName(
					biolarkAnnotation.getUri()).replace('_', ':');
			OntologyTerm term = ontologies.resolveTerm(termId);
			if (term != null) {
				long start = biolarkAnnotation.getStartOffset();
				long end = biolarkAnnotation.getEndOffset();
				finalAnnotations.add(new TermAnnotation(start , end, term));
			}
		}
		return finalAnnotations;
	}

}
