/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.textanalysis.internal;

import org.xwiki.component.annotation.Role;

import java.util.List;

import au.edu.uq.eresearch.biolark.cr.Annotation;

/**
 * @version $Id$
 * @since 1.0M1
 */
@Role
public interface BiolarkWrapper
{
    /**
     * Annotates given text, and returns a list of BioLark annotations.
     *
     * @param text Text to be annotated
     * @param longestMatch Set to true if biolark should be looking for longest matches
     * @return List of Biolark annotations
     */
    List<Annotation> annotatePlain(String text, boolean longestMatch);
}
