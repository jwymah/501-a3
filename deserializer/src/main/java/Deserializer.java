import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Scanner;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'bighoon' at '06/11/15 10:16 PM' with Gradle 2.6
 *
 * @author bighoon, @date 06/11/15 10:16 PM
 */
public class Deserializer {	
    public Deserializer()
    {
    	BufferedReader br = null;
    	try
		{
    		File file = new File("classB.txt");
    		
    		SAXBuilder saxBuilder = new SAXBuilder();
    		Document doc = saxBuilder.build(file);

    		Element rootNode = doc.getRootElement();
    		
    		for (Element element : rootNode.getChildren())
    		{    			
    			// create objects for every child of root element
    			if (element.getAttribute("length") == null) //TODO: add array parsing
    			{
	    			Class classObj = Class.forName(element.getAttributeValue("class"));
	    			classObj.getDeclaredConstructor(null).setAccessible(true);
	    			Object newInstance = null;
	    			try
					{
						newInstance = classObj.newInstance();
						// all of the element's children are fields
						for (Element child : element.getChildren())
						{
							String fieldName = child.getAttributeValue("name");
							String value = child.getChildren().get(0).getText();
							
							System.out.println("setting field on id {"+element.getAttributeValue("id")+"}" + classObj.toString() + ":" + fieldName + " --> " + value);
							try
							{
								Field field = classObj.getDeclaredField(fieldName);
								field.setAccessible(true);
								
								field.set(newInstance, convert(field.getType(), value));
							}
							catch (NullPointerException e)
							{
//								e.printStackTrace();
							}
							catch (NoSuchFieldException | IllegalAccessException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					catch (InstantiationException | IllegalAccessException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JDOMException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // from the second answer on 
    // http://stackoverflow.com/questions/13943550/how-to-convert-from-string-to-a-primitive-type-or-standard-java-wrapper-types
    private Object convert(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }
    
    public static void main(String[] args)
    {
    	Deserializer deserializer = new Deserializer();
    }
}
