package rt;

/**
 * An intersectable supports ray-surface intersection.
 */
public interface Intersectable {

	/**
	 * Implement ray-surface intersection in this method. Implementations of this 
	 * method need to make and return a {@link HitRecord} correctly, following
	 * the conventions of assumed for {@link HitRecord}.
	 * 
	 * @param r the ray used for intersection testing
	 * @return a hit record, should return null if there is no intersection
	 */
	public HitRecord intersect(Ray r);
}
