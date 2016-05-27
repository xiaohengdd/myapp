package com.cac.machehui.client.sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class GetData {

	HashMap<String, String> map=null;
	List<HashMap<String, String>> list=null;
	public HashMap<String, String> getCity(InputStream inputStream)
	{
		//InputStream is = null;
		
		SAXParserFactory factory=SAXParserFactory.newInstance();
		try {
		SAXParser sp=factory.newSAXParser();
		MyHandler handler=new MyHandler();
		sp.parse(inputStream, handler);
		//System.out.println(sp.parse(inputStream, new MyHandler()));
	   // MyHandler hand=new MyHandler();
	    map=handler.getCity();
		} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return map;
	}
	
}
