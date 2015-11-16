
/*==========================================================================
File: ObjectInspector.java
Purpose:Demo Object inspector for the Asst2TestDriver

Location: University of Calgary, Alberta, Canada
Created By: Jordan Kidney
Created on:  Oct 23, 2005
Last Updated: Oct 23, 2005

***********************************************************************
If you are going to reproduce this code in any way for your asignment 
rember to include my name at the top of the file toindicate where you
got the original code from
***********************************************************************


========================================================================*/

import java.util.*;
import java.lang.reflect.*;

public class ObjectInspector
{
	private boolean recursive;
	private Map<Object,Object> inspectedSet = new IdentityHashMap<Object,Object>();
	
	public ObjectInspector()
	{
	}

	// -----------------------------------------------------------
	public void inspect(Object obj, boolean recursive)
	{
		System.out.println("Inspecting " + obj);
		this.recursive = recursive;
		inspectedSet.put(obj, null);

		Queue<Class<?>> interfaceQueue = new LinkedList<Class<?>>();
		Set<Class<?>> interfaceTypes = new HashSet<Class<?>>();
		Vector<Field> objectsToInspect = new Vector<Field>();
		Vector<Object> arrayELementsToInspect = new Vector<Object>();
		Class<?> objClass = obj.getClass();
		
		if(objClass.isArray())
		{
			System.out.println(objClass.isArray());
			inspectArray(obj,objClass);
		}

		System.out.println("Declaring class:\t" + objClass.getDeclaringClass());
		
		inspectSuperClass(obj, objClass, interfaceQueue, interfaceTypes, objectsToInspect, arrayELementsToInspect);
		inspectConstructors(objClass);
		for (Class<?> intface : objClass.getInterfaces())
		{
			interfaceQueue.add(intface);
			interfaceTypes.add(intface);
		}
		inspectInterfaces(objClass, interfaceQueue, interfaceTypes, objectsToInspect, arrayELementsToInspect);
		inspectMethods(objClass);
		inspectFields(obj, objClass, objectsToInspect, arrayELementsToInspect);

		if (recursive)
		{
			inspectFieldClasses(obj, objClass, objectsToInspect, arrayELementsToInspect);
		}
	}

	private void inspectArray(Object obj, Class<?> objClass)
	{
		System.out.println("Object is an Array: ");
		System.out.println("\tname:\t" + objClass.getName());
		System.out.println("\tlength:\t" + Array.getLength(obj));
		System.out.println("\ttype:\t" + objClass.getComponentType());
		System.out.println("\tcontents:");
		for (int i=0; i<Array.getLength(obj); i++)
		{
			System.out.println("\t\t" + Array.get(obj, i));
		}
	}

	private void inspectInterfaces(Class<?> objClass, Queue<Class<?>> interfaceQueue, Set<Class<?>> interfaceTypes, Vector<Field> objectsToInspect, Vector<Object> arrayELementsToInspect)
	{
		System.out.print("Implemented Interfaces:\t");
		if (objClass.getInterfaces().length == 0)
		{
			System.out.print("None");
		}
		for (Class<?> intface : objClass.getInterfaces())
		{
			System.out.println(intface.getName() + ", ");
		}
		System.out.println();

		for (Class<?> intface : objClass.getInterfaces())
		{
			if (!interfaceTypes.contains(intface))
			{
				interfaceQueue.add(intface);
				interfaceTypes.add(intface);
			}
		}
		
		while (!interfaceQueue.isEmpty())
		{
			Class<?> curr = interfaceQueue.poll();
			System.out.println("Inspecting interface: " + curr);
			inspectMethods(curr);
			inspectFields(curr, curr.getClass(), objectsToInspect, arrayELementsToInspect, false); //TODO THIS CAUSESE ERROR
		}
	}

	private void inspectSuperClass(Object obj, Class<?> objClass, Queue<Class<?>> interfaceQueue, Set<Class<?>> interfaceTypes, Vector<Field> objectsToInspect, Vector<Object> arrayELementsToInspect)
	{		
		System.out.print("\nSuperclass:\t\t");
		if (objClass.getSuperclass() == null)
		{
			System.out.println("None");
		}
		else
		{
			System.out.println(objClass.getSuperclass().getName());
			inspectConstructors(objClass.getSuperclass());
			
			inspectInterfaces(objClass.getSuperclass(), interfaceQueue, interfaceTypes, objectsToInspect, arrayELementsToInspect);

			if ((objClass.getSuperclass() != null) && (objClass.getSuperclass().getDeclaredFields().length > 0))
			{
				inspectFields(obj, objClass.getSuperclass(), objectsToInspect, arrayELementsToInspect);
			}
			if (objClass.getSuperclass() != null)
			{
				inspectSuperClass(obj, objClass.getSuperclass(), interfaceQueue, interfaceTypes, objectsToInspect, arrayELementsToInspect);
			}
		}
	}

	private void inspectConstructors(Class<?> objClass)
	{
		System.out.println("Constructors:");
		for (Constructor<?> constructor : objClass.getConstructors())
		{
			System.out.println("\tName:\t" + constructor.getName());
			printParameterTypes(constructor);
			printModifiers(constructor);
		}
	}

	private void inspectMethods(Class<?> objClass)
	{
		return;
//		System.out.println("Methods:");
//		for (Method method : objClass.getDeclaredMethods())
//		{
//			try
//			{
//				System.out.println("\tName:\t" + method.getName());
//				printThrownExceptions(method);
//				printParameterTypes(method);
//				printModifiers(method);
//				System.out.println("\t  Return Type:\t\t" + method.getReturnType());
//				System.out.println();
//			}
//			catch (Exception e)
//			{
//			}
//		}
	}

	private void printModifiers(Constructor<?> constructor)
	{
		System.out.println("\t  Modifiers:\t\t" + Modifier.toString(constructor.getModifiers()));
	}
	private void printModifiers(Method method)
	{
		System.out.println("\t  Modifiers:\t\t" + Modifier.toString(method.getModifiers()));
	}

	private void printParameterTypes(Constructor<?> constructor)
	{
		System.out.print("\t  Parameter Types:\t");
		if (constructor.getParameterTypes().length == 0)
		{
			System.out.print("None");
		}
		for (Class<?> param : constructor.getParameterTypes())
		{
			System.out.print(param.getTypeName() + " ");
		}
		System.out.println();
	}
	private void printParameterTypes(Method method)
	{
		System.out.print("\t  Parameter Types:\t");
		if (method.getParameterTypes().length == 0)
		{
			System.out.print("None");
		}
		for (Class<?> param : method.getParameterTypes())
		{
			System.out.print(param.getTypeName() + " ");
		}
		System.out.println();
	}

	private void printThrownExceptions(Method method)
	{
		System.out.print("\t  Exceptions thrown:\t");
		if (method.getExceptionTypes().length == 0)
		{
			System.out.print("None");
		}
		for (Class<?> e : method.getExceptionTypes())
		{
			System.out.print(e.getName());
		}
		System.out.println();
	}

	// -----------------------------------------------------------
	private void inspectFieldClasses(Object obj, Class<?> objClass, Vector<Field> objectsToInspect, Vector<Object> arrayELementsToInspect)
	{
		if (objectsToInspect.size() > 0)
			System.out.println("---- Inspecting Field Classes ----");

		Enumeration<Field> e = objectsToInspect.elements();
		while (e.hasMoreElements())
		{
			Field f = (Field) e.nextElement();
			System.out.println("Inspecting Field: {"+ obj + "." + f.getName()+":"+f.getType()+"}");

			try
			{
				System.out.println("******************");
				if (f.get(obj) != null && !inspectedSet.containsKey(f.get(obj)))
				{
					inspect(f.get(obj), recursive);
				}
				else
				{
					System.out.println("\tField is null.");
				}
				System.out.println("******************");
			}
			catch (Exception exp)
			{
				exp.printStackTrace();
			}
		}
		
		Enumeration<Object> e2 = arrayELementsToInspect.elements();
		while( e2.hasMoreElements())
		{
			Object o = (Object) e2.nextElement();
			if (!inspectedSet.containsKey(o))
			{
				System.out.println("******************");
				System.out.println("Inspecting Array Object {" + o + "}");
				System.out.println("******************");
				inspect(o.getClass(), recursive);
			}
		}
	}

	// -----------------------------------------------------------
	private void inspectFields(Object obj, Class<?> objClass, Vector<Field> objectsToInspect, Vector<Object> arrayELementsToInspect)
	{
		inspectFields(obj, objClass, objectsToInspect, arrayELementsToInspect, true);
	}
	private void inspectFields(Object obj, Class<?> objClass, Vector<Field> objectsToInspect, Vector<Object> arrayELementsToInspect, boolean goFurther)
	{
		System.out.println("Fields in class " + objClass.getName() + ":");
		for (Field f : objClass.getDeclaredFields())
		{
			f.setAccessible(true);
			try
			{
				System.out.print("\tName:\t" + objClass.getName() + "." + f.getName());
				System.out.print("\tType:\t" + f.getType());
				System.out.print("\tValue:\t" + f.get(obj)); // TODO: this throws a NPE when trying iterate and toString()
				System.out.println("\tModifiers:\t" + Modifier.toString(f.getModifiers()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				if (f.get(obj).getClass().isArray())
				{
					for (int i=0; i<Array.getLength(f.get(obj)); i++)
					{
						if (Array.get(f.get(obj), i) != null)
						{
							if (!Array.get(f.get(obj), i).getClass().isPrimitive()) //this is pointless because of primitive wrappers...
							{
								arrayELementsToInspect.add(Array.get(f.get(obj), i));
							}
						}
						System.out.println("\t\t\t" + f.getName()+ "[" + i + "]" + " = " + Array.get(f.get(obj), i));
					}
				}
				else if (goFurther && !f.getType().isPrimitive())
				{
					objectsToInspect.addElement(f);
				}
			}
			catch (Exception e)
			{
			}
		}
	}
}
