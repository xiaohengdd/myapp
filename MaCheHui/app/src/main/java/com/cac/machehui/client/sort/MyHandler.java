package com.cac.machehui.client.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.cac.machehui.client.entity.District;

public class MyHandler extends DefaultHandler {
	
	String tagName="";

    List<District> disList=new ArrayList<District>();
 
    private boolean flag=false;

	private HashMap<String, String> map=new HashMap<String, String>();

	@Override
	public void endDocument() throws SAXException {
		
	super.endDocument();

	}

	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		
		super.endElement(uri, localName, qName);
		
	
	}

	
	@Override
	public void startDocument() throws SAXException {
		
	    super.startDocument();
	}

	public HashMap<String, String> getCity()
	{
		//System.out.println("map="+map);
		return map;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
	Attributes attributes) throws SAXException {
		tagName=localName;
	  
		if(localName.equals("string-array")){
			
	     
		}
		if(localName.equals("item")){
			
			map.put(attributes.getValue("id"), attributes.getValue("name"));

		}
	     
     

	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException {
	/*if(tagName.equals("string-array")){
	    //System.out.println("string-array="+new String(ch,start,length));
        
	}else if(tagName.equals("item")){

		//System.out.println("item="+new String(ch,start,length));
		
//		list = new ArrayList<District>();
//		
//	    district.setName(new String(ch,start,length));
//	    System.out.println("district"+"name"+district.getName()); 
	   
	   
	}
*/
	}

}