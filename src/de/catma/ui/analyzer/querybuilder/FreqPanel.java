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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalSplitPanel;

import de.catma.queryengine.QueryOptions;
import de.catma.queryengine.querybuilder.QueryTree;
import de.catma.ui.data.util.IntegerValueValidator;
import de.catma.ui.dialog.wizard.ToggleButtonStateListener;

public class FreqPanel extends AbstractSearchPanel {
	
	private ResultPanel resultPanel;
	private ComboBox freqComparatorCombo;
	private TextField freq1Input;
	private TextField freq2Input;
	private Label andLabel;
	private FreqComparator exactlyFreqComp;


	public FreqPanel(ToggleButtonStateListener toggleButtonStateListener,
			QueryTree queryTree, QueryOptions queryOptions, TagsetDefinitionDictionary tagsetDefinitionDictionary) {
		super(toggleButtonStateListener, queryTree, queryOptions, tagsetDefinitionDictionary);
		initComponents();
		initActions();
		freqComparatorCombo.setValue(exactlyFreqComp);
	}

	
	private void initActions() {

		resultPanel.addBtShowInPreviewListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				showInPreview();
			}

		});
		
		freqComparatorCombo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				FreqComparator curComparator = 
						(FreqComparator)freqComparatorCombo.getValue(); 
				
				andLabel.setEnabled(curComparator.isRange());
				freq2Input.setEnabled(curComparator.isRange());
				freq2Input.setRequired(curComparator.isRange());
			}
		});
	}

	private void showInPreview() {
		if (freq1Input.isValid() && freq2Input.isValid() && freqComparatorCombo.isValid()) {
			
			StringBuilder builder = new StringBuilder("freq ");
			FreqComparator curComparator = 
					(FreqComparator)freqComparatorCombo.getValue(); 
			builder.append(curComparator.getComparator());
			builder.append(" ");
			builder.append(freq1Input.getValue());
			if (curComparator.isRange()) {
				builder.append("-");
				builder.append(freq2Input.getValue());
			}
			
			if (curQuery != null) {
				queryTree.removeLast();
			}
			curQuery = builder.toString();
			resultPanel.setQuery(curQuery);
			
			queryTree.add(curQuery);
			onFinish = !isComplexQuery();
			onAdvance = true;
			toggleButtonStateListener.stepChanged(this);
		}
		else {
			onFinish = false;
			onAdvance = false;
		}		

		
	}

	private void initComponents() {
		VerticalSplitPanel splitPanel = new VerticalSplitPanel();
		
		Component searchPanel = createSearchPanel();
		splitPanel.addComponent(searchPanel);
		resultPanel = new ResultPanel(queryOptions);
		splitPanel.addComponent(resultPanel);
		addComponent(splitPanel);
		
		super.initSearchPanelComponents(splitPanel);
	}


	private Component createSearchPanel() {
		HorizontalLayout searchPanel = new HorizontalLayout();
		searchPanel.setMargin(true, false, false, false);
		
		searchPanel.setSpacing(true);
	
		List<FreqComparator> freqComparators = new ArrayList<FreqComparator>();
		exactlyFreqComp = 
				new FreqComparator("exactly", "=");
		freqComparators.add(exactlyFreqComp);
		
		freqComparators.add(
                new FreqComparator("greaterThan", ">"));
		freqComparators.add(
                new FreqComparator("lessThan", ">"));
		freqComparators.add(
                new FreqComparator("greaterOrEqualThan", ">="));
		freqComparators.add(
                new FreqComparator("lessOrEqualThan", "<="));
		freqComparators.add(
                new FreqComparator("between", "=", true));
		
		searchPanel.addComponent(new Label("The word shall appear"));
		
		freqComparatorCombo = new ComboBox(null, freqComparators);
		freqComparatorCombo.setImmediate(true);
		freqComparatorCombo.setNewItemsAllowed(false);
		freqComparatorCombo.setNullSelectionAllowed(false);
		freqComparatorCombo.setInvalidAllowed(false);
		
		searchPanel.addComponent(freqComparatorCombo);
		
		freq1Input = new TextField();
		freq1Input.setImmediate(true);
		freq1Input.addValidator(new IntegerValueValidator(true, false));
		freq1Input.setInvalidAllowed(false);
		freq1Input.setRequired(true);
		
		searchPanel.addComponent(freq1Input);
		searchPanel.setExpandRatio(freq1Input, 0.5f);
		
		andLabel = new Label("and");
		searchPanel.addComponent(andLabel);
		
		freq2Input = new TextField();
		freq2Input.setImmediate(true);
		freq2Input.addValidator(new IntegerValueValidator(false, false));
		freq2Input.setInvalidAllowed(false);
		searchPanel.addComponent(freq2Input);
		searchPanel.setExpandRatio(freq2Input, 0.5f);
		
		searchPanel.addComponent(new Label("times."));

		return searchPanel;
	}

	@Override
	public String getCaption() {
		return "How frequent shall it be?";
	}
	
	@Override
	public String toString() {
		return "by frequency";
	}
	
	
}
