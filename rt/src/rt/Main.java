package rt;

import javax.imageio.ImageIO;

import rt.scenes.*;

import java.util.*;
import java.awt.image.*;
import java.io.*;

/**
 * The main rendering loop. Provides multi-threading support. The scene to be rendered
 * is hard-coded here, but you can easily change it. The scene contains all configuration
 * information for the renderer.
 */
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
	
	/**
	 * A render task represents a rectangular image region that is rendered
	 * by a thread in one chunk.
	 */
	static public class RenderTask
	{
		public int left, right, bottom, top;
		public Integrator integrator;
		public Scene scene;
		public Sampler sampler;
		
		public RenderTask(Scene scene, int left, int right, int bottom, int top)
		{			
			this.scene = scene;
			this.left = left;
			this.right = right;
			this.bottom = bottom;
			this.top = top;

			// The render task has its own sampler and integrator. This way threads don't 
			// compete for access to a shared sampler/integrator, and thread contention
			// can be reduced. 
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
													
				// Render the image block represented by the task
				
				// For all pixels
				for(int j=task.bottom; j<task.top; j++)
				{
					for(int i=task.left; i<task.right; i++)
					{											
						float samples[][] = task.integrator.makePixelSamples(task.sampler, task.scene.getSPP());

						// For all samples of the pixel
						for(int k=0; k<samples.length; k++)
						{							
							// Make ray
							Ray r = task.scene.getCamera().makeWorldSpaceRay(i, j, k, samples);					
							
							// Evaluate ray
							Spectrum s = task.integrator.integrate(r);							
							
							// Write to film
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
		int taskSize = 32;	// Each task renders a square image block of this size
		int nThreads = 8;	// Number of threads to be used for rendering
		
		// Scene to be rendered
		Scene scene = new Scene0();
		
		int width = scene.getFilm().getWidth();
		int height = scene.getFilm().getHeight();

		scene.prepare();
		
		// Make render tasks, split image into blocks to be rendered by the tasks
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
		
		// Tone map output image and writ to file
		BufferedImage image = scene.getTonemapper().process(scene.getFilm());
		try
		{
			ImageIO.write(image, "png", new File(scene.getOutputFilename()+".png"));
		} catch (IOException e) {}
	}
	
}
