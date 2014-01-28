package rt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import javax.vecmath.*;

public class PhotonMapIntegrator implements Integrator {

	private Intersectable scene;
	private int maxBounces;
	public KdTree<HitRecord> globalMap, causticMap;
	private int k;
	private float maxD, minD;
	private ArrayList<Iterator<float[]>> sampleItr;
	private boolean useFinalGathering;
	private LightList lights;

	private int nFound[];
	private HitRecord neighbors[];
	private float distances[];
	
	public PhotonMapIntegrator(Intersectable scene, LightList lights, KdTree<HitRecord> globalMap, KdTree<HitRecord> causticMap, boolean useFinalGathering, int k, float maxD, float minD)
	{
		this.scene = scene;
		this.lights = lights;
		maxBounces = 1;
		this.globalMap = globalMap;
		this.causticMap = causticMap;
		this.k = k;
		this.maxD = maxD;
		this.minD = minD;
				
		this.useFinalGathering = useFinalGathering;
		
		// Used for photon gathering
		nFound = new int[1];
		neighbors = new HitRecord[200];
		distances = new float[200];
	}
	
	public Spectrum integrate(Ray r) {
		
		HitRecord hitRecord = scene.intersect(r);
		
		Vector3f d = new Vector3f();
		Vector3f o = new Vector3f();
		Vector3f tmp = new Vector3f();
		
		Spectrum s = new Spectrum(); 
		Spectrum alpha = new Spectrum(1.f, 1.f, 1.f);
		
		float[] sample = sampleItr.get(0).next();
		
		while(hitRecord!=null)
		{
			hitRecord.wIn.normalize();
			Material material = hitRecord.material;
						
			// Get surface emission, if any
			if(hitRecord.spectrum != null)
			{
				s.r += alpha.r * hitRecord.spectrum.r;
				s.g += alpha.g * hitRecord.spectrum.g;
				s.b += alpha.b * hitRecord.spectrum.b;
			}

			// Shadow ray, no need to do this if surface is a mirror
			if(!material.mirrorReflect())
			{
				Iterator<LightSource> lightItr = lights.iterator();
				while(lightItr.hasNext())
				{
					// Pick light
					LightSource light = lightItr.next();
	
					// Avoid shooting shadow ray towards light itself
					if(hitRecord.intersectable != (Intersectable)light) 
					{
						LightSource.LightGeometry lightGeometry = light.sampleGeometry(sample);
						
						// Make shadow ray
						d.x = lightGeometry.position.x;
						d.y = lightGeometry.position.y;
						d.z = lightGeometry.position.z;
						d.sub(hitRecord.position);
						o.x = hitRecord.position.x;
						o.y = hitRecord.position.y;
						o.z = hitRecord.position.z;
						o.scaleAdd(0.0001f, d, o);
						Ray shadowRay = new Ray(o, d);
						d.normalize();
						
						// Cosine factor
						float c = d.dot(hitRecord.normal);
	
						// Shoot shadow ray, if light is in front of surface
						HitRecord shadowHit = null;
						if(c>0)
							shadowHit = scene.intersect(shadowRay);
						
						// If shadow ray is front facing and not blocked, shade
						if(c>0 && (shadowHit==null || shadowHit.t>0.999f))
						{							
							// Diffuse shading
							Spectrum kd = material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
							// Specular shading
							Spectrum ks;
							if(!material.mirrorReflect()) 
								ks = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
							else
								ks = new Spectrum();
													
							Spectrum l = light.sampleSpectrum(sample);
						
							// Compute geometry term
							tmp.sub(lightGeometry.position,hitRecord.position);
							float t = tmp.length();
							
							if(lightGeometry.normal != null)
							{
								// Area light source
								
								tmp = new Vector3f(lightGeometry.normal);
								shadowRay.direction.normalize();
								if(tmp.dot(shadowRay.direction)>0)
									t = 0;
								else
									t = Math.abs(tmp.dot(shadowRay.direction)) / (t*t);
								
								// Divide geometry term by pdf. Note: pdf = 1/(area of light), i.e. 1/pdf = (area of light)
								t = t*light.surfaceArea();
							} else
							{
								// Point light source
								t = 1 / (t*t);
							}
			
							// Final shading: alpha * light * shading coefficients * cosine factor * (geometry term / pdf)
							s.r += alpha.r * l.r * (kd.r+ks.r) * c * t;
							s.g += alpha.g * l.g * (kd.g+ks.g) * c * t;
							s.b += alpha.b * l.b * (kd.b+ks.b) * c * t;
						} // Shadow ray not blocked
					} // Loop over light samples
				} // Loop over lights
				
				// Add caustics, if there are any caustic photons
				if(causticMap!=null)
				{
					Spectrum caustic = radianceEstimate(hitRecord, causticMap);
					
					// Add caustic
					s.r += alpha.r * caustic.r;
					s.g += alpha.g * caustic.g;
					s.b += alpha.g * caustic.g;
				}		
					
				// Generate gather ray with cosine distribution
//	 			float[] sample = sampleItr.get(0).next();
				float phi = (float)(2*Math.PI*sample[0]);
				d.x = 0.f;
				d.y = 0.f;
				d.z = 0.f;
				tmp.x = hitRecord.t1.x;
				tmp.y = hitRecord.t1.y;
				tmp.z = hitRecord.t1.z;
				tmp.normalize();
				// cos(phi)*sqrt(sample[1]); note r = sqrt(sample[1]) 
				d.scaleAdd((float)(Math.cos(phi)*Math.sqrt(sample[1])), tmp, d);
				tmp.x = hitRecord.t2.x;
				tmp.y = hitRecord.t2.y;
				tmp.z = hitRecord.t2.z;
				tmp.normalize();
				// sin(phi)*sqrt(sample[1]); note r = sqrt(sample[1])
				d.scaleAdd((float)(Math.sin(phi)*Math.sqrt(sample[1])), tmp, d);
				// sqrt(1-sample[1]); note r^2 = sample[1]
				d.scaleAdd((float)Math.sqrt(1-sample[1]), hitRecord.normal, d);
				d.normalize();
				
				tmp.scaleAdd(0.00001f, d, hitRecord.position);
				Ray rGather = new Ray(tmp, d);
				HitRecord hitRecordGather = scene.intersect(rGather);
			
				// Final gathering
				if(hitRecordGather!=null)
				{
					d.normalize();
					Spectrum dd = hitRecord.material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
					
					// Note factor pi, which comes from normalization factor of cosine distribution
					Spectrum g = radianceEstimate(hitRecordGather, globalMap);
					g.r *= (float)Math.PI;
					g.g *= (float)Math.PI;
					g.b *= (float)Math.PI;
					s.r += dd.r * g.r;
					s.g += dd.g * g.g;
					s.b += dd.b * g.b;
				} 

			} // Material is not a mirror
						
			// Follow reflection or refraction, if necessary
			float pReflect, pRefract;
			if(material.mirrorReflect() && !material.mirrorRefract())
			{
				pReflect = 1;
				pRefract = 0;
			} else if(material.mirrorReflect() && material.mirrorRefract())
			{
				pReflect = .5f;
				pRefract = .5f;
			} else
			{
				pReflect = 0.f;
				pRefract = 0.f;
			}
			
			Spectrum f = null;
			float p = 0.f;
			float t = (float)Math.random();
			Vector3f newD;
			
			if(t<pReflect)
			{
				// Follow reflection
				newD = material.sampleReflect(hitRecord, sample);
				f = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, newD);
				p = pReflect;
			} else if(t<pReflect+pRefract)
			{
				// Follow refraction
				newD = material.sampleRefract(hitRecord, sample);
				if(newD!=null)
					f = material.evaluateRefractiveBRDF(hitRecord, hitRecord.wIn, newD);
				p = pRefract;
			} else
			{
				newD = null;
			}
			
			if(newD!=null)
			{
				// The length of d needs to encode the sample density!
				float density = newD.length();
				newD.normalize();
								
				// Cosine term
				float cosine = Math.abs(hitRecord.normal.dot(newD));
				
				// Update alpha
				alpha.r = alpha.r * f.r * cosine / (density * p);
				alpha.g = alpha.g * f.g * cosine / (density * p);
				alpha.b = alpha.b * f.b * cosine / (density * p);
				
				// Shoot next ray segment
				r.origin.scaleAdd(0.0001f, newD, hitRecord.position);
				r.direction = newD;
				hitRecord = scene.intersect(r);
			} else
			{
				hitRecord = null;
			}			
		} // while(hitRecord!=null)
		
		return s;
	}

	public void prepareSamples(int n) {
		sampleItr = new ArrayList<Iterator<float[]>>(maxBounces);
		for(int i=0; i<maxBounces; i++)
		{
			Sampler sampler = new JitteredSampler(n, 2);
			sampler.makeSamples();
			sampleItr.add(i, sampler.getIterator());
		}
	}
	
	private Spectrum radianceEstimate(HitRecord hitRecord, KdTree<HitRecord> map)
	{
		// Gather neighbors
		map.getNeighbors(hitRecord.position, k, maxD, minD, nFound, neighbors, distances);

		// Outgoing direction, pointing away from surface
		Vector3f wOut = new Vector3f(hitRecord.wIn);
		wOut.normalize();
		wOut.negate();
		
		// Sum up radiance
		Spectrum spectrum = new Spectrum();
		Spectrum tmp;
		for(int i=0; i<nFound[0]; i++)
		{
			HitRecord photonHitRecord = neighbors[i];
			tmp = photonHitRecord.material.evaluateDiffuseBRDF(photonHitRecord, wOut, photonHitRecord.wIn);
			spectrum.r += photonHitRecord.spectrum.r*tmp.r;
			spectrum.g += photonHitRecord.spectrum.g*tmp.g;
			spectrum.b += photonHitRecord.spectrum.b*tmp.b;
		}
		
		// Constant filter, divide by area of circle enclosing neighbors
		float d = distances[0];
		float a = (float)(1.f/(Math.PI*d*d));
		spectrum.r *= a;
		spectrum.g *= a;
		spectrum.b *= a;
		
		return spectrum;
	}
}
