public class ArrayOfObjectRefs {

	public Object[] arrayObjectRefs;

	public ArrayOfObjectRefs()
	{
		arrayObjectRefs = new Object[8];
	}
	
	public Object[] getArrayObjectRefs() {
		return arrayObjectRefs;
	}

	public void setArrayObjectRefs(Object[] arrayObjectRefs) {
		this.arrayObjectRefs = arrayObjectRefs;
	}
	
	public void setArrayValue(Object value, int index) {
		this.arrayObjectRefs[index] = value;
	}
}