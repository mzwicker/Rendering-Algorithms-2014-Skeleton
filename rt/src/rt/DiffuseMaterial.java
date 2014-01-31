package rt;

import javax.vecmath.Vector3f;

public class DiffuseMaterial implements Material {

	Spectrum kd;
	
	public DiffuseMaterial(Spectrum kd)
	{
		this.kd = new Spectrum(kd);
	}
	
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		return new Spectrum(kd);
	}

}
