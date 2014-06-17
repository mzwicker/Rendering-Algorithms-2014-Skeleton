package rt;

import javax.imageio.ImageIO;

import rt.basicscenes.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.awt.image.*;
import java.io.*;

/**
 * The main rendering loop. Provides multi-threading support. The {@link Main#scene} to be rendered
 * is hard-coded here, so you can easily change it. The {@link Main#scene} contains 
 * all configuration information for the renderer.
 */
public class Main {

	/** 
	 * The scene to be rendered.
	 */
	public static Scene scene = new Dodecahedron();

	static LinkedList<RenderTask> queue;

	/**
	 * A render task represents a rectangular image region that is rendered
	 * by a thread in one chunk.
	 */
	static public class RenderTask implements Runnable
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

		@Override
		public void run() {
			for(int j=bottom; j<top; j++)
			{
				for(int i=left; i<right; i++)
				{											
					float samples[][] = integrator.makePixelSamples(sampler, scene.getSPP());
					// For all samples of the pixel
					for(int k = 0; k < samples.length; k++)
					{	
						// Make ray
						Ray r = scene.getCamera().makeWorldSpaceRay(i, j, samples[k]);

						// Evaluate ray
						Spectrum s = integrator.integrate(r);							
						
						// Write to film
						scene.getFilm().addSample(i + samples[k][0], j + samples[k][1], s);
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{			
		int taskSize = 4;	// Each task renders a square image block of this size
		int nThreads = Runtime.getRuntime().availableProcessors();	// Number of threads to be used for rendering
		int width = scene.getFilm().getWidth();
		int height = scene.getFilm().getHeight();

		scene.prepare();
		
		int nTasks = (int)(Math.ceil(width/(double)taskSize) * Math.ceil(height/(double)taskSize));
		//ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(nThreads);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(nTasks) );
		// Make render tasks, split image into blocks to be rendered by the tasks
		for(int j=0; j < Math.ceil(height/(float)taskSize); j++) {
			for(int i=0; i < Math.ceil(width/(float)taskSize); i++) {
				RenderTask task = new RenderTask(scene, i*taskSize, Math.min((i+1)*taskSize, width), j*taskSize, 
																	Math.min((j+1)*taskSize, height));
				executor.execute(task);
			}
		}
		Timer timer = new Timer();
		timer.reset();
		
		
		// Wait for threads to end
		System.out.printf("Rendering scene %s to file %s: \n", scene.getClass().toString(), scene.outputFilename);
		System.out.println("0%                                                50%                                           100%");
		System.out.println("|---------|---------|---------|---------|---------|---------|---------|---------|---------|--------|");
		executor.shutdown();
		int printed = 0;
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int toPrint = (int) (executor.getCompletedTaskCount()/(float)executor.getTaskCount()*100);
			for (; printed < toPrint; printed++) {
				System.out.print("*");
			}
		}
		System.out.println();
		
		long time_ms = timer.timeElapsed();
		long time_s = time_ms / 1000;
		long time_min =  time_s / 60;
		String timing_output = String.format("Image computed in %d ms = %d min, %d sec.\n", time_ms, time_min, time_s - time_min*60);
		System.out.print(timing_output);
		
		// Tone map output image and writ to file
		BufferedImage image = scene.getTonemapper().process(scene.getFilm());
		try
		{
			ImageIO.write(image, "png", new File(scene.getOutputFilename()+".png"));
		} catch (IOException e) {}
	}
	
}
