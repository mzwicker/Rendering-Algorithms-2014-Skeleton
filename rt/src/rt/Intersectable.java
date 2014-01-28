package rt;

public interface Intersectable {

	public HitRecord intersect(Ray r);
	public float surfaceArea();
	public AxisAlignedBox boundingBox();
}
