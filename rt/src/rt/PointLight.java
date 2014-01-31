package rt;

import javax.vecmath.*;

public class PointLight implements LightSource {

	Vector3f position;
	Spectrum emission;
	
	public PointLight(Vector3f position, Spectrum emission)
	{
		this.position = new Vector3f(position);
		this.emission = new Spectrum(emission);
	}
	
	public Spectrum getEmission(float[] s) {
		return emission;
	}
	
	public LightGeometry getGeometry(float[] s) {
		return new LightGeometry(position, null);
	}

}
