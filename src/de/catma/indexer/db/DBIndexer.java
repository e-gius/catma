/*   
 *   CATMA Computer Aided Text Markup and Analysis
 *   
 *   Copyright (C) 2009-2013  University Of Hamburg
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.catma.indexer.db;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.NamingException;

import de.catma.document.Range;
import de.catma.document.source.SourceDocument;
import de.catma.document.standoffmarkup.usermarkup.TagReference;
import de.catma.document.standoffmarkup.usermarkup.UserMarkupCollection;
import de.catma.indexer.Indexer;
import de.catma.indexer.SpanContext;
import de.catma.indexer.SpanDirection;
import de.catma.indexer.TagsetDefinitionUpdateLog;
import de.catma.indexer.TermInfo;
import de.catma.queryengine.CompareOperator;
import de.catma.queryengine.result.QueryResult;
import de.catma.tag.Property;
import de.catma.tag.TagInstance;
import de.catma.tag.TagLibrary;
import de.catma.tag.TagsetDefinition;

public class DBIndexer implements Indexer {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private TagReferenceIndexer tagReferenceIndexer;
	private SourceDocumentIndexer sourceDocumentIndexer;

	public DBIndexer(Map<String, Object> properties) throws NamingException {
		tagReferenceIndexer = new TagReferenceIndexer();
		sourceDocumentIndexer = new SourceDocumentIndexer();
	}
	
	public void index(SourceDocument sourceDocument)
			throws IOException {
		
		sourceDocumentIndexer.index(sourceDocument);
	}

	public void index(List<TagReference> tagReferences,
			String sourceDocumentID, String userMarkupCollectionID,
			TagLibrary tagLibrary) throws IOException {
		tagReferenceIndexer.index(
				tagReferences, 
				sourceDocumentID, 
				userMarkupCollectionID, 
				tagLibrary);
	}
	
	public void removeSourceDocument(String sourceDocumentID) throws IOException {
		sourceDocumentIndexer.removeSourceDocument(sourceDocumentID);
	}
	
	public void removeUserMarkupCollection(String userMarkupCollectionID) throws IOException {
		tagReferenceIndexer.removeUserMarkupCollection(userMarkupCollectionID);
	}
	
	public void removeTagReferences(
			List<TagReference> tagReferences) throws IOException {
		tagReferenceIndexer.removeTagReferences(tagReferences);
	}
	
	public void reindex(TagsetDefinition tagsetDefinition,
			TagsetDefinitionUpdateLog tagsetDefinitionUpdateLog,
			UserMarkupCollection userMarkupCollection)
			throws IOException {
		logger.info(
			"reindexing tagsetdefinition " + tagsetDefinition 
			+ " in umc " + userMarkupCollection);
		
		tagReferenceIndexer.reindex(
				tagsetDefinition, tagsetDefinitionUpdateLog, 
				userMarkupCollection);
	}
	
	public QueryResult searchPhrase(List<String> documentIdList,
			String phrase, List<String> termList, int limit) throws IOException {
		try {
			PhraseSearcher phraseSearcher = new PhraseSearcher();
			
			return phraseSearcher.search(documentIdList, phrase, termList, limit);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	public QueryResult searchWildcardPhrase(List<String> documentIdList,
			List<String> termList, int limit) throws IOException {
		try {
			PhraseSearcher phraseSearcher = new PhraseSearcher();
			
			return phraseSearcher.searchWildcard(documentIdList, termList, limit);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}

	public QueryResult searchTagDefinitionPath(List<String> userMarkupCollectionIdList, 
			String tagDefinitionPath) throws IOException {
		
		try {
			TagDefinitionSearcher tagSearcher = new TagDefinitionSearcher();
		
			return tagSearcher.search(userMarkupCollectionIdList, tagDefinitionPath);
		} catch (NamingException e) {
			throw new IOException(e);
		}

	}
	
	public QueryResult searchProperty(
			List<String> userMarkupCollectionIdList, Set<String> propertyDefinitionIDs,
			String propertyName, String propertyValue, String tagValue) throws IOException {

		try {
			TagDefinitionSearcher tagSearcher = new TagDefinitionSearcher();
			return tagSearcher.searchProperties(
					userMarkupCollectionIdList, propertyDefinitionIDs, 
					propertyName, propertyValue, tagValue);
		} catch (NamingException e) {
			throw new IOException(e);
		}
		
	}

	public QueryResult searchFreqency(
			List<String> documentIdList, 
			CompareOperator comp1, int freq1,
			CompareOperator comp2, int freq2) throws IOException {
		try {
			FrequencySearcher freqSearcher = new FrequencySearcher();
			return freqSearcher.search(documentIdList, comp1, freq1, comp2, freq2);
		} catch (NamingException e) {
			throw new IOException(e);
		}
	}

	public SpanContext getSpanContextFor(
			String sourceDocumentId, Range range, int spanContextSize,
			SpanDirection direction) throws IOException {
		try {
			CollocationSearcher collocationSearcher = 
					new CollocationSearcher();
			
			return collocationSearcher.getSpanContextFor(
					sourceDocumentId, range, spanContextSize, direction);
		}
		catch (NamingException ne) {
			throw new IOException(ne);
		}
	}
	
	public QueryResult searchCollocation(QueryResult baseResult,
			QueryResult collocationConditionResult, int spanContextSize,
			SpanDirection direction) throws IOException {
		try {
			CollocationSearcher collocationSearcher = 
					new CollocationSearcher();
			return collocationSearcher.search(
				baseResult, collocationConditionResult, spanContextSize, direction);
		}
		catch (NamingException ne) {
			throw new IOException(ne);
		}
	}
	
	public List<TermInfo> getTermInfosFor(String sourceDocumentId, Range range) throws IOException {
		try {
			CollocationSearcher collocationSearcher = 
					new CollocationSearcher();
			return collocationSearcher.getTermInfosFor(sourceDocumentId, range);
		}
		catch (NamingException ne) {
			throw new IOException(ne);
		}
	}

	
	public void updateIndex(TagInstance tagInstance, Property property) 
			throws IOException {
		tagReferenceIndexer.reIndexProperty(tagInstance, property);
	}
	

	public void removeUserMarkupCollections(
			Collection<String> usermarkupCollectionIDs) throws IOException {
		//TODO: optimize with jooq
		for (String userMarkupColl : usermarkupCollectionIDs) {
			removeUserMarkupCollection(userMarkupColl);
		}
		
	}
	
	public void close() { /*noop sessionfactory is closed by repository*/ }

}
