//#condition polish.usePolishGui

/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2007 - 2008 Michael Koch / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA02111-1307USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package cn.hunthawk.j2me.html.html;

import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class HtmlForm
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	private final String formName;
	private final String actionUrl;
	private final String method;
	
	private final ArrayList formItems = new ArrayList();
	private Hashtable hiddenElememts;
	
	
	public HtmlForm(String name, String actionUrl, String method)
	{
		this.formName = name;
		this.actionUrl = actionUrl;
		this.method = method.toUpperCase();
	}
	
	public String getAction()
	{
		return this.actionUrl;
	}
	
	public String getMethod()
	{
		return this.method;
	}
	
	public boolean isGet() {
		return GET.equals(this.method);
	}
	
	public boolean isPost() {
		return POST.equals(this.method);
	}

	
	/**
	 * Retrieves the (optional) name
	 * @return the name of this form, if specified
	 */
	public String getName() {
		return this.formName;
	}
	
	public void addItem(Item item)
	{
		this.formItems.add(item);
	}
	
	public Item[] getItems()
	{
		return (Item[]) this.formItems.toArray(new Item[this.formItems.size()]);
	}
	
	/**
	 * Adds a hidden element to this form. The hidden element will not be shown.
	 * @param name the name of the hidden element
	 * @param value the value of the hidden element
	 */
	public void addHiddenElement(String name, String value)
	{
		if (this.hiddenElememts == null) {
			this.hiddenElememts = new Hashtable();
		}
		if (value == null) {
			value = "";
		}
		this.hiddenElememts.put( name, value );
	}
	
	/**
	 * Retrieves all form input elements for submitting this form as string-pairs (name:value) in a Hashtable.
	 * @return a hashtable with all input elements
	 */
	public Hashtable getFormElements() {
		return getFormElements(null, null);
	}
	
	/**
	 * Retrieves all form input elements for submitting this form as string-pairs (name:value) in a Hashtable.
	 * @param listener the form listener that may change the values, can be null
	 * @param submitItem the submitItem that triggered the submission of the form, can be null
	 * @return a hashtable with all input elements
	 */
	public Hashtable getFormElements(FormListener listener, Item submitItem) {
		int size = this.hiddenElememts != null ? this.hiddenElememts.size() + this.formItems.size() : this.formItems.size();
		Hashtable elements = new Hashtable(size);
		if (this.hiddenElememts != null) {
			Enumeration enumeration = this.hiddenElememts.keys();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				String value = (String) this.hiddenElememts.get(name);
				if (listener != null) {
					value = listener.verifySubmitFormValue(this.actionUrl, name, value);
				}
				if (value == null) {
					value = "";
				}
				elements.put( name, value );
			}
		}
		Object[] items = this.formItems.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			if ("submit".equals(item.getAttribute(HtmlTagHandler.ATTR_TYPE))
					&& item != submitItem)
			{
				continue;
			}

			String name = (String) item.getAttribute(HtmlTagHandler.ATTR_NAME);
			if (name == null) {
				continue;
			}
			String value = (String) item.getAttribute(HtmlTagHandler.ATTR_VALUE);

			if (item instanceof TextField)
			{
				TextField textField = (TextField) item;
				value = textField.getString();
//				System.out.println("textField value:"+value);
			}
			else if (item instanceof ChoiceGroup) 
			{
				ChoiceGroup choiceGroup = (ChoiceGroup) item;
				HtmlSelect htmlSelect = (HtmlSelect) choiceGroup.getAttribute(HtmlSelect.SELECT);
				value = htmlSelect.getValue(choiceGroup.getSelectedIndex());
			}
			if (listener != null) {
				value = listener.verifySubmitFormValue(this.actionUrl, name, value);
			}
			if (value == null) {
				value = "";
			}
			elements.put(name, value);
			
		}
		return elements;
	}
	
	

}
