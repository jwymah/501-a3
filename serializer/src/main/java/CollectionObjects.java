import java.util.ArrayList;
import java.util.List;

public class CollectionObjects {
	private List<Object> listObjs = new ArrayList<Object>();
	
	public CollectionObjects(){
		listObjs = new ArrayList<Object>();
		listObjs.add(new SimpleInts());
		listObjs.add(new ArraysOfPrimitive());
		listObjs.add(new ArrayOfObjectRefs());
	}
	
	public List<?> getListObjs() {
		return listObjs;
	}

	public void setListObjs(List<Object> listObjs) {
		this.listObjs = listObjs;
	}
	
}