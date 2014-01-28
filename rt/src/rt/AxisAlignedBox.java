package rt;

import javax.vecmath.*;

public class AxisAlignedBox {

	float bounds[][];
	
	public AxisAlignedBox(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax)
	{
		bounds = new float[3][2];
		bounds[0][0] = xMin;
		bounds[0][1] = xMax;
		bounds[1][0] = yMin;
		bounds[1][1] = yMax;
		bounds[2][0] = zMin;
		bounds[2][1] = zMax;
	}
	
	/**
	 * Tests if two axis aligned boxes intersect.
	 */
	public boolean intersects(AxisAlignedBox b)
	{
		return !(bounds[0][1] <= b.bounds[0][0] || b.bounds[0][1] <= bounds[0][0]) &&
			   !(bounds[1][1] <= b.bounds[1][0] || b.bounds[1][1] <= bounds[1][0]) &&
			   !(bounds[2][1] <= b.bounds[2][0] || b.bounds[2][1] <= bounds[2][0]);
	}

	public float surfaceArea()
	{
		return 2*(bounds[0][1]-bounds[0][0])*(bounds[1][1]-bounds[1][0])+
			   2*(bounds[1][1]-bounds[1][0])*(bounds[2][1]-bounds[2][0])+
			   2*(bounds[2][1]-bounds[2][0])*(bounds[0][1]-bounds[0][0]);
	}

	/**
	 * Returns the two (or no) intersections of ray with this box.
	 */
	public Tuple2f intersections(Ray r)
	{
		float txmin, txmax, tymin, tymax, tzmin, tzmax;
		
		if(r.direction.x==0)
		{
			txmin = Float.MIN_VALUE;
			txmax = Float.MAX_VALUE;
		}
		else
		{
			txmin = (bounds[0][0]-r.origin.x)/r.direction.x;
			txmax = (bounds[0][1]-r.origin.x)/r.direction.x;
			if(txmin>txmax) 
			{
				float tmp = txmin;
				txmin = txmax;
				txmax = tmp;
			}
		}
		if(r.direction.y==0)
		{
			tymin = Float.MIN_VALUE;
			tymax = Float.MAX_VALUE;
		}
		else
		{
			tymin = (bounds[1][0]-r.origin.y)/r.direction.y;
			tymax = (bounds[1][1]-r.origin.y)/r.direction.y;
			if(tymin>tymax) 
			{
				float tmp = tymin;
				tymin = tymax;
				tymax = tmp;
			}

		}
		if(r.direction.z==0)
		{
			tzmin = Float.MIN_VALUE;
			tzmax = Float.MAX_VALUE;
		}
		else
		{
			tzmin = (bounds[2][0]-r.origin.z)/r.direction.z;
			tzmax = (bounds[2][1]-r.origin.z)/r.direction.z;
			if(tzmin>tzmax) 
			{
				float tmp = tzmin;
				tzmin = tzmax;
				tzmax = tmp;
			}
		}
		
		Tuple2f i = intervalIntersection(new Vector2f(txmin,txmax), new Vector2f(tymin,tymax));
		if(i==null) return null;
		return intervalIntersection(i, new Vector2f(tzmin,tzmax));
	}
	
	public boolean intervalOverlap(float min1, float max1, float min2, float max2)
	{
		if(max1<=min2) return false;
		if(min1>=max2) return false;
		return true;
	}
	
	public Tuple2f intervalIntersection(Tuple2f i0, Tuple2f i1)
	{
		float start, end;
		if(i0.x<i1.x) start = i1.x; else start = i0.x;
		if(i0.y>i1.y) end = i1.y; else end = i0.y;
		if(end-start<=0)
		{
			return null;
		} 
		else
		{
			return new Vector2f(start, end);
		}
	}
	
	public AxisAlignedBox transform(Matrix4f t)
	{
		float tbounds[][] = new float[3][2];
		tbounds[0][0] = Float.MAX_VALUE;
		tbounds[0][1] = Float.MIN_VALUE;
		tbounds[1][0] = Float.MAX_VALUE;
		tbounds[1][1] = Float.MIN_VALUE;
		tbounds[2][0] = Float.MAX_VALUE;
		tbounds[2][1] = Float.MIN_VALUE;

		Vector3f vertices[] = new Vector3f[8];
		for(int i = 0; i<8; i++)
		{
			vertices[i] = new Vector3f();
			vertices[i].x = bounds[0][i & 1];
			vertices[i].y = bounds[0][(i & 2) >> 1];
			vertices[i].z = bounds[0][(i & 4) >> 2];
			t.transform(vertices[i]);
			
			tbounds[0][0] = Math.min(tbounds[0][0], vertices[i].x);
			tbounds[0][1] = Math.max(tbounds[0][1], vertices[i].x);
			tbounds[1][0] = Math.min(tbounds[1][0], vertices[i].y);
			tbounds[1][1] = Math.max(tbounds[1][1], vertices[i].y);
			tbounds[2][0] = Math.min(tbounds[2][0], vertices[i].z);
			tbounds[2][1] = Math.max(tbounds[2][1], vertices[i].z);
		}
		
		return new AxisAlignedBox(tbounds[0][0], tbounds[0][1], tbounds[1][0], tbounds[1][1], tbounds[2][0], tbounds[2][1]);
	}
}
