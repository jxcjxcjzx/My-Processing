package org.speakright.demos.simpsons;

import java.util.HashMap;
import java.util.Map;


/*
 * can this go in the model?
 */
public class SimpsonsData {

	Map<Integer,Item> m_map = new HashMap<Integer,Item>();
	
	SimpsonsData()
	{
		add(1, "Homer", "Homer Simpson suffers from a short attention span which complements his intense but short-lived passion for hobbies and projects.", "wife", 2);
		add(2, "Marge", "Marge is Homer's well-meaning and patient wife with blue hair.", "mother", 3);
		add(3, "Bart", "Bart is a self-proclaimed underacheiver, whose pranks terrify the people of SpringField", "sister", 4);
		add(4, "Lisa", "Lisa is the brains of the Simpsons family and its moral conscience", "sister", 5);
		add(5, "Maggie", "Maggie is the youngest member of the Simpsons family, and says little.", "mother", 2);
		add(6, "Mister Burns", "Montgomery Burns owns the Springfield Nuclear Power Plant where Homer works.", "worker", 1);
		add(7, "Smithers", "Waylon Smithers is Mister Burns loyal assistant, whose love for his boss remains firmly in the closet.", "boss", 6);
	}
	
	public void add(int id, String name, String description, String relation, int relatedId)
	{
		Item item = new Item();
		item.m_name = name;
		item.m_description = description;
		item.m_relatedId = relatedId;
		item.m_relation = relation;
		m_map.put(id, item);
	}
	
	public String getName(int id)
	{
		String ret = "";
		Item item = getItem(id);
		if (item != null) {
			ret = item.m_name;
		}
		return ret;
	}
	
	public String getDescription(int id)
	{
		String ret = "";
		Item item = getItem(id);
		if (item != null) {
			ret = item.m_description;
		}
		return ret;
	}
	
	public int getRelatedId(int id)
	{
		int ret = 0;
		Item item = getItem(id);
		if (item != null) {
			ret = item.m_relatedId;
		}
		return ret;
	}
	
	public String getRelation(int id)
	{
		String ret = "";
		Item item = getItem(id);
		if (item != null) {
			ret = item.m_relation;
		}
		return ret;
	}
	
	
	Item getItem(int id)
	{
		Item item = m_map.get(id);
		return item;
	}
	
	public class Item
	{
		String m_name;
		int m_relatedId;
		String m_relation;
		String m_description;
	}
}
