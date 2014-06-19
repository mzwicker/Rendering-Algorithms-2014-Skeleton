package rt.intersectables;

import java.util.LinkedList;
import java.util.Iterator;
import rt.*;

public class IntersectableList extends Aggregate {

	public LinkedList<Intersectable> list;
	
	public IntersectableList()
	{
		list = new LinkedList<Intersectable>();
	}
	
	public void add(Intersectable i)
	{
		list.add(i);
	}
	
	public Iterator<Intersectable> iterator() {
		return list.iterator();
	}

}
