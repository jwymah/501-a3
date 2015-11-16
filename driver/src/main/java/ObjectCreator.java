import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import sun.security.provider.certpath.CollectionCertStore;

public class ObjectCreator
{
	private List<Object> objList = new ArrayList<Object>();
	private Scanner in = new Scanner(System.in);
	public ObjectCreator()
	{
		
	}
	
	public List<Object> getObjects()
	{
		return objList;
	}
	
	public void menu()
	{
		while (true)
		{
			System.out.println("\n\t++++++ Object Creator ++++++");
			System.out.println("1: \t Simple primitive Object");
			System.out.println("2: \t Ojbect with references");
			System.out.println("3: \t Object with array of primitives");
			System.out.println("4: \t Object with array of objects");
			System.out.println("5: \t Object with a java Collection");
			System.out.println("0: \t Main Menu");
			int input = Util.getMenuIntFromUser(5);
			
			switch (input)
			{
				case 1:
					SimpleInts simpleints = makeSimpleInts();
					objList.add(simpleints);
					break;
				case 2:
					SimpleObjects simpleobjects = makeSimpleObjectRefs();
					objList.add(simpleobjects);
					break;
				case 3:
					System.out.println("making object with array of primitives");
					ArraysOfPrimitive aop = new ArraysOfPrimitive();
					for (Field field : ArraysOfPrimitive.class.getDeclaredFields())
					{
						field.setAccessible(true);
						// assume array of int[] as only fields and not null and size > 0
						int index;
						try
						{
							System.out.println("Edit array entries");
							while (true)
							{
								System.out.print("enter index to modify from 0 to " + (Array.getLength(field.get(aop))-1));
								System.out.println(" or a negative int to exit");
								index = Util.getIntFromUserWithMax(Array.getLength(field.get(aop)));
								if (index < 0)
								{
									break;
								}
								System.out.println("enter new value");
								int value = Util.getIntFromUser();
								Array.setInt(field.get(aop), index, value);
							}
							objList.add(field.get(aop));
						}
						catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | IllegalAccessException e)
						{
							e.printStackTrace();
						}
					}
					objList.add(aop);
					break;
				case 4:
					System.out.println("making object with array of references");
					ArrayOfObjectRefs aof = new ArrayOfObjectRefs();
					for (Field field : ArrayOfObjectRefs.class.getDeclaredFields())
					{
						field.setAccessible(true);
						// assume array of Object[] as only fields and not null and size > 0
						int index;System.out.println("Edit array entries");
						while (true)
						{
							try
							{
								System.out.print("enter index to modify from 0 to " + (Array.getLength(field.get(aof))-1));
								System.out.println(" or a negative int to exit");
								index = Util.getIntFromUserWithMax(Array.getLength(field.get(aof)));
								if (index < 0)
								{
									break;
								}
								Class<?> type = field.get(aof).getClass().getComponentType();
								System.out.println(field.get(aof).getClass().getComponentType());
								Object value = selectCompatibleObject(type);
								Array.set(field.get(aof), index, value);
							}
							catch (IllegalArgumentException | IllegalAccessException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try
						{
							objList.add(field.get(aof));
						}
						catch (IllegalArgumentException | IllegalAccessException e)
						{
							e.printStackTrace();
						}
					objList.add(aof);
					}
					break;
				case 5:
					System.out.println("Making a collections object");
					CollectionObjects co = new CollectionObjects();
					for (Field field : CollectionObjects.class.getDeclaredFields())
					{
						field.setAccessible(true);
						if (field.getClass().isInstance(Collection.class))
						{
							continue;
						}
						while (true)
						{
							System.out.println("1: Select objects to be part of the collection");
							System.out.println("0: Use default values");
							int selection = Util.getMenuIntFromUser(1);
							if (selection == 0)
							{
								break;
							}
							else
							{
								System.out.println("How many objects do you want to add to this collection?");
								selection = Util.getIntFromUser();
								if (selection < 1)
								{
									System.out.println("cannot make collection of that size. using default collection");
									break;
								}
								ArrayList<Object> al = new ArrayList<Object>();
								for (int i=0; i<selection; i++)
								{
									System.out.println("Choose an object to add to collection");
									al.add(selectCompatibleObject(Object.class));
								}
								try
								{
									field.set(co, al);
								}
								catch (IllegalArgumentException | IllegalAccessException e)
								{
									e.printStackTrace();
								}
							}
							break;
						}
					}
					objList.add(co);
					break;
				case 0:
					return;
			}
		}
	}

	private SimpleObjects makeSimpleObjectRefs()
	{
		System.out.println("making object with references");
		SimpleObjects simpleobjects = new SimpleObjects();
		for (Field field : SimpleObjects.class.getDeclaredFields())
		{
			field.setAccessible(true);
			Class type = field.getType();
			Object value = selectCompatibleObject(type);
			try
			{
				field.set(simpleobjects, value);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return simpleobjects;
	}

	private Object selectCompatibleObject(Class type)
	{
		// compatible objects to choose
		ArrayList<Object> compatibleList = new ArrayList<Object>();
		for (Object o : objList.toArray())
		{
			if (type.isInstance(o))
			{
				compatibleList.add(o);
			}
		}
		System.out.println("potential objects of type " + type + ":");
		int i=0; 
		while (i<compatibleList.size())
		{
			System.out.println("\t" + i + ":\t" + compatibleList.get(i));
			i++;
		}
		System.out.println("\t" + compatibleList.size() + ":\tleave as null");
		
		Object value = null;
		try
		{
			int index = Util.getMenuIntFromUser(compatibleList.size() + 1);
			if (index < compatibleList.size())
			{
				value = compatibleList.get(index);
			}
		}
		catch (NumberFormatException | IndexOutOfBoundsException e)
		{
			System.out.println("error, defaulting value to null");
		}
		return value;
	}

	private SimpleInts makeSimpleInts()
	{
		System.out.println("making a simple object with primitives");
		SimpleInts simpleints = new SimpleInts();
		for (Field field : SimpleInts.class.getDeclaredFields())
		{
			System.out.println("enter " + field.getType() + " value for field: " + field.getName());
			try
			{
				field.setAccessible(true);
				field.set(simpleints, Util.getIntFromUser());
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				System.out.println("invalid input. skipping..");
			}
		}
		return simpleints;
	}
	
	public void viewObjects()
	{
		System.out.println("\nObjects Created:");
		for (Object obj : objList)
		{
			System.out.println(obj);
			
			if (obj.getClass().isArray())
			{
				for (int i=0; i<Array.getLength(obj); i++)
				{
					System.out.println("\t[" + i + "]" + " = " + Array.get(obj, i));
				}
			}
			
			for(Field f : obj.getClass().getDeclaredFields())
			{
				try
				{
					f.setAccessible(true);
					System.out.println("\tfield:\t" + f.getName() + "\t\tvalue:\t" + f.get(obj));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
				}
			}
		}
	}
}
