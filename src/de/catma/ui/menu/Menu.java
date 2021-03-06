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
package de.catma.ui.menu;

import java.util.HashMap;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.MenuBar;

public class Menu {

	private MenuBar menuBar;
	
	private HashMap<ComponentContainer, MenuBar.Command> entries;
	
	public Menu(MenuBar menuBar) {
		this.menuBar = menuBar;
		this.entries = new HashMap<ComponentContainer, MenuBar.Command>();
	}
	
	public void addEntry(ComponentContainer compContainer, MenuBar.Command command) {
		this.entries.put(compContainer, command);
	}
	
	public void executeEntry(ComponentContainer compContainer) {
		this.entries.get(compContainer).menuSelected(null);
	}
	
	public MenuBar getMenuBar() {
		return menuBar;
	}
	
}
