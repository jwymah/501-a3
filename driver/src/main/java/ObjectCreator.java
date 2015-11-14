import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
			System.out.println("6: \t Main Menu");
			int input = Integer.parseInt(in.nextLine());
			
			switch (input)
			{
				case 1:
					SimpleInts simpleints = makeSimpleInts();
					objList.add(simpleints);
					break;
				case 2:
					SimpleObjects simpleobjects;
					simpleobjects = new SimpleObjects();
					System.out.println("making object with references");
					for (Field field : SimpleObjects.class.getDeclaredFields())
					{
						field.setAccessible(true);
						Object value = objList.get(0); //TODO dynamic selection
						try
						{
							field.set(simpleobjects, value);
						}
						catch (IllegalArgumentException | IllegalAccessException e)
						{
							e.printStackTrace();
						}
					}
					objList.add(simpleobjects);
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					return;
			}
		}
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
				field.set(simpleints, Integer.parseInt(in.nextLine()));
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
			System.out.println(obj.getClass().getName());
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
