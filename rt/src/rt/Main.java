package rt;

import javax.imageio.ImageIO;
import rt.scenes.*;
import java.util.*;


import java.awt.image.*;
import java.io.*;

public class Main {

	static LinkedList<RenderTask> queue;
	static Counter tasksLeft;
	
	static Scene scene;
	
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
		public Sampler pixelSampler;
		
		public RenderTask(int left, int right, int bottom, int top)
		{			
			this.left = left;
			this.right = right;
			this.bottom = bottom;
			this.top = top;
			
//			integrator = integratorFactory.make(objects, lights, envMap);
//			pixelSampler = samplerFactory.make(2);
			integrator = scene.makeIntegrator();
			pixelSampler = scene.makeSampler(2);
			pixelSampler.makeSamples();
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
						task.integrator.prepareSamples(task.pixelSampler.getNrOfSamples());
						
						Iterator<float[]> pixelItr = task.pixelSampler.getIterator();						
						while(pixelItr.hasNext())
						{
							float[] pixelSample = pixelItr.next();
							float x = pixelSample[0]+(float)i;
							float y = pixelSample[1]+(float)j;									
							Ray r = scene.getCamera().makeWorldSpaceRay(x, y);
						
							Spectrum s = task.integrator.integrate(r);
							scene.getFilm().addSample(x, y, s);							
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
		int taskSize = 16;
		int nThreads = 1;
		
		// Scene to be rendered
//		DragonEnvMap scene = new DragonEnvMap();
		Assignment1_Refractive scene = new Assignment1_Refractive();
		
		camera = scene.camera;
		film = scene.film;
		objects = scene.objects;
		lights = scene.lights;
//		envMap = scene.envMap;
		int width = film.width;
		int height = film.height;
		integratorFactory = scene.integratorFactory;
		samplerFactory = scene.samplerFactory;
		
		Timer timer = new Timer();
		timer.reset();
		
		integratorFactory.prepareScene(objects, lights, envMap);
		
		// Make render tasks
		int nTasks = (int)Math.ceil((double)width/(double)taskSize) * (int)Math.ceil((double)height/(double)taskSize);
		tasksLeft = new Counter(nTasks);
		queue = new LinkedList<RenderTask>();
		for(int i=0; i<(int)Math.ceil((double)height/(double)taskSize); i++)
		{
			for(int j=0; j<(int)Math.ceil((double)width/(double)taskSize); j++)
			{
				RenderTask task = new RenderTask(i*taskSize, Math.min((i+1)*taskSize,width), j*taskSize, Math.min((j+1)*taskSize,height));
				queue.add(task);
			}
		}
		
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
		
		BufferedImage image = Tonemapper.clamp(film);
		try
		{
			ImageIO.write(image, "png", new File(scene.outputFileName));
		} catch (IOException e) {}
	}
	
}
