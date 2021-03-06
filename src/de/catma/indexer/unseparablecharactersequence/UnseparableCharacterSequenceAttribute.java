/*
 * CATMA Computer Aided Text Markup and Analysis
 *
 *    Copyright (C) 2008-2010  University Of Hamburg
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.catma.indexer.unseparablecharactersequence;

import org.apache.lucene.util.Attribute;

/**
 * An attribute that states if a sequence is separable or not.
 *
 * @author Marco Petris
 *
 */
public interface UnseparableCharacterSequenceAttribute extends Attribute {
    /**
     * @param b true-> not separable
     */
    void setUnseparable(boolean b);

    /**
     * @return true->is not separable
     */
    boolean isUnseparable();
}
