public class ClassB implements Runnable
{
    public ClassB() throws Exception
    {
    }

    public void run() { }

    public String toString() { return "ClassB"; }

    public void func3(int a)
    {

    }

    private ClassA val = new ClassA();
    private ClassA val2 = new ClassA(12);
    private ClassA val3;
    public int int1;;
    private String string1 = "Hello!";
    private int[] intArray= {1,2,3};
    private String[] stringArray = {"hi", "no"};
    
    public int[] getIntArray()
    {
    	return intArray;
    }
    public ClassA getVal()
    {
    	return val;
    }
}
