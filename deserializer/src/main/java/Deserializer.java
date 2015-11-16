import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'bighoon' at '06/11/15 10:16 PM' with Gradle 2.6
 *
 * @author bighoon, @date 06/11/15 10:16 PM
 */
public class Deserializer {
	Map<String, Object> deserializedMap = new HashMap<>();
	Element rootNode;
	
    public Deserializer()
    {
    }

	public Object deserialize(Document doc)
	{
		deserializedMap.clear();
		try
		{
    		File file = new File("sample.txt");
    		
    		SAXBuilder saxBuilder = new SAXBuilder();
//    		Document doc = saxBuilder.build(file);

    		rootNode = doc.getRootElement();
    		
    		for (Element element : rootNode.getChildren())
    		{
    			// create objects for every child of root element
    			if (deserializedMap.get(element.getAttribute("class")) != null)
    			{
    				continue; // because it may have already been instantiated due to being pointed to by another object
    			}
    			
    			deserializeElement(element);
    		}
    		
    		// visualize what was done...
    		System.out.println("printing out map contents. size: " + deserializedMap.size() + "\n");
    		
    		for (Entry<String, Object> entry : deserializedMap.entrySet())
    		{
    			if (entry.getValue() == null) { continue; }
    			System.out.println("{" + entry.getKey() + " : " + entry.getValue() + "}");
    			if (entry.getValue().getClass().isArray())
    			{
    				for (int i=0; i<Array.getLength(entry.getValue()); i++)
    				{
    					System.out.println("\t[" + i + "] : " + Array.get(entry.getValue(), i));
    				}
    			}
    			else
    			{
	    			for (Field f : entry.getValue().getClass().getDeclaredFields())
	    			{
	    				f.setAccessible(true);
	    				System.out.print("\tfield:\t" + f.getName() + "\tvalue:\t");
	    				try
	    				{
							System.out.println(f.get(entry.getValue()));
	    				}
	    				catch(Exception e)
	    				{
	    					System.out.println();
	    				}
	    			}
    			}
    		}    		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		String idOfRoot;
		int lowId=1000;
		for(Entry entry : deserializedMap.entrySet())
		{
			if (Integer.parseInt((String) entry.getKey()) < lowId)
			{
				lowId = Integer.parseInt((String) entry.getKey());
			}
		}
		return deserializedMap.get(Integer.toString(lowId));
	}

	private void deserializeElement(Element element) throws ClassNotFoundException, NoSuchMethodException
	{
		if (element.getAttribute("length") != null)
		{
			System.out.println("DESERIALIZING ARRAY: " + element.getAttributeValue("class")
								+ " ID: " + element.getAttributeValue("id"));
			
			Object newArray = makeArray(element.getAttributeValue("class"), Integer.parseInt(element.getAttributeValue("length")));
			Class<?> componentType = newArray.getClass().getComponentType();
			
			if (newArray.getClass().getComponentType().isPrimitive())
			{
				for (int i=0; i<element.getChildren().size(); i++)
				{
					Element child = element.getChildren().get(i);
					if(element.getAttributeValue("class").compareTo("[C") == 0)
					{
						Array.set(newArray, i, child.getText().charAt(0));
					}
					else
					{
						Array.set(newArray, i, convert(componentType, child.getText()));
					}
				}
				deserializedMap.put(element.getAttributeValue("id"), newArray);
			}
			else //not primitive
			{
				for (int i=0; i<element.getChildren().size(); i++)
				{
					Element child = element.getChildren().get(i);
					// if in map
					if (deserializedMap.get(child.getText()) != null)
					{
						Array.set(newArray, i, deserializedMap.get(child.getText()));
					}
					// else not in map
					else
					{
						try
						{
							Array.set(newArray, i, findAndSerializeById(child.getText()));
						}
						catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | IllegalAccessException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		else
		{
			Class<?> classObj = Class.forName(element.getAttributeValue("class"));
			classObj.getDeclaredConstructor(null).setAccessible(true);
			Object classInstance = null;
			try
			{
				classInstance = classObj.newInstance();
				deserializedMap.put(element.getAttributeValue("id"), classInstance);
				System.err.println("adding to map: {" + element.getAttributeValue("id") + "," + classInstance + "}");
				
				System.err.println("Serializing: " + classInstance.getClass() + " ID: " + element.getAttributeValue("id"));
				
				setFields(element, classObj, classInstance);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void setFields(Element element, Class<?> classObj, Object classInstance)
	{
		// all of the element's children are fields
		for (Element child : element.getChildren())
		{
			String fieldName = child.getAttributeValue("name");
			String value = child.getChildren().get(0).getText();
			
			System.out.println("setting field on id {"+element.getAttributeValue("id")+"} " + classObj.toString() + ":" + fieldName + " --> " + value);
			try
			{
				Field field = classObj.getDeclaredField(fieldName);
				field.setAccessible(true);

				// field == null or reference or primitive
				if (value == null)
				{
					continue; //ie. do nothing
				}
				else if (child.getChildren().get(0).getName() == "reference")
				{
					// find the object's reference in map if already created
					// otherwise go and create it...
					System.err.println("is a reference with value: " + value);
					if (deserializedMap.get(value) != null)
					{
						field.setAccessible(true);
						field.set(classInstance, deserializedMap.get(value));
					}
					else
					{
						field.set(classInstance, findAndSerializeById(value));
					}
				}
				else // primitive that is not null
				{
					field.set(classInstance, convert(field.getType(), value));
				}
			}
			catch (NullPointerException e)
			{
				System.err.println("is this still being hit?");
				System.err.println(child.getChildren().get(0).getName());
				e.printStackTrace();
			}
			catch (NoSuchFieldException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		System.out.print("key set size: ");
		System.out.println(deserializedMap.size());
	}

	private Object findAndSerializeById(String id) throws IllegalAccessException
	{
		// go deserialize a specific object...
		// go recursive and put this call onto the stack
		for (Element searchElement : rootNode.getChildren())
		{
			if (searchElement.getAttributeValue("id").compareTo(id) == 0)
			{
				System.err.println("FOUND IT... now go deserialize it");
				System.out.print("key set size: ");
				System.out.println(deserializedMap.size());
				
				try
				{
					deserializeElement(searchElement);
				}
				catch (ClassNotFoundException | NoSuchMethodException e)
				{
					e.printStackTrace();
				}
				return deserializedMap.get(id);
			}
		}
		return null;
	}
    
    // from the second answer on 
    // http://stackoverflow.com/questions/13943550/how-to-convert-from-string-to-a-primitive-type-or-standard-java-wrapper-types
    private Object convert(Class<?> targetType, String text) {
    	if (targetType.isInstance(char.class))
    	{
    		return char.class.cast(text);
    	}
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }

	private Object makeArray(String className, int length) throws ClassNotFoundException{
		if("[I".equals(className)) return Array.newInstance(int.class, length);
		if("[D".equals(className)) return Array.newInstance(double.class, length);
		if("[F".equals(className)) return Array.newInstance(float.class, length);
		if("[B".equals(className)) return Array.newInstance(byte.class, length);
		if("[J".equals(className)) return Array.newInstance(long.class, length);
		if("[S".equals(className)) return Array.newInstance(short.class, length);
		if("[Z".equals(className)) return Array.newInstance(boolean.class, length);
		if("[C".equals(className)) return Array.newInstance(char.class, length);
		// remove Leading [L and trailing ;
		return Array.newInstance(Class.forName(className.substring(2,className.length()-1)), length);
	}
    
    public static void main(String[] args)
    {
//    	Deserializer deserializer = new Deserializer();
//    	Object result = deserializer.deserialize();
//    	ObjectInspector oi = new ObjectInspector();
//    	oi.inspect(result, true);    	
    }
}
