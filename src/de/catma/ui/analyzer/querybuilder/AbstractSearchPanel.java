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
package de.catma.ui.analyzer.querybuilder;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.catma.queryengine.QueryOptions;
import de.catma.queryengine.querybuilder.QueryTree;
import de.catma.ui.dialog.wizard.DynamicWizardStep;
import de.catma.ui.dialog.wizard.ToggleButtonStateListener;

public abstract class AbstractSearchPanel extends VerticalLayout implements DynamicWizardStep {

	private CheckBox cbComplexQuery;
	protected ToggleButtonStateListener toggleButtonStateListener;
	protected QueryTree queryTree;
	protected QueryOptions queryOptions;
	protected TagsetDefinitionDictionary tagsetDefinitionDictionary;
	
	private ComplexTypeSelectionPanel complexTypeSelectionPanel;
	private SearchTypeSelectionPanel searchTypeSelectionPanel;
	
	protected boolean onFinish;
	protected boolean onFinishOnly;
	protected boolean onAdvance;
	protected String curQuery = null;


	public AbstractSearchPanel(
			ToggleButtonStateListener toggleButtonStateListener, 
			QueryTree queryTree, QueryOptions queryOptions,
			TagsetDefinitionDictionary tagsetDefinitionDictionary) {
		this.toggleButtonStateListener = toggleButtonStateListener;
		this.queryTree = queryTree;
		this.queryOptions = queryOptions;
		this.tagsetDefinitionDictionary = tagsetDefinitionDictionary;
		onFinish = false;
		onFinishOnly = true;
		onAdvance = false;
	}

	protected void initSearchPanelComponents(Component content) {
		setSizeFull();
		setSpacing(true);
		cbComplexQuery = new CheckBox("continue to build a complex query");
		cbComplexQuery.setImmediate(true);
		addComponent(cbComplexQuery);
		
		setExpandRatio(content, 0.99f);
		setExpandRatio(cbComplexQuery, 0.01f);
		setComponentAlignment(cbComplexQuery, Alignment.BOTTOM_RIGHT);
		
		initSearchPanelActions();
	}

	private void initSearchPanelActions() {
		cbComplexQuery.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if (cbComplexQuery.booleanValue()) {
					if (complexTypeSelectionPanel == null) {
						complexTypeSelectionPanel = 
								new ComplexTypeSelectionPanel(queryTree);
						searchTypeSelectionPanel = new SearchTypeSelectionPanel(
								toggleButtonStateListener, queryTree, queryOptions, 
								true, tagsetDefinitionDictionary);
					}
					toggleButtonStateListener.getWizard().addStep(
							complexTypeSelectionPanel);
					toggleButtonStateListener.getWizard().addStep(
							searchTypeSelectionPanel);
				}
				else {
					if (complexTypeSelectionPanel != null) {
						toggleButtonStateListener.getWizard().removeStep(
								searchTypeSelectionPanel);
						toggleButtonStateListener.getWizard().removeStep(
								complexTypeSelectionPanel);
					}
				}
				
				onFinishOnly = !cbComplexQuery.booleanValue();
				onFinish = (!cbComplexQuery.booleanValue() 
							&& (curQuery != null) && !curQuery.isEmpty());
				
				toggleButtonStateListener.stepChanged(AbstractSearchPanel.this);
			}
			
		});
	}

	public void addCbComplexQueryListener(ClickListener listener) {
		cbComplexQuery.addListener(listener);
	}
	
	public void stepActivated(boolean forward) {
		if (forward) {
			if ((curQuery != null) && !curQuery.isEmpty()) {
				queryTree.add(curQuery);
			}
		}
	}
	
	public void stepDeactivated(boolean forward) {		
		if (!forward) {
			cbComplexQuery.setValue(Boolean.FALSE);
			if (curQuery != null) {
				queryTree.removeLast();
			}
		}
	}


	public void stepAdded() {/* noop */}
	
	public boolean onAdvance() {
		return onAdvance;
	}

	public boolean onBack() {
		return true;
	}

	public boolean onFinish() {
		return onFinish;
	}

	public boolean onFinishOnly() {
		return onFinishOnly;
	}

	public boolean isComplexQuery() {
		return cbComplexQuery.booleanValue();
	}
	
	@Override
	public void attach() {
		super.attach();
		getParent().setHeight("100%");
	}
	
	public Component getContent() {
		return this;
	}


}
