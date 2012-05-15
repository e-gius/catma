package de.catma.queryengine.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupedQueryResultSet implements QueryResult {
	private static class GroupedQueryResultListIterator 
		implements Iterator<QueryResultRow>{
		
		private Iterator<GroupedQueryResult> groupedQueryResultIterator;
		private Iterator<QueryResultRow> currentResultRowIterator;
		
		public GroupedQueryResultListIterator(
				GroupedQueryResultSet groupedQueryResultSet) {
			this.groupedQueryResultIterator = 
					groupedQueryResultSet.groupedQueryResults.iterator();
		}
		public boolean hasNext() {
			if ((this.currentResultRowIterator == null) 
					|| (!this.currentResultRowIterator.hasNext())) {
				
				while (this.groupedQueryResultIterator.hasNext()
						&& ((this.currentResultRowIterator == null) 
								|| !currentResultRowIterator.hasNext())) {
					this.currentResultRowIterator = 
							groupedQueryResultIterator.next().iterator();
				}
				
				if (currentResultRowIterator != null) {
					return currentResultRowIterator.hasNext();
				}
				
				return false;
			}
			return true;
		}
		public QueryResultRow next() {
			if (hasNext()) {
				return currentResultRowIterator.next();
			}
			return null;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private Set<GroupedQueryResult> groupedQueryResults;
	
	public GroupedQueryResultSet() {
		groupedQueryResults = new HashSet<GroupedQueryResult>();
	}
	
	public Iterator<QueryResultRow> iterator() {
		return new GroupedQueryResultListIterator(this);
	}
	
	public Set<GroupedQueryResult> asGroupedQueryResultSet() {
		return groupedQueryResults;
	}
	
	public void add(GroupedQueryResult groupedQueryResult) {
		groupedQueryResults.add(groupedQueryResult);
	}

	public boolean addAll(Collection<? extends GroupedQueryResult> c) {
		return groupedQueryResults.addAll(c);
	}

	public static Set<GroupedQueryResult> asGroupedQueryResultSet(Iterable<QueryResultRow> source) {
		HashMap<String, PhraseResult> phraseResultMapping = 
				new HashMap<String, PhraseResult>();
		
		for (QueryResultRow row : source) {
			if (row.getPhrase() == null) {
				throw new UnsupportedOperationException(
					"The rows in this QueryResultRowArray are not phrase based, " +
					"don't know how to group!");
			}
			if (!phraseResultMapping.containsKey(row.getPhrase())) {
				phraseResultMapping.put(
					row.getPhrase(), new PhraseResult(row.getPhrase()));
			}
			
			phraseResultMapping.get(row.getPhrase()).addQueryResultRow(row);
		}
		
		Set<GroupedQueryResult> groupedQueryResults = 
				new HashSet<GroupedQueryResult>();
		
		groupedQueryResults.addAll(phraseResultMapping.values());
		
		return groupedQueryResults;	
	}
}
