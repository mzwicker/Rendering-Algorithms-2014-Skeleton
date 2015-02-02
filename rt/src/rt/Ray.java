package rt;

import javax.vecmath.*;

/**
 * A ray represented by an origin and a direction.
 */
public class Ray {

	public Point3f origin;
	public Vector3f direction;
	
	public Ray(Tuple3f origin, Tuple3f direction)
	{
		this.origin = new Point3f(origin); 
		this.direction = new Vector3f(direction);
	}
}
