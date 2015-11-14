import java.util.Scanner;

public class Util
{
	private static Scanner in = new Scanner(System.in);
	
	public static int getMenuIntFromUser(int max)
	{
		int input = -1;
		while (input < 0 || input >= max)
		{
			input = getIntFromUser();
			if (input >= max)
			{
				System.err.println("that was not a valid selection.. try again");
			}
		}
		return input;
	}

	public static int getIntFromUserWithMax(int max)
	{
		int input = max;
		while (input >= max)
		{
			input = getIntFromUser();
			if (input >= max)
			{
				System.err.println("that was not a valid selection.. try again");
			}
		}
		return input;
	}
	
	public static int getIntFromUser()
	{
		try
		{
			return Integer.parseInt(in.nextLine());
		}
		catch(Exception e)
		{
			System.err.println("that was not a valid selection.. try again");
		}
		return -1;
	}
}
