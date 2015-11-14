public class ArraysOfPrimitive {
	public int[] arrayInt;
	
	public ArraysOfPrimitive()
	{
		arrayInt = new int[3];
	}
	
	public int[] getArrayInt() {
		return arrayInt;
	}

	public void setArrayInt(int[] arrayInt) {
		this.arrayInt = arrayInt;
	}
	
	public void setArrayValue(int value, int index) {
		this.arrayInt[index] = value;
	}
}