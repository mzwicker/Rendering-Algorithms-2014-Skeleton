package rt;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import rt.PathTracingIntegrator.ScatteringType;

public class PhotonMapIntegratorFactory extends IntegratorFactory {

	ArrayList<HitRecord> globalList, causticList;
	KdTree<HitRecord> globalMap, causticMap;
	PhotonMapIntegrator photonMapIntegrator;
	boolean useCausticMap, useGlobalMap, useFinalGathering;
	int nPhotons;
	int k;
	float maxD, minD;

	/**
	 * @param useCausticMap
	 * @param useGlobalMap
	 * @param useFinalGathering
	 * @param nPhotons Number of photons to shoot
	 * @param k Desired number of photons per radiance estimate
	 * @param maxD Maximum search radius for radiance estimate
	 * @param minD Minimum search radius for radiance estimate
	 */
	public PhotonMapIntegratorFactory(boolean useCausticMap, boolean useGlobalMap, boolean useFinalGathering, int nPhotons, int k, float maxD, float minD)
	{
		this.useCausticMap = useCausticMap;
		this.useGlobalMap = useGlobalMap;
		this.useFinalGathering = useFinalGathering;
		this.nPhotons = nPhotons;
		this.k = k;
		this.maxD = maxD;
		this.minD = minD;
	}
	
	public Integrator make(Intersectable objects, LightList lights, EnvironmentMap envMap) {		
		return new PhotonMapIntegrator(objects, lights, globalMap, causticMap, useFinalGathering, k, maxD, minD);
	}

	public void prepareScene(Intersectable objects, LightList lights, EnvironmentMap envMap) {
		
		globalList = new ArrayList<HitRecord>();
		causticList = new ArrayList<HitRecord>();
		
		float sample[] = new float[4];
		int maxBounces = 4;
		
		for(int i=0; i<nPhotons; i++)
		{			
			// Pick light randomly
			LightSource light = lights.get((int)Math.max(0, Math.round( Math.random()*lights.size()-0.5000001f )));
			float lightPdf = 1.f/lights.size();

			// Sample a photon on the light
			sample[0] = (float)Math.random();
			sample[1] = (float)Math.random();
			sample[2] = (float)Math.random();
			sample[3] = (float)Math.random();
			HitRecord photon = light.samplePhoton(sample);
		
			// Start as non-caustic photon
			boolean isCausticPhoton = false;
			
			// Weight photons using total number of photons and pdf to pick current light
			photon.spectrum.r /= ((float)nPhotons*lightPdf);
			photon.spectrum.g /= ((float)nPhotons*lightPdf);
			photon.spectrum.b /= ((float)nPhotons*lightPdf);
			
			// Bounce around and deposit photon in scene
			Ray r = new Ray(photon.position, photon.wIn);
			HitRecord hitRecord = objects.intersect(r);
			
			int bounce = 0;
			while(bounce<maxBounces && hitRecord!=null)
			{
				// Deposit photon, i.e., add it to global or caustic list
				// Don't deposit on mirror surfaces
				if(!hitRecord.material.mirrorReflect() && !hitRecord.material.mirrorRefract())
				{
					hitRecord.spectrum = new Spectrum(photon.spectrum);
					if(isCausticPhoton)
						causticList.add(hitRecord);
					else
						globalList.add(hitRecord);
				}

				// Russian roulette
				float q;
				if(bounce>1)
				{
					q = (float)Math.random();
					if(q<0.5) break;
					q = 0.5f;
				} else
				{
					q = 0.f;
				}
				
				// Find next hit point
				Material material = hitRecord.material;
				hitRecord.wIn.normalize();

				// Determine scattering type to be evaluated
				float p = (float)Math.random();		
				float diffusePdf = material.getDiffuseContribution();
				float reflectPdf = material.getReflectContribution();
				float refractPdf = material.getRefractContribution();
				ScatteringType type;
				float typePdf;

				if(p<=diffusePdf)
				{
					type = ScatteringType.DIFFUSE;
					typePdf = diffusePdf;
				} else if(p<=diffusePdf+reflectPdf)
				{
					type = ScatteringType.REFLECT;
					typePdf = reflectPdf;
				} else
				{
					type = ScatteringType.REFRACT;
					typePdf = refractPdf;
				}
			
				// Sample new direction
				Vector3f d;
				sample[0] = (float)Math.random();
				sample[1] = (float)Math.random();
				if(type == ScatteringType.DIFFUSE)
					d = material.sampleDiffuse(hitRecord, sample);
				else if(type == ScatteringType.REFLECT)
					d = material.sampleReflect(hitRecord, sample);
				else
					d = material.sampleRefract(hitRecord, sample);

				// If total internal reflection occurs we terminate the path
				if(d == null) break;
				
				// The length of d needs to encode the sample density!
				float density = d.length();
				d.normalize();
								
				// Update alpha
				Spectrum f;
				if(type == ScatteringType.DIFFUSE)
				{
					f = material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
					isCausticPhoton = false;
				} else if(type == ScatteringType.REFLECT)
				{
					f = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
					// Switch state to caustic if we reflect off of a mirror
					if(material.mirrorReflect()) isCausticPhoton = true;
				} else
				{
					f = material.evaluateRefractiveBRDF(hitRecord, hitRecord.wIn, d);
					// Switch state to caustic if we refract mirror-like					
					if(material.mirrorRefract()) isCausticPhoton = true;
				}
				
				// Cosine term
				float cosine = Math.abs(hitRecord.normal.dot(d));
				
				photon.spectrum.r = photon.spectrum.r * f.r * cosine / ((1-q) * density * typePdf);
				photon.spectrum.g = photon.spectrum.g * f.g * cosine / ((1-q) * density * typePdf);
				photon.spectrum.b = photon.spectrum.b * f.b * cosine / ((1-q) * density * typePdf);
				
				// Shoot next ray segment
				r.origin.scaleAdd(0.0001f, d, hitRecord.position);
				r.direction = d;
				hitRecord = objects.intersect(r);

				bounce++;			
			}
		}
		
		// Construct kd-tree
		globalMap = new KdTree<HitRecord>(globalList);
		if(causticList.size()>0)
			causticMap = new KdTree<HitRecord>(causticList);
		else
			causticMap = null;
	}

}
