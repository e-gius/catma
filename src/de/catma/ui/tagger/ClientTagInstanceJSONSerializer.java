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
package de.catma.ui.tagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.catma.ui.client.ui.tagger.shared.ClientTagInstance;
import de.catma.ui.client.ui.tagger.shared.ClientTagInstance.SerializationField;
import de.catma.ui.client.ui.tagger.shared.TextRange;
import de.catma.ui.data.util.JSONSerializationException;

public class ClientTagInstanceJSONSerializer {
	
	public List<String> fromInstanceIDJSONArray(String jsonArray) throws JSONSerializationException {
		try {
			List<String> result = new ArrayList<String>();
			JSONArray instanceIDArray = new JSONArray(jsonArray);
			
			for (int i=0; i<instanceIDArray.length(); i++) {
				JSONObject instanceIDJSON = (JSONObject)instanceIDArray.get(i);
				result.add(
					instanceIDJSON.getString(
							SerializationField.instanceID.name()));
			}
			
			return result;
		}
		catch(JSONException e) {
			throw new JSONSerializationException(e);
		}
	}
	
	public ClientTagInstance fromJSON(String json) throws JSONSerializationException {
		try {
			JSONObject tagInstanceJSON = new JSONObject(json);
			String tagDefinitionID =
					tagInstanceJSON.getString(SerializationField.tagDefinitionID.name());
			String instanceID = 
					tagInstanceJSON.getString(SerializationField.instanceID.name());
			String color =
					tagInstanceJSON.getString(SerializationField.color.name());
			List<TextRange> ranges = new ArrayList<TextRange>();
			JSONArray rangesJSON = 
					tagInstanceJSON.getJSONArray(SerializationField.ranges.name());
			
			for (int i=0; i<rangesJSON.length(); i++) {
				JSONObject trJSON = (JSONObject)rangesJSON.get(i);
				
				TextRange tr = 
						new TextRange(
								trJSON.getInt(SerializationField.startPos.name()), 
								trJSON.getInt(SerializationField.endPos.name()));
				ranges.add(tr);
			}		
			
			return new ClientTagInstance(tagDefinitionID, instanceID, color, ranges);
		}
		catch(JSONException e) {
			throw new JSONSerializationException(e);
		}
	}
	
	private JSONObject toJSONObject(ClientTagInstance tagInstance) throws JSONSerializationException {
		try {
			JSONObject tagInstanceJSON = new JSONObject();
			tagInstanceJSON.put(
				SerializationField.tagDefinitionID.name(),
				tagInstance.getTagDefinitionID());
			tagInstanceJSON.put(
				SerializationField.instanceID.name(), 
				tagInstance.getInstanceID());
			
			tagInstanceJSON.put(
					SerializationField.color.name(), 
					tagInstance.getColor());
	
			JSONArray rangesJSON = new JSONArray();
			tagInstanceJSON.put(
					SerializationField.ranges.name(), 
					rangesJSON);
			
	
			for (TextRange tr : tagInstance.getRanges()) {
				JSONObject trJSON = new JSONObject();
				trJSON.put(SerializationField.startPos.name(), tr.getStartPos());
				trJSON.put(SerializationField.endPos.name(), tr.getEndPos());
				rangesJSON.put(trJSON);
			}
			
			return tagInstanceJSON;
		}
		catch (JSONException e) {
			throw new JSONSerializationException(e);
		}
	}
	
	public String toJSON(ClientTagInstance tagInstance) throws JSONSerializationException {
		return toJSONObject(tagInstance).toString();
	}
	
	public String toJSON(Collection<ClientTagInstance> tagInstances) 
			throws JSONSerializationException {
		JSONArray tagInstancesJSON = new JSONArray();
		for (ClientTagInstance ti : tagInstances) {
			tagInstancesJSON.put(toJSONObject(ti));
		}
	
		return tagInstancesJSON.toString();
	}

	public String join(
			String jsonArray, Collection<ClientTagInstance> tagInstances) 
					throws JSONSerializationException {
		try {
			if (jsonArray == null) {
				jsonArray = "[]";
			}
			JSONArray tagInstancesJSON = new JSONArray(jsonArray);
			for (ClientTagInstance ti : tagInstances) {
				tagInstancesJSON.put(toJSONObject(ti));
			}
			return tagInstancesJSON.toString();
		}
		catch (JSONException e) {
			throw new JSONSerializationException(e);
		}
	}
}
