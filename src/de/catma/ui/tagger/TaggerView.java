package de.catma.ui.tagger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;

import de.catma.CatmaApplication;
import de.catma.document.Corpus;
import de.catma.document.Range;
import de.catma.document.repository.Repository;
import de.catma.document.repository.Repository.RepositoryChangeEvent;
import de.catma.document.source.SourceDocument;
import de.catma.document.standoffmarkup.usermarkup.TagReference;
import de.catma.document.standoffmarkup.usermarkup.UserMarkupCollection;
import de.catma.document.standoffmarkup.usermarkup.UserMarkupCollectionReference;
import de.catma.indexer.IndexedRepository;
import de.catma.tag.TagDefinition;
import de.catma.tag.TagInstance;
import de.catma.tag.TagLibrary;
import de.catma.tag.TagManager;
import de.catma.tag.TagsetDefinition;
import de.catma.ui.analyzer.AnalyzerProvider;
import de.catma.ui.client.ui.tagger.shared.ClientTagInstance;
import de.catma.ui.client.ui.tagger.shared.TextRange;
import de.catma.ui.tabbedview.ClosableTab;
import de.catma.ui.tagger.Tagger.TaggerListener;
import de.catma.ui.tagger.pager.Pager;
import de.catma.ui.tagger.pager.PagerComponent;
import de.catma.ui.tagger.pager.PagerComponent.PageChangeListener;
import de.catma.ui.tagmanager.ColorButtonColumnGenerator.ColorButtonListener;

public class TaggerView extends VerticalLayout 
	implements TaggerListener, ClosableTab {
	
	private SourceDocument sourceDocument;
	private Tagger tagger;
	private Pager pager;
	private MarkupPanel markupPanel;
	private boolean init = true;
	private TagManager tagManager;
	private int taggerID;
	private Button btAnalyze;
	private Repository repository;
	private PropertyChangeListener sourceDocChangedListener;
	private PagerComponent pagerComponent;
	
	public TaggerView(
			int taggerID, TagManager tagManager, 
			SourceDocument sourceDocument, Repository repository, 
			PropertyChangeListener sourceDocChangedListener) {
		this.taggerID = taggerID;
		this.tagManager = tagManager;
		this.repository = repository;
		this.sourceDocument = sourceDocument;
		this.sourceDocChangedListener = sourceDocChangedListener;
		initComponents();
		initActions();
		initListeners();
	}

	private void initListeners() {
		repository.addPropertyChangeListener(
			RepositoryChangeEvent.sourceDocumentChanged,
			sourceDocChangedListener);
	}

	private void initActions() {
		btAnalyze.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				Corpus corpus = new Corpus(sourceDocument.toString());
				corpus.addSourceDocument(sourceDocument);
				for (UserMarkupCollection umc : 
					markupPanel.getUserMarkupCollections()) {
					UserMarkupCollectionReference userMarkupCollRef =
							sourceDocument.getUserMarkupCollectionReference(
									umc.getId());
					if (userMarkupCollRef != null) {
						corpus.addUserMarkupCollectionReference(
								userMarkupCollRef);
					}
				}
				//TODO: add static markup colls
				
				((AnalyzerProvider)getApplication()).analyze(
						corpus, (IndexedRepository)markupPanel.getRepository());
			}
		});
		
	}

	private void initComponents() {
		setSizeFull();
		
		VerticalLayout taggerPanel = new VerticalLayout();
		taggerPanel.setSpacing(true);
		
		pager = new Pager(taggerID, 80, 30);
		
		tagger = new Tagger(taggerID, pager, this);
		
		tagger.setSizeFull();
		taggerPanel.addComponent(tagger);

		HorizontalLayout actionPanel = new HorizontalLayout();
		actionPanel.setSpacing(true);
		taggerPanel.addComponent(actionPanel);

		pagerComponent = new PagerComponent(
				pager, new PageChangeListener() {
					
			public void pageChanged(int number) {
				tagger.setPage(number);
			}
		});
		
		actionPanel.addComponent(pagerComponent);
		
		btAnalyze = new Button("Analyze Document");
		btAnalyze.setEnabled(repository instanceof IndexedRepository);
		actionPanel.addComponent(btAnalyze);
		
		markupPanel = new MarkupPanel(
				tagManager,
				repository,
				new ColorButtonListener() {
					
					private boolean enabled = false;
			
					public void colorButtonClicked(TagDefinition tagDefinition) {
						if (enabled) {
							tagger.addTagInstanceWith(tagDefinition);
						}
						else {
							getWindow().showNotification(
	                                "Information",
	                                "Please select a User Markup Collection "
	                                + " to store your markup first!<br>"
	                                + "See 'Currently active Markup Colletions'.");
						}
					}
					
					public void setEnabled(boolean enabled) {
						this.enabled = enabled;
					}
				},
				new PropertyChangeListener() {
					
					public void propertyChange(PropertyChangeEvent evt) {
						boolean selected = evt.getNewValue() != null;
						@SuppressWarnings("unchecked")
						List<TagReference> tagReferences = 
							(List<TagReference>)(
									selected?evt.getNewValue():evt.getOldValue());
						
						tagger.setVisible(tagReferences, selected);
					}
				});
		
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		splitPanel.addComponent(taggerPanel);
		splitPanel.addComponent(markupPanel);
		addComponent(splitPanel);
	}

	public SourceDocument getSourceDocument() {
		return sourceDocument;
	}
	
	@Override
	public void attach() {
		super.attach();
		if (init) {
			WebApplicationContext context = 
					((WebApplicationContext) getApplication().getContext());
			WebBrowser wb = context.getBrowser();
			// TODO: should be changeable by the user:
			float lines = (wb.getScreenHeight()/3)/12;
			pager.setMaxPageLengthInLines(Math.round(lines));
			
			try {
				tagger.setText(sourceDocument.getContent());
			} catch (IOException e) {
				((CatmaApplication)getApplication()).showAndLogError(
					"Error showing Source Document!", e);
			}
			init = false;
		}
	}

	public void openUserMarkupCollection(
			UserMarkupCollection userMarkupCollection) {
		markupPanel.openUserMarkupCollection(userMarkupCollection);
	}

	public void close() {
		markupPanel.close();
		repository.removePropertyChangeListener(
				RepositoryChangeEvent.sourceDocumentChanged,
				sourceDocChangedListener);
		sourceDocChangedListener = null;
	}
	
	public void tagInstanceAdded(
			ClientTagInstance clientTagInstance) {
		TagLibrary tagLibrary =
				markupPanel.getCurrentWritableUserMarkupCollection().getTagLibrary();
		
		if (tagLibrary.getTagDefinition(clientTagInstance.getTagDefinitionID())
				== null) {
			TagsetDefinition tagsetDef =
					markupPanel.getTagsetDefinition(
							clientTagInstance.getTagDefinitionID());
			//FIXME: somehow this does not show up in the MarkupCollectionsPannel
			tagManager.addTagsetDefinition(
					tagLibrary, new TagsetDefinition(tagsetDef));
		}
		
		TagDefinition tagDef = 
				tagLibrary.getTagDefinition(
						clientTagInstance.getTagDefinitionID());
		
		TagInstance ti = 
			new TagInstance(clientTagInstance.getInstanceID(), tagDef);
		
		List<TagReference> tagReferences = new ArrayList<TagReference>();
		
		try {
			for (TextRange tr : clientTagInstance.getRanges()) {
				Range r = new Range(tr.getStartPos(), tr.getEndPos());
				TagReference ref = 
						new TagReference(ti, sourceDocument.getID(), r);
				tagReferences.add(ref);
			}
			markupPanel.addTagReferences(tagReferences);
		} catch (URISyntaxException e) {
			((CatmaApplication)getApplication()).showAndLogError(
				"Error adding Tags!", e);
		}
	}
	
	public void tagInstanceRemoved(String instanceID) {
		markupPanel.removeTagInstance(instanceID);
	}

	public void show(Range range) {
		int pageNumber = pager.getStartPageNumberFor(range);
		pagerComponent.setPage(pageNumber);
		TextRange tr = pager.getCurrentPage().getRelativeRangeFor(range);
		tagger.highlight(tr);
	}
	
	public void addClickshortCuts() { /* noop*/	}
	
	public void removeClickshortCuts() { /* noop*/ }

}
