package rt;

import javax.imageio.ImageIO;

import rt.scenes.*;

import java.util.*;
import java.awt.image.*;
import java.io.*;

public class Main {

	static LinkedList<RenderTask> queue;
	static Counter tasksLeft;
	
	static public class Counter
	{
		public Counter(int n)
		{
			this.n = n;
		}
		
		public int n;
	}
	
	static public class RenderTask
	{
		public int left, right, bottom, top;
		public Integrator integrator;
		public Scene scene;
		public Sampler sampler;
		
		float tmp;
		
		public RenderTask(Scene scene, int left, int right, int bottom, int top)
		{			
			this.scene = scene;
			this.left = left;
			this.right = right;
			this.bottom = bottom;
			this.top = top;
			
//			integrator = integratorFactory.make(objects, lights, envMap);
//			pixelSampler = samplerFactory.make(2);
			integrator = scene.getIntegratorFactory().make(scene);
			sampler = scene.getSamplerFactory().make();
		}
	}
	
	static public class RenderThread implements Runnable
	{			
		public void run()
		{
			while(true)
			{
				RenderTask task;
				synchronized(queue)
				{
					if(queue.size() == 0) break;
					task = queue.poll();
				}
													
				for(int j=task.bottom; j<task.top; j++)
				{
					for(int i=task.left; i<task.right; i++)
					{					
	/*					for(int k=1; k<100000; k++)
						{
						
							task.tmp = (float)k*(float)k;
						}*/
						
						float samples[][] = task.integrator.makePixelSamples(task.sampler, task.scene.getSPP());
//						task.integrator.prepareSamples(task.pixelSampler.getNrOfSamples());
						
//						Iterator<float[]> pixelItr = task.pixelSampler.getIterator();						
//						while(pixelItr.hasNext())
						for(int k=0; k<samples.length; k++)
						{
//							float[] pixelSample = pixelItr.next();							
							Ray r = task.scene.getCamera().makeWorldSpaceRay(i, j, k, samples);
						
							Spectrum s = task.integrator.integrate(r);
							task.scene.getFilm().addSample((double)i+(double)samples[k][0], (double)j+(double)samples[k][1], s);											
						}
					}
				}
				
				synchronized(tasksLeft)
				{
					tasksLeft.n--;
					if(tasksLeft.n == 0) tasksLeft.notifyAll();
				}
			}
		}
	}
	
	public static void main(String[] args)
	{			
		int taskSize = 128;
		int nThreads = 1;
		
		// Scene to be rendered
//		DragonEnvMap scene = new DragonEnvMap();
//		Assignment1_Refractive scene = new Assignment1_Refractive();
		Scene scene = new Scene0();
		
//		camera = scene.camera;
//		film = scene.film;
//		objects = scene.objects;
//		lights = scene.lights;
//		envMap = scene.envMap;
		int width = scene.getFilm().getWidth();
		int height = scene.getFilm().getHeight();
//		integratorFactory = scene.integratorFactory;
//		samplerFactory = scene.samplerFactory;
		
//		integratorFactory.prepareScene(objects, lights, envMap);
		scene.prepare();
		
		// Make render tasks
		int nTasks = (int)Math.ceil((double)width/(double)taskSize) * (int)Math.ceil((double)height/(double)taskSize);
		tasksLeft = new Counter(nTasks);
		queue = new LinkedList<RenderTask>();
		for(int i=0; i<(int)Math.ceil((double)height/(double)taskSize); i++)
		{
			for(int j=0; j<(int)Math.ceil((double)width/(double)taskSize); j++)
			{
				RenderTask task = new RenderTask(scene, i*taskSize, Math.min((i+1)*taskSize,width), j*taskSize, Math.min((j+1)*taskSize,height));
				queue.add(task);
			}
		}
		
		Timer timer = new Timer();
		timer.reset();
		
		// Start render threads
		for(int i=0; i<nThreads; i++)
		{
			new Thread(new RenderThread()).start();
		}
		
		// Wait for threads to end
		int printed = 0;
		System.out.printf("Rendering image:\n");
		System.out.printf("0%%                                                50%%                                           100%%\n");
		System.out.printf("|---------|---------|---------|---------|---------|---------|---------|---------|---------|---------\n");
		synchronized(tasksLeft)
		{
			while(tasksLeft.n>0)
			{
				try
				{
					tasksLeft.wait(500);
				} catch (InterruptedException e) {}
				
				int toPrint = (int)( ((float)nTasks-(float)tasksLeft.n)/(float)nTasks*100-printed );
				for(int i=0; i<toPrint; i++)
					System.out.printf("*");
				printed += toPrint;
			}
		}
		
		System.out.printf("\n");
		System.out.printf("Image computed in %d ms.\n", timer.timeElapsed());
		
		BufferedImage image = scene.getTonemapper().process(scene.getFilm());
		try
		{
			ImageIO.write(image, "png", new File(scene.getOutputFilename()+".png"));
		} catch (IOException e) {}
	}
	
}
