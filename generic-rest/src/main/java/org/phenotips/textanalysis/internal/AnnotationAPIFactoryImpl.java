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

import org.xwiki.component.annotation.Component;

import java.net.MalformedURLException;

/**
 * Implementation to construct AnnotationAPI instances.
 *
 * @version $Id$
 */
@Component
public class AnnotationAPIFactoryImpl implements AnnotationAPIFactory
{
    @Override
    public AnnotationAPI build(String serviceURL)
    {
        try {
            return new AnnotationAPIImpl(serviceURL);
        } catch (MalformedURLException e) {
            /* TODO: Is it reasonable to assume this can only happen on hacker error? */
            throw new RuntimeException(e);
        }
    }
}
