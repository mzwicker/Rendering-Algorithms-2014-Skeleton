package rt;

/**
 * Interface to be implemented by any class supporting ray-surface intersection.
 */
public interface Intersectable {

	/**
	 * Implement ray-surface intersection in this method.
	 * 
	 * @param r the ray used for intersection testing
	 * @return a hit record, should return null if there is no intersection
	 */
	public HitRecord intersect(Ray r);
}
