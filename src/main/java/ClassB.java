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
}
