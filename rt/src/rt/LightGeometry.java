package rt;

/**
 * An interface to implement light sources. Light sources derive from 
 * this interface, and they store a reference to a {@link Material}
 * with an emission term. As an examples, see {@link rt.lightsources.PointLight}.
 */
public interface LightGeometry extends Intersectable {

	/**
	 * Sample a point on a light geometry.
	 */
	public HitRecord sample(float[] s);
}
