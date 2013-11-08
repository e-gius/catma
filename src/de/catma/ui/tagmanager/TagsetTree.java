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
package de.catma.ui.tagmanager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.Action;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;

import de.catma.tag.PropertyDefinition;
import de.catma.tag.PropertyPossibleValueList;
import de.catma.tag.TagDefinition;
import de.catma.tag.TagLibrary;
import de.catma.tag.TagManager;
import de.catma.tag.TagManager.TagManagerEvent;
import de.catma.tag.TagsetDefinition;
import de.catma.tag.Version;
import de.catma.ui.EndorsedTreeTable;
import de.catma.ui.dialog.FormDialog;
import de.catma.ui.dialog.PropertyCollection;
import de.catma.ui.dialog.SaveCancelListener;
import de.catma.ui.dialog.StringProperty;
import de.catma.ui.dialog.TagDefinitionFieldFactory;
import de.catma.ui.tagmanager.ColorButtonColumnGenerator.ColorButtonListener;
import de.catma.util.ColorConverter;
import de.catma.util.IDGenerator;
import de.catma.util.Pair;

public class TagsetTree extends HorizontalLayout {
	
	private static enum TagTreePropertyName {
		caption,
		icon,
		color,
		;
	}

	private boolean init = true;
	private TreeTable tagTree;
	private Button btInsertTagset;
	private Button btRemoveTagset;
	private Button btEditTagset;
	private Button btInsertTag;
	private Button btRemoveTag;
	private Button btEditTag;
	private Button btInsertProperty;
	private Button btRemoveProperty;
	private Button btEditProperty;
	private boolean withTagsetButtons;
	private ColorButtonListener colorButtonListener;
	private TagManager tagManager;
	private TagLibrary tagLibrary;
	private PropertyChangeListener tagsetDefinitionChangedListener;
	private PropertyChangeListener tagDefinitionChangedListener;
	private PropertyChangeListener userPropertyDefinitionChangedListener;
	private boolean withButtonPanel;
	private Application application;
	private Button btReload;

	public TagsetTree(TagManager tagManager, TagLibrary tagLibrary) {
		this(tagManager, tagLibrary, true, null);
	}
	
	public TagsetTree(
			TagManager tagManager, final TagLibrary tagLibrary, 
			boolean withTagsetButtons, 
			ColorButtonListener colorButtonListener) {
		this(tagManager, tagLibrary, withTagsetButtons, true, colorButtonListener);
	}
	
	public TagsetTree(
			TagManager tagManager, final TagLibrary tagLibrary, 
			boolean withTagsetButtons, 
			boolean withButtonPanel,
			ColorButtonListener colorButtonListener) {
		this.tagManager = tagManager;
		this.tagLibrary = tagLibrary;
		if (withTagsetButtons) {
			tagManager.addTagLibrary(tagLibrary);
		}
		this.withTagsetButtons = withTagsetButtons;
		this.withButtonPanel = withButtonPanel;
		this.colorButtonListener = colorButtonListener;
	}
	
	@Override
	public void attach() {
		super.attach();
		if (init){
			application = getApplication();
			initComponents();
			initActions();
			init = false;
		}
	}
	
	private void initActions() {
		if (withTagsetButtons) {

			tagsetDefinitionChangedListener = 
					new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getOldValue()==null) {
						@SuppressWarnings("unchecked")
						Pair<TagLibrary, TagsetDefinition> addOperationResult = 
							(Pair<TagLibrary,TagsetDefinition>)evt.getNewValue();
						
						if (tagLibrary.equals(addOperationResult.getFirst())) {
							addTagsetDefinition(addOperationResult.getSecond());
						}
					}
					else if (evt.getNewValue() == null) {
						@SuppressWarnings("unchecked")
						Pair<TagLibrary, TagsetDefinition> removeOperationResult = 
							(Pair<TagLibrary,TagsetDefinition>)evt.getOldValue();
						
						if (tagLibrary.equals(removeOperationResult.getFirst())) {
							TagsetDefinition tagsetDef = 
									removeOperationResult.getSecond();
							removeTagsetDefinitionFromTree(tagsetDef);
						}
					}
					else {
						TagsetDefinition tagsetDefinition = 
								(TagsetDefinition)evt.getNewValue();
						if (tagTree.containsId(tagsetDefinition)) {
							tagTree.getContainerProperty(
								tagsetDefinition, TagTreePropertyName.caption).setValue(
										tagsetDefinition.getName());
						}
					}
				}
			};
			
			this.tagManager.addPropertyChangeListener(
					TagManagerEvent.tagsetDefinitionChanged,
					tagsetDefinitionChangedListener);
			
			this.btInsertTagset.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					handleInsertTagsetDefinitionRequest();
				}
			});
			
			this.btEditTagset.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					handleEditTagsetDefinitionRequest();
				}
			});

			btRemoveTagset.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					handleRemoveTagsetDefinitionRequest();
				}
			});
		}
		
		tagDefinitionChangedListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object oldValue = evt.getOldValue();
				Object newValue = evt.getNewValue();
				if ((oldValue == null) && (newValue == null)) {
					return;
				}
				
				if (oldValue == null) {
					@SuppressWarnings("unchecked")
					Pair<TagsetDefinition, TagDefinition> addOperationResult =
							(Pair<TagsetDefinition, TagDefinition>)evt.getNewValue();
					TagsetDefinition tagsetDefinition = 
							addOperationResult.getFirst();
					TagDefinition tagDefinition = 
							addOperationResult.getSecond();
					if (tagTree.containsId(tagsetDefinition)) {
						addTagDefinition(tagDefinition);
						establishHierarchy(tagsetDefinition, tagDefinition);
						configureChildren(tagDefinition);
					}
				}
				else if (newValue == null) {
					@SuppressWarnings("unchecked")
					Pair<TagsetDefinition, TagDefinition> removeOperationResult = 
						(Pair<TagsetDefinition, TagDefinition>)evt.getOldValue();
					TagDefinition td = removeOperationResult.getSecond();
					if (tagTree.containsId(td)) {
						removeTagDefinitionFromTree(td, removeOperationResult.getFirst());
					}
				}
				else {
					TagDefinition tagDefinition = (TagDefinition)evt.getNewValue();

					Property prop = tagTree.getContainerProperty(
							tagDefinition, 
							TagTreePropertyName.caption);
					if (prop != null) {
						prop.setValue(tagDefinition.getName());
					}
				}
			}
		};
		
		this.tagManager.addPropertyChangeListener(
				TagManagerEvent.tagDefinitionChanged,
				tagDefinitionChangedListener);
		
		userPropertyDefinitionChangedListener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				Object oldValue = evt.getOldValue();
				Object newValue = evt.getNewValue();

				if (oldValue == null) { // insert
					
					@SuppressWarnings("unchecked")
					Pair<PropertyDefinition, TagDefinition> newPair = 
							(Pair<PropertyDefinition, TagDefinition>)newValue;
					if (tagTree.containsId(newPair.getSecond())) {
						addUserDefinedPropertyDefinition(
								newPair.getFirst(), newPair.getSecond());
					}
				}
				else if (newValue == null) { // delete
					@SuppressWarnings("unchecked")
					Pair<PropertyDefinition, TagDefinition> oldPair = 
							(Pair<PropertyDefinition, TagDefinition>)oldValue;
					if (tagTree.containsId(oldPair.getFirst())) {
						removeUserDefinedPropertyDefinitionFromTree(
								oldPair.getFirst());
					}
				}
				else { // update
					
					PropertyDefinition pd = (PropertyDefinition)evt.getNewValue();
					if (tagTree.containsId(pd)) {
						Property contProp = tagTree.getContainerProperty(
							pd, 
							TagTreePropertyName.caption);
						
						if (contProp != null) {
							contProp.setValue(pd.getName());
						}
					}
				}
			}
		};
		
		this.tagManager.addPropertyChangeListener(
				TagManagerEvent.userPropertyDefinitionChanged,
				userPropertyDefinitionChangedListener);
		
		btInsertTag.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				handleInsertTagDefinitionRequest();
			}
		});
		
		btRemoveTag.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				handleRemoveTagDefinitionRequest();
			}

		});

		btEditTag.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				handleEditTagDefinitionRequest();
			}
		});
		
		btInsertProperty.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				handleInsertPropertyDefinitionRequest();
			}
		});
		
		btEditProperty.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				handleEditPropertyDefinitionRequest();
			}
		});
		
		btRemoveProperty.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				handleDeletePropertyDefinitionRequest();
			}
		});
		
		tagTree.addListener(
				new ButtonStateManager(
						withTagsetButtons,
						btRemoveTagset, btEditTagset, 
						btInsertTag, btRemoveTag, btEditTag, 
						btInsertProperty, btRemoveProperty, btEditProperty));
	}
	
	private void removeUserDefinedPropertyDefinitionFromTree(
			PropertyDefinition propertyDefinition) {
		Object parent = tagTree.getParent(propertyDefinition);
		this.tagTree.removeItem(propertyDefinition);
		if (!tagTree.hasChildren(parent)) {
			tagTree.setChildrenAllowed(parent, false);
		}
		
	}

	private void handleDeletePropertyDefinitionRequest() {
		final Object selectedValue = tagTree.getValue();
		if (!(selectedValue instanceof PropertyDefinition)) {
			return;
		}
		
		final PropertyDefinition pd = (PropertyDefinition)selectedValue;
		final TagDefinition parent = (TagDefinition)tagTree.getParent(pd);

		ConfirmDialog.show(
				getApplication().getMainWindow(), 
				"Delete User Defined Property",
				"Are you sure you want to delete this Property?", "Yes", "No",
				new ConfirmDialog.Listener() {
					
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							tagManager.removeUserDefinedPropertyDefinition(pd, parent);
						}
					}
				});
	}

	private void handleEditPropertyDefinitionRequest() {
		final Object selectedValue = tagTree.getValue();
		if (!(selectedValue instanceof PropertyDefinition)) {
			return;
		}
		final PropertyDefinition pd = (PropertyDefinition)selectedValue;
		final TagDefinition parent = (TagDefinition)tagTree.getParent(pd);
		
		PropertyDefinitionDialog propertyDefinitionDialog = 
				new PropertyDefinitionDialog(
					"Edit Property", pd, 
					new SaveCancelListener<PropertyDefinition>() {
						
						public void cancelPressed() {}
						public void savePressed(PropertyDefinition result) {
							tagManager.updateUserDefinedPropertyDefinition(
									parent, result);
						}
					});
			propertyDefinitionDialog.show(getApplication().getMainWindow());
		
		
	}

	private void handleInsertPropertyDefinitionRequest() {
		
		final Object selectedParent = 
				tagTree.getValue();
		
		if (selectedParent == null) {
			return;
		}
		
		PropertyDefinitionDialog propertyDefinitionDialog = 
			new PropertyDefinitionDialog(
				"Create Property", 
				new SaveCancelListener<PropertyDefinition>() {
					
					public void cancelPressed() {}
					public void savePressed(PropertyDefinition result) {
						TagDefinition td = (TagDefinition)selectedParent;
						tagManager.addUserDefinedPropertyDefinition(td, result);
					}
				});
		propertyDefinitionDialog.show(getApplication().getMainWindow());
	}

	private void handleEditTagDefinitionRequest() {
		Object selValue = tagTree.getValue();
		
		if ((selValue != null) 
			&& (selValue instanceof TagDefinition)) {
			final TagDefinition selTagDefinition = (TagDefinition)selValue;
			final String tagDefNameProp = "name";
			final String tagDefColorProp = "color";
			
			PropertyCollection propertyCollection = 
					new PropertyCollection(tagDefNameProp, tagDefColorProp);
			
			propertyCollection.getItemProperty(tagDefNameProp).setValue(
					selTagDefinition.getName());
			propertyCollection.getItemProperty(tagDefColorProp).setValue(
					ColorConverter.toHex(selTagDefinition.getColor()));
			
			FormDialog<PropertysetItem> tagFormDialog = new FormDialog<PropertysetItem>(
				"Edit Tag",
				propertyCollection,
				new TagDefinitionFieldFactory(tagDefColorProp),
				new SaveCancelListener<PropertysetItem>() {
					public void cancelPressed() {}
					public void savePressed(
							PropertysetItem propertysetItem) {
						
						Property nameProperty =
							propertysetItem.getItemProperty(
									tagDefNameProp);
						
						Property colorProperty =
							propertysetItem.getItemProperty(
									tagDefColorProp);

						tagManager.setTagDefinitionTypeAndColor(
								selTagDefinition, 
								(String)nameProperty.getValue(),
								ColorConverter.toRGBIntAsString(
										(String)colorProperty.getValue()));
					}
				});
			tagFormDialog.show(application.getMainWindow(), "50%");
		}
		
	}

	private void removeTagDefinitionFromTree(
			TagDefinition td, TagsetDefinition tagsetDefinition) {
		
		Collection<Object> children = new ArrayList<Object>();
		if (tagTree.hasChildren(td)) {
			children.addAll(tagTree.getChildren(td));
		}
		for (Object child : children) {
			if (child instanceof TagDefinition) {
				removeTagDefinitionFromTree((TagDefinition)child, tagsetDefinition);
			}
		}
		
		for (PropertyDefinition pd : 
			td.getSystemPropertyDefinitions()) {
			tagTree.removeItem(pd);
		}
		for (PropertyDefinition pd :
			td.getUserDefinedPropertyDefinitions()) {
			tagTree.removeItem(pd);
		}
		Object parentId = tagTree.getParent(td);
		tagTree.removeItem(td);
		if ((parentId != null) && (!tagTree.hasChildren(parentId))) {
			tagTree.setChildrenAllowed(parentId, false);
		}
	}

	private void handleRemoveTagDefinitionRequest() {
		Object selValue = tagTree.getValue();
		
		if ((selValue != null)
				&& (selValue instanceof TagDefinition)) {
			final TagDefinition td = (TagDefinition)selValue;
			
			ConfirmDialog.show(
				application.getMainWindow(),
				"Remove Tag", 
				"Do you really want to delete this Tag " +
				"with all its properties?", "Yes", "No", 
				new ConfirmDialog.Listener() {
					
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							tagManager.removeTagDefinition(
									getTagsetDefinition(td), td);
						}
					}
				});
		}
	}
	
	private void handleInsertTagDefinitionRequest() {
		final String tagDefNameProp = "name";
		final String tagDefColorProp = "color";
		
		PropertyCollection propertyCollection = 
				new PropertyCollection(tagDefNameProp, tagDefColorProp);
		propertyCollection.getItemProperty(tagDefColorProp).setValue(
				ColorConverter.randomHex());

		final Object selectedParent = 
				tagTree.getValue();
		
		if (selectedParent == null) {
			getWindow().showNotification(
				"Info", "Please select a Tagset or parent Tag first!", 
				Notification.TYPE_TRAY_NOTIFICATION);
			return;
		}
		
		FormDialog<PropertysetItem> tagFormDialog =
			new FormDialog<PropertysetItem>(
				"Create new Tag",
				propertyCollection,
				new TagDefinitionFieldFactory(
					tagDefColorProp),
				new SaveCancelListener<PropertysetItem>() {
					public void cancelPressed() {}
					public void savePressed(
							PropertysetItem propertysetItem) {
						
						Property nameProperty =
							propertysetItem.getItemProperty(
									tagDefNameProp);
						
						Property colorProperty =
							propertysetItem.getItemProperty(
									tagDefColorProp);
						
						String baseID = null;
						TagsetDefinition tagsetDefinition = null;

						if (selectedParent instanceof TagsetDefinition) {
							baseID = "";
							tagsetDefinition = 
									(TagsetDefinition)selectedParent;
						}
						else if (selectedParent instanceof TagDefinition) {
							baseID = 
								((TagDefinition)selectedParent).getUuid();
							tagsetDefinition = 
									getTagsetDefinition(
										(TagDefinition)selectedParent);
						}
						else {
							throw new IllegalStateException(
								"a parent of a TagDefinition has to be either a"
								+ "TagDefinition or a TagsetDefinition and not a " 
								+ selectedParent.getClass().getName());
						}
						IDGenerator idGenerator = new IDGenerator();
						TagDefinition tagDefinition = 
								new TagDefinition(
									null,
									idGenerator.generate(),
									(String)nameProperty.getValue(),
									new Version(), 
									(baseID.isEmpty()? null : 
										((TagDefinition)selectedParent).getId()),
									baseID);
						PropertyDefinition colorPropertyDef =
								new PropertyDefinition(
									null,
									idGenerator.generate(),
									PropertyDefinition.SystemPropertyName.
										catma_displaycolor.name(),
									new PropertyPossibleValueList(
										ColorConverter.toRGBIntAsString(
											(String)colorProperty.getValue())));
						tagDefinition.addSystemPropertyDefinition(
								colorPropertyDef);
						tagManager.addTagDefinition(
								tagsetDefinition, 
								tagDefinition);
					}
				});
		tagFormDialog.show(application.getMainWindow(), "50%");
	}

	public TagsetDefinition getTagsetDefinition(TagDefinition tagDefinition) {
		Object parent = tagTree.getParent(tagDefinition);
		if (parent instanceof TagsetDefinition) {
			return (TagsetDefinition)parent;
		}
		else {
			return getTagsetDefinition((TagDefinition)parent);
		}
	}

	private void handleRemoveTagsetDefinitionRequest() {
		Object selValue = tagTree.getValue();
		
		if ((selValue != null)
				&& (selValue instanceof TagsetDefinition)) {
			final TagsetDefinition td = (TagsetDefinition)selValue;
			
			ConfirmDialog.show(
				application.getMainWindow(),
				"Remove Tagset", 
				"Do you really want to delete this Tagset " +
				"with all its Tags?", "Yes", "No", 
				new ConfirmDialog.Listener() {
					
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							tagManager.removeTagsetDefinition(
									tagLibrary, td);
						}
					}
				});
		}
	}

	private void handleInsertTagsetDefinitionRequest() {
		final String tagsetdefinitionnameProperty = "name";
		
		PropertyCollection propertyCollection = 
				new PropertyCollection(tagsetdefinitionnameProperty);

		FormDialog<PropertysetItem> tagsetFormDialog =
			new FormDialog<PropertysetItem>(
				"Create new Tagset",
				propertyCollection,
				new SaveCancelListener<PropertysetItem>() {
					public void cancelPressed() {}
					public void savePressed(
							PropertysetItem propertysetItem) {
						Property property = 
								propertysetItem.getItemProperty(
										tagsetdefinitionnameProperty);
						TagsetDefinition td = 
								new TagsetDefinition(
									null,
									new IDGenerator().generate(), 
									(String)property.getValue(), 
									new Version());
						
						tagManager.addTagsetDefinition(
								tagLibrary, td);
					}
				});
		configureTagsetFormDialog(
				tagsetFormDialog, tagsetdefinitionnameProperty);
		tagsetFormDialog.show(application.getMainWindow());
	}
	
	private void handleEditTagsetDefinitionRequest() {
		final String tagsetdefinitionnameProperty = "name";
		
		Object selValue = tagTree.getValue();
		
		if ((selValue != null)
				&& (selValue instanceof TagsetDefinition)) {
			
			final TagsetDefinition curSelTagsetDefinition =
					(TagsetDefinition)selValue;
			
			PropertyCollection propertyCollection = 
					new PropertyCollection();
			propertyCollection.addItemProperty(
							tagsetdefinitionnameProperty,
							new StringProperty(
								curSelTagsetDefinition.getName()));
			
			FormDialog<PropertysetItem> tagsetFormDialog =
				new FormDialog<PropertysetItem>(
					"Edit Tagset",
					propertyCollection,
					new SaveCancelListener<PropertysetItem>() {
						public void cancelPressed() {}
						public void savePressed(
								PropertysetItem propertysetItem) {
							Property property = 
									propertysetItem.getItemProperty(
										tagsetdefinitionnameProperty);
							
							tagManager.setTagsetDefinitionName(
									curSelTagsetDefinition,
									(String)property.getValue());
						}
					});
			configureTagsetFormDialog(
					tagsetFormDialog, tagsetdefinitionnameProperty);

			tagsetFormDialog.show(application.getMainWindow());
		}
	}

	private void configureTagsetFormDialog(
			FormDialog<PropertysetItem> formDialog, String propertyId) {
		formDialog.getField(
				propertyId).setRequired(true);
		formDialog.getField(
				propertyId).setRequiredError(
						"You have to enter a name!");
	}

	private void initComponents() {
		setSizeFull();

		tagTree = new EndorsedTreeTable();
		tagTree.setImmediate(true);
		tagTree.setSizeFull();
		tagTree.setSelectable(true);
		tagTree.setMultiSelect(false);
		
		tagTree.setContainerDataSource(new HierarchicalContainer());
		
		tagTree.addContainerProperty(
				TagTreePropertyName.caption, String.class, null);
		tagTree.setColumnHeader(TagTreePropertyName.caption, "Tagsets");
		
		tagTree.addContainerProperty(
				TagTreePropertyName.icon, Resource.class, null);

		tagTree.setItemCaptionPropertyId(TagTreePropertyName.caption);
		tagTree.setItemIconPropertyId(TagTreePropertyName.icon);
		tagTree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
	
		tagTree.setVisibleColumns(
				new Object[] {
						TagTreePropertyName.caption});
		
		if (colorButtonListener != null) {
			tagTree.addGeneratedColumn(
				TagTreePropertyName.color,
				new ColorButtonColumnGenerator(colorButtonListener));
			tagTree.setColumnReorderingAllowed(true);
		}
		else {
			tagTree.addGeneratedColumn(
					TagTreePropertyName.color, new ColorLabelColumnGenerator());
		}
		tagTree.setColumnHeader(TagTreePropertyName.color, "Tag Color");
		addComponent(tagTree);
		setExpandRatio(tagTree, 2);
		
		GridLayout buttonGrid = new GridLayout(1, 19);
		buttonGrid.setMargin(true);
		buttonGrid.setSpacing(true);

		buttonGrid.addStyleName("taglibrary-action-grid");
		int buttonGridRowCount = 0;
		
		if (withTagsetButtons) {
			btReload = new Button(""); 
			btReload.setIcon(new ClassResource(
					"ui/resources/icon-reload.gif",
					getApplication()));
			btReload.addStyleName("icon-button");
			buttonGrid.addComponent(btReload);
			buttonGrid.setComponentAlignment(btReload, Alignment.MIDDLE_RIGHT);
			buttonGridRowCount++;

			Label tagsetLabel = new Label();
			tagsetLabel.setIcon(
					new ClassResource(
							"ui/tagmanager/resources/grndiamd.gif", 
							application));
			tagsetLabel.setCaption("Tagset");
			
			buttonGrid.addComponent(tagsetLabel);
			buttonGridRowCount++;
			
			btInsertTagset = new Button("Create Tagset");
			btInsertTagset.setEnabled(true);
			btInsertTagset.setWidth("100%");
			buttonGrid.addComponent(btInsertTagset);
			buttonGridRowCount++;
			
			btRemoveTagset = new Button("Remove Tagset");
			btRemoveTagset.setWidth("100%");
			buttonGrid.addComponent(btRemoveTagset);
			buttonGridRowCount++;
			
			btEditTagset = new Button("Edit Tagset");
			btEditTagset.setWidth("100%");
			buttonGrid.addComponent(btEditTagset);
			buttonGridRowCount++;
		}
		
		Label tagLabel = new Label();
		tagLabel.setIcon(
				new ClassResource(
						"ui/tagmanager/resources/reddiamd.gif", 
						application));
		tagLabel.setCaption("Tag");
		
		buttonGrid.addComponent(
				tagLabel, 0, buttonGridRowCount, 0, buttonGridRowCount+4 );
		buttonGridRowCount+=5;
		
		buttonGrid.setComponentAlignment(tagLabel, Alignment.BOTTOM_LEFT);
		
		btInsertTag = new Button("Create Tag");
		btInsertTag.setWidth("100%");
		if (withTagsetButtons) {
			btInsertTag.setEnabled(true);
		}
		buttonGrid.addComponent(btInsertTag);
		buttonGridRowCount++;
		
		btRemoveTag = new Button("Remove Tag");
		btRemoveTag.setWidth("100%");
		buttonGrid.addComponent(btRemoveTag);
		buttonGridRowCount++;
		
		btEditTag = new Button("Edit Tag");
		btEditTag.setWidth("100%");
		buttonGrid.addComponent(btEditTag);
		buttonGridRowCount++;
		
		Label propertyLabel = new Label();
		propertyLabel.setIcon(
				new ClassResource(
						"ui/tagmanager/resources/ylwdiamd.gif", 
						application));
		propertyLabel.setCaption("Property");
		
		
		buttonGrid.addComponent(
				propertyLabel, 0, buttonGridRowCount, 0, buttonGridRowCount+4);
		buttonGridRowCount+=5;
		
		buttonGrid.setComponentAlignment(propertyLabel, Alignment.BOTTOM_LEFT);
		
		btInsertProperty = new Button("Create Property");
		btInsertProperty.setWidth("100%");
		buttonGrid.addComponent(btInsertProperty);
		buttonGridRowCount++;
		
		btRemoveProperty = new Button("Remove Property");
		// commented out on purpose: somehow this forces all the other buttons to 
		// show up in natural size...
//		btRemoveProperty.setWidth("100%");
		buttonGrid.addComponent(btRemoveProperty);
		buttonGridRowCount++;
		
		btEditProperty = new Button("Edit Property");
		btEditProperty.setWidth("100%");
		buttonGrid.addComponent(btEditProperty);
		buttonGridRowCount++;
		
		addComponent(buttonGrid);
		setExpandRatio(buttonGrid, 0);
		
		if (!withButtonPanel) {
			buttonGrid.setVisible(false);
		}

	}

	public void addTagsetDefinition(Collection<TagsetDefinition> tagsetDefinitions) {
		for (TagsetDefinition tagsetDefinition : tagsetDefinitions) {
			addTagsetDefinition(tagsetDefinition);
		}
	}
	
	public void addTagsetDefinition(TagsetDefinition tagsetDefinition) {
		
		ClassResource tagsetIcon = 
				new ClassResource(
					"ui/tagmanager/resources/grndiamd.gif", application);
		tagTree.addItem(tagsetDefinition);
		tagTree.getContainerProperty(
				tagsetDefinition, TagTreePropertyName.caption).setValue(
						tagsetDefinition.getName());
		tagTree.getContainerProperty(
				tagsetDefinition, TagTreePropertyName.icon).setValue(tagsetIcon);
		
		for (TagDefinition tagDefinition : tagsetDefinition) {
			addTagDefinition(tagDefinition);
		}
		
		for (TagDefinition tagDefinition : tagsetDefinition) {
			establishHierarchy(tagsetDefinition, tagDefinition);
		}
		
		for (TagDefinition tagDefinition : tagsetDefinition) {
			configureChildren(tagDefinition);
		}
	}
	

	
	private void configureChildren(TagDefinition tagDefinition) {
		if (!tagTree.hasChildren(tagDefinition)) {
			tagTree.setChildrenAllowed(tagDefinition, false);
		}
	}

	private void establishHierarchy(
			TagsetDefinition tagsetDefinition, TagDefinition tagDefinition) {
		String baseID = tagDefinition.getParentUuid();
		if (baseID.isEmpty()) {
			tagTree.setChildrenAllowed(tagsetDefinition, true);
			tagTree.setParent(tagDefinition, tagsetDefinition);
		}
		else {
			TagDefinition parent = tagsetDefinition.getTagDefinition(baseID);
			tagTree.setChildrenAllowed(parent, true);
			tagTree.setParent(tagDefinition, parent);
		}		
	}

	private void addTagDefinition(TagDefinition tagDefinition) {
		ClassResource tagIcon = 
			new ClassResource(
				"ui/tagmanager/resources/reddiamd.gif", application);

		tagTree.addItem(tagDefinition);
		tagTree.getContainerProperty(
				tagDefinition, 
				TagTreePropertyName.caption).setValue(
						tagDefinition.getName());
		tagTree.getContainerProperty(
				tagDefinition, 
				TagTreePropertyName.icon).setValue(tagIcon);
		
		for (PropertyDefinition propertyDefinition : 
				tagDefinition.getUserDefinedPropertyDefinitions()) {
			addUserDefinedPropertyDefinition(propertyDefinition, tagDefinition);
		}
	}

	private void addUserDefinedPropertyDefinition(
			PropertyDefinition propertyDefinition, TagDefinition tagDefinition) {
		ClassResource propertyIcon = 
				new ClassResource(
					"ui/tagmanager/resources/ylwdiamd.gif", 
					application);
		
		tagTree.addItem(propertyDefinition);
		tagTree.setChildrenAllowed(tagDefinition, true);
		tagTree.setParent(propertyDefinition, tagDefinition);
		tagTree.getContainerProperty(
				propertyDefinition, 
				TagTreePropertyName.caption).setValue(
						propertyDefinition.getName());
		tagTree.getContainerProperty(
				propertyDefinition, 
				TagTreePropertyName.icon).setValue(
						propertyIcon);
		tagTree.setChildrenAllowed(propertyDefinition, false);
	
	}

	public TreeTable getTagTree() {
		return tagTree;
	}
	
	public void close() {
		if (withTagsetButtons) {
			tagManager.removePropertyChangeListener(
					TagManagerEvent.tagsetDefinitionChanged,
					tagsetDefinitionChangedListener);
			tagManager.removeTagLibrary(tagLibrary);
		}
		tagManager.removePropertyChangeListener(
				TagManagerEvent.tagDefinitionChanged,
				tagDefinitionChangedListener);
		tagManager.removePropertyChangeListener(
				TagManagerEvent.userPropertyDefinitionChanged,
				userPropertyDefinitionChangedListener);
	}
	
	public TagManager getTagManager() {
		return tagManager;
	}
	
	public TagDefinition getTagDefinition(String tagDefinitionID) {
		for (Object item : tagTree.getItemIds()) {
			if ((item instanceof TagDefinition) 
					&& ((TagDefinition)item).getUuid().equals(tagDefinitionID)) {
				return (TagDefinition)item;
			}
		}
		return null;
	}
	
	public TagsetDefinition getTagsetDefinition(String tagDefinitionID) {
		return getTagsetDefinition(getTagDefinition(tagDefinitionID));
	}

	public void removeTagsetDefinition(TagsetDefinition tagsetDefinition) {
		removeTagsetDefinitionFromTree(tagsetDefinition);
	}

	private void removeTagsetDefinitionFromTree(TagsetDefinition tagsetDef) {
		for (TagDefinition td : tagsetDef) {
			removeTagDefinitionFromTree(td, tagsetDef);
		}
		tagTree.removeItem(tagsetDef);
	}
	
	public void addValueChangeListener(ValueChangeListener valueChangeListener) {
		tagTree.addListener(valueChangeListener);
	}
	
	public void removeValueChangeListener(ValueChangeListener valueChangeListener) {
		tagTree.removeListener(valueChangeListener);
	}
	
	public List<TagsetDefinition> getTagsetDefinitions() {
		ArrayList<TagsetDefinition> result = new ArrayList<TagsetDefinition>();
		
		for (Object itemId : tagTree.getItemIds()) {
			if (itemId instanceof TagsetDefinition) {
				result.add((TagsetDefinition)itemId);
			}
			
		}
		
		return result;
	}
	
	public void addActionHandler(Action.Handler actionHandler) {
		tagTree.addActionHandler(actionHandler);
	}
	
	public void addBtReloadListener(ClickListener listener) {
		btReload.addListener(listener);
	}
}