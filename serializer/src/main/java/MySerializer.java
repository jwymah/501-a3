/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'bighoon' at '04/11/15 12:16 PM' with Gradle 2.6
 *
 * @author bighoon, @date 04/11/15 12:16 PM
 */

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jdom2.DataConversionException;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class MySerializer {
	private Integer id = 0;
	private Map<Object, Integer> serializedMap = new IdentityHashMap<Object, Integer>();
	private Element rootElement = new Element("serialized");
	
	public MySerializer()
	{
		try
		{
			Document doc = new Document(rootElement);
			doc.setDocType(new DocType("rooty"));
			
			CollectionObjects co = new CollectionObjects();
			rootElement.addContent(serialize(co.getListObjs()));
			
//			rootElement.addContent(serialize(new ClassB()));
		
			System.out.println(doc.toString());
		
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			
			OutputStream outStream = System.out;
			xmlOutput.output(doc, outStream);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public Element serialize(Object obj) throws IllegalArgumentException, IllegalAccessException, IOException, DataConversionException {
    	Field[] fields = obj.getClass().getDeclaredFields();
    	
    	Element thisNode = new Element("object")
    			.setAttribute("class", obj.getClass().getName())
    			.setAttribute("id", String.valueOf(id));
    	
    	serializedMap.put(obj, id);
    	id++;
    	
    	if (obj.getClass().isArray())
    	{
    		thisNode.setAttribute("class", obj.getClass().toString().substring(6));
    		thisNode.setAttribute("length", String.valueOf(Array.getLength(obj)));
			
    		if (obj.getClass().getComponentType().isPrimitive())
//    				|| obj.getClass().getComponentType() == String.class)
    		{
    			for (int i=0; i<Array.getLength(obj); i++)
        		{
    				Element arrayElement = new Element("value")
    						.setText(Array.get(obj, i).toString());
    				thisNode.addContent(arrayElement);
        		}
    		}
    		else
    		{
	    		for (int i=0; i<Array.getLength(obj); i++)
	    		{
	    			Element arrayItem = new Element("reference");
	    			
	    			if (Array.get(obj, i) == null)
	    			{
	    				continue;
	    			}
	    			else if (Array.get(obj, i) != null && serializedMap.get(Array.get(obj, i).getClass()) == null)
	    			{
	        			Element objectXml = serialize(Array.get(obj, i));
	    				arrayItem.setText(objectXml.getAttributeValue("id"));
	        			rootElement.addContent(objectXml);
	    			}
	    			else 
	    			{
	    				arrayItem.setText(serializedMap.get(Array.get(obj, i)).toString());
	    			}
	    			thisNode.addContent(arrayItem);
	    		}
    		}
			return thisNode;
    	}
    	
    	// for each field add an element
    	for( Field field : fields)
    	{
    		field.setAccessible(true);
    		Element newFieldElement = new Element("field")
    								.setAttribute("name", field.getName())
    								.setAttribute("declaringclass", field.getDeclaringClass().getName());
    		
    		if (field.getType().isPrimitive())
//    				|| field.getType() == String.class)
    		{
    			try
    			{
				newFieldElement.addContent(new Element("value")
											.setText(field.get(obj).toString()));
    			}
    			catch (IllegalDataException e)
    			{
    				newFieldElement.addContent(new Element("value"));
    			}
    		}
    		else // field is a reference
    		{
				Element refElement = new Element("reference");

				if (field.get(obj) != null && serializedMap.get(field.get(obj).getClass()) == null)
				{
					Element child = serialize(field.get(obj));
					Integer newId = child.getAttribute("id").getIntValue();
					rootElement.addContent(child);
					
					refElement.setText(String.valueOf(newId));
				}
				newFieldElement.addContent(refElement);
    		}
    		thisNode.addContent(newFieldElement);
    	}
    	
    	// for each element in array get its value or reference
    	
    	
    	return thisNode;
    }
    
    public static void main(String[] args)
    {
    	new MySerializer();
    }
}
