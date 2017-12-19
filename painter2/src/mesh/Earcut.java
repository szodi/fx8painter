package mesh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Earcut
{

	private Earcut()
	{
	};

	/**
	 * Triangulates the given polygon
	 * 
	 * @param data
	 *            is a flat array of vertice coordinates like [x0,y0, x1,y1, x2,y2, ...].
	 * @return List containing groups of three vertice indices in the resulting array forms a triangle.
	 */
	public static List<Integer> earcut(double[] data)
	{
		return earcut(data, null, 2);
	}

	/**
	 * Triangulates the given polygon
	 * 
	 * @param data
	 *            is a flat array of vertice coordinates like [x0,y0, x1,y1, x2,y2, ...].
	 * @param holeIndices
	 *            is an array of hole indices if any (e.g. [5, 8] for a 12-vertice input would mean one hole with vertices 5�7 and another with 8�11).
	 * @param dim
	 *            is the number of coordinates per vertice in the input array
	 * @return List containing groups of three vertice indices in the resulting array forms a triangle.
	 */
	public static List<Integer> earcut(double[] data, int[] holeIndices, int dim)
	{

		boolean hasHoles = holeIndices != null && holeIndices.length > 0;
		int outerLen = hasHoles ? holeIndices[0] * dim : data.length;

		Node outerNode = linkedList(data, 0, outerLen, dim, true);

		List<Integer> triangles = new ArrayList<>();

		if (outerNode == null)
			return triangles;

		double minX = 0;
		double minY = 0;
		double maxX = 0;
		double maxY = 0;
		double size = Double.MIN_VALUE;

		if (hasHoles)
			outerNode = eliminateHoles(data, holeIndices, outerNode, dim);

		// if the shape is not too simple, we'll use z-order curve hash later;
		// calculate polygon bbox
		if (data.length > 80 * dim)
		{
			minX = maxX = data[0];
			minY = maxY = data[1];

			for (int i = dim; i < outerLen; i += dim)
			{
				double x = data[i];
				double y = data[i + 1];
				if (x < minX)
					minX = x;
				if (y < minY)
					minY = y;
				if (x > maxX)
					maxX = x;
				if (y > maxY)
					maxY = y;
			}

			// minX, minY and size are later used to transform coords into
			// integers for z-order calculation
			size = Math.max(maxX - minX, maxY - minY);
		}

		earcutLinked(outerNode, triangles, dim, minX, minY, size, Integer.MIN_VALUE);

		return triangles;
	}

	private static void earcutLinked(Node ear, List<Integer> triangles, int dim, double minX, double minY, double size, int pass)
	{
		if (ear == null)
			return;

		// interlink polygon nodes in z-order
		if (pass == Integer.MIN_VALUE && size != Double.MIN_VALUE)
			indexCurve(ear, minX, minY, size);

		Node stop = ear;

		// iterate through ears, slicing them one by one
		while (ear.prev != ear.next)
		{
			Node prev = ear.prev;
			Node next = ear.next;

			if (size != Double.MIN_VALUE ? isEarHashed(ear, minX, minY, size) : isEar(ear))
			{
				// cut off the triangle
				triangles.add(prev.i / dim);
				triangles.add(ear.i / dim);
				triangles.add(next.i / dim);

				removeNode(ear);

				// skipping the next vertice leads to less sliver triangles
				ear = next.next;
				stop = next.next;

				continue;
			}

			ear = next;

			// if we looped through the whole remaining polygon and can't find
			// any more ears
			if (ear == stop)
			{
				// try filtering points and slicing again
				if (pass == Integer.MIN_VALUE)
				{
					earcutLinked(filterPoints(ear, null), triangles, dim, minX, minY, size, 1);

					// if this didn't work, try curing all small
					// self-intersections locally
				}
				else if (pass == 1)
				{
					ear = cureLocalIntersections(ear, triangles, dim);
					earcutLinked(ear, triangles, dim, minX, minY, size, 2);

					// as a last resort, try splitting the remaining polygon
					// into two
				}
				else if (pass == 2)
				{
					splitEarcut(ear, triangles, dim, minX, minY, size);
				}

				break;
			}
		}
	}

	private static void splitEarcut(Node start, List<Integer> triangles, int dim, double minX, double minY, double size)
	{
		// look for a valid diagonal that divides the polygon into two
		Node a = start;
		do
		{
			Node b = a.next.next;
			while (b != a.prev)
			{
				if (a.i != b.i && isValidDiagonal(a, b))
				{
					// split the polygon in two by the diagonal
					Node c = splitPolygon(a, b);

					// filter colinear points around the cuts
					a = filterPoints(a, a.next);
					c = filterPoints(c, c.next);

					// run earcut on each half
					earcutLinked(a, triangles, dim, minX, minY, size, Integer.MIN_VALUE);
					earcutLinked(c, triangles, dim, minX, minY, size, Integer.MIN_VALUE);
					return;
				}
				b = b.next;
			}
			a = a.next;
		}
		while (a != start);
	}

	private static boolean isValidDiagonal(Node a, Node b)
	{
		return a.next.i != b.i && a.prev.i != b.i && !intersectsPolygon(a, b) && locallyInside(a, b) && locallyInside(b, a) && middleInside(a, b);
	}

	private static boolean middleInside(Node a, Node b)
	{
		Node p = a;
		boolean inside = false;
		double px = (a.x + b.x) / 2;
		double py = (a.y + b.y) / 2;
		do
		{
			if (((p.y > py) != (p.next.y > py)) && (px < (p.next.x - p.x) * (py - p.y) / (p.next.y - p.y) + p.x))
				inside = !inside;
			p = p.next;
		}
		while (p != a);

		return inside;
	}

	private static boolean intersectsPolygon(Node a, Node b)
	{
		Node p = a;
		do
		{
			if (p.i != a.i && p.next.i != a.i && p.i != b.i && p.next.i != b.i && intersects(p, p.next, a, b))
				return true;
			p = p.next;
		}
		while (p != a);

		return false;
	}

	private static boolean intersects(Node p1, Node q1, Node p2, Node q2)
	{
		if ((equals(p1, q1) && equals(p2, q2)) || (equals(p1, q2) && equals(p2, q1)))
			return true;
		return area(p1, q1, p2) > 0 != area(p1, q1, q2) > 0 && area(p2, q2, p1) > 0 != area(p2, q2, q1) > 0;
	}

	private static Node cureLocalIntersections(Node start, List<Integer> triangles, int dim)
	{
		Node p = start;
		do
		{
			Node a = p.prev, b = p.next.next;

			if (!equals(a, b) && intersects(a, p, p.next, b) && locallyInside(a, b) && locallyInside(b, a))
			{

				triangles.add(a.i / dim);
				triangles.add(p.i / dim);
				triangles.add(b.i / dim);

				// remove two nodes involved
				removeNode(p);
				removeNode(p.next);

				p = start = b;
			}
			p = p.next;
		}
		while (p != start);

		return p;
	}

	private static boolean isEar(Node ear)
	{
		Node a = ear.prev, b = ear, c = ear.next;

		if (area(a, b, c) >= 0)
			return false; // reflex, can't be an ear

		// now make sure we don't have other points inside the potential ear
		Node p = ear.next.next;

		while (p != ear.prev)
		{
			if (pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0)
				return false;
			p = p.next;
		}

		return true;
	}

	private static boolean isEarHashed(Node ear, double minX, double minY, double size)
	{
		Node a = ear.prev;
		Node b = ear;
		Node c = ear.next;

		if (area(a, b, c) >= 0)
			return false; // reflex, can't be an ear

		// triangle bbox; min & max are calculated like this for speed
		double minTX = a.x < b.x ? (a.x < c.x ? a.x : c.x) : (b.x < c.x ? b.x : c.x), minTY = a.y < b.y ? (a.y < c.y ? a.y : c.y) : (b.y < c.y ? b.y : c.y), maxTX = a.x > b.x ? (a.x > c.x ? a.x : c.x) : (b.x > c.x ? b.x : c.x),
				maxTY = a.y > b.y ? (a.y > c.y ? a.y : c.y) : (b.y > c.y ? b.y : c.y);

		// z-order range for the current triangle bbox;
		double minZ = zOrder(minTX, minTY, minX, minY, size);
		double maxZ = zOrder(maxTX, maxTY, minX, minY, size);

		// first look for points inside the triangle in increasing z-order
		Node p = ear.nextZ;

		while (p != null && p.z <= maxZ)
		{
			if (p != ear.prev && p != ear.next && pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0)
				return false;
			p = p.nextZ;
		}

		// then look for points in decreasing z-order
		p = ear.prevZ;

		while (p != null && p.z >= minZ)
		{
			if (p != ear.prev && p != ear.next && pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0)
				return false;
			p = p.prevZ;
		}

		return true;
	}

	private static double zOrder(double x, double y, double minX, double minY, double size)
	{
		// coords are transformed into non-negative 15-bit integer range
		int lx = new Double(32767 * (x - minX) / size).intValue();
		int ly = new Double(32767 * (y - minY) / size).intValue();

		lx = (lx | (lx << 8)) & 0x00FF00FF;
		lx = (lx | (lx << 4)) & 0x0F0F0F0F;
		lx = (lx | (lx << 2)) & 0x33333333;
		lx = (lx | (lx << 1)) & 0x55555555;

		y = (ly | (ly << 8)) & 0x00FF00FF;
		y = (ly | (ly << 4)) & 0x0F0F0F0F;
		y = (ly | (ly << 2)) & 0x33333333;
		y = (ly | (ly << 1)) & 0x55555555;

		return lx | (ly << 1);
	}

	private static void indexCurve(Node start, double minX, double minY, double size)
	{
		Node p = start;
		do
		{
			if (p.z == Double.MIN_VALUE)
				p.z = zOrder(p.x, p.y, minX, minY, size);
			p.prevZ = p.prev;
			p.nextZ = p.next;
			p = p.next;
		}
		while (p != start);

		p.prevZ.nextZ = null;
		p.prevZ = null;

		sortLinked(p);
	}

	private static Node sortLinked(Node list)
	{
		int inSize = 1;

		int numMerges;
		do
		{
			Node p = list;
			list = null;
			Node tail = null;
			numMerges = 0;

			while (p != null)
			{
				numMerges++;
				Node q = p;
				int pSize = 0;
				for (int i = 0; i < inSize; i++)
				{
					pSize++;
					q = q.nextZ;
					if (q == null)
						break;
				}

				int qSize = inSize;

				while (pSize > 0 || (qSize > 0 && q != null))
				{
					Node e;
					if (pSize == 0)
					{
						e = q;
						q = q.nextZ;
						qSize--;
					}
					else if (qSize == 0 || q == null)
					{
						e = p;
						p = p.nextZ;
						pSize--;
					}
					else if (p.z <= q.z)
					{
						e = p;
						p = p.nextZ;
						pSize--;
					}
					else
					{
						e = q;
						q = q.nextZ;
						qSize--;
					}

					if (tail != null)
						tail.nextZ = e;
					else
						list = e;

					e.prevZ = tail;
					tail = e;
				}

				p = q;
			}

			tail.nextZ = null;
			inSize *= 2;

		}
		while (numMerges > 1);

		return list;
	}

	private static Node eliminateHoles(double[] data, int[] holeIndices, Node outerNode, int dim)
	{
		List<Node> queue = new ArrayList<>();

		int len = holeIndices.length;
		for (int i = 0; i < len; i++)
		{
			int start = holeIndices[i] * dim;
			int end = i < len - 1 ? holeIndices[i + 1] * dim : data.length;
			Node list = linkedList(data, start, end, dim, false);
			if (list == list.next)
				list.steiner = true;
			queue.add(getLeftmost(list));
		}

		queue.sort(new Comparator<Node>()
		{

			@Override
			public int compare(Node o1, Node o2)
			{
				if (o1.x - o2.x > 0)
					return 1;
				else if (o1.x - o2.x < 0)
					return -2;
				return 0;
			}
		});

		for (Node node : queue)
		{
			eliminateHole(node, outerNode);
			outerNode = filterPoints(outerNode, outerNode.next);
		}

		return outerNode;
	}

	private static Node filterPoints(Node start, Node end)
	{
		if (start == null)
			return start;
		if (end == null)
			end = start;

		Node p = start;
		boolean again;

		do
		{
			again = false;

			if (!p.steiner && equals(p, p.next) || area(p.prev, p, p.next) == 0)
			{
				removeNode(p);
				p = end = p.prev;
				if (p == p.next)
					return null;
				again = true;
			}
			else
			{
				p = p.next;
			}
		}
		while (again || p != end);

		return end;
	}

	private static boolean equals(Node p1, Node p2)
	{
		return p1.x == p2.x && p1.y == p2.y;
	}

	private static double area(Node p, Node q, Node r)
	{
		return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
	}

	private static void eliminateHole(Node hole, Node outerNode)
	{
		outerNode = findHoleBridge(hole, outerNode);
		if (outerNode != null)
		{
			Node b = splitPolygon(outerNode, hole);
			filterPoints(b, b.next);
		}
	}

	private static Node splitPolygon(Node a, Node b)
	{
		Node a2 = new Node(a.i, a.x, a.y);
		Node b2 = new Node(b.i, b.x, b.y);
		Node an = a.next;
		Node bp = b.prev;

		a.next = b;
		b.prev = a;

		a2.next = an;
		an.prev = a2;

		b2.next = a2;
		a2.prev = b2;

		bp.next = b2;
		b2.prev = bp;

		return b2;
	}

	// David Eberly's algorithm for finding a bridge between hole and outer
	// polygon
	private static Node findHoleBridge(Node hole, Node outerNode)
	{
		Node p = outerNode;
		double hx = hole.x;
		double hy = hole.y;
		double qx = -Double.MAX_VALUE;
		Node m = null;

		// find a segment intersected by a ray from the hole's leftmost point to
		// the left;
		// segment's endpoint with lesser x will be potential connection point
		do
		{
			if (hy <= p.y && hy >= p.next.y)
			{
				double x = p.x + (hy - p.y) * (p.next.x - p.x) / (p.next.y - p.y);
				if (x <= hx && x > qx)
				{
					qx = x;
					if (x == hx)
					{
						if (hy == p.y)
							return p;
						if (hy == p.next.y)
							return p.next;
					}
					m = p.x < p.next.x ? p : p.next;
				}
			}
			p = p.next;
		}
		while (p != outerNode);

		if (m == null)
			return null;

		if (hx == qx)
			return m.prev; // hole touches outer segment; pick lower endpoint

		// look for points inside the triangle of hole point, segment
		// intersection and endpoint;
		// if there are no points found, we have a valid connection;
		// otherwise choose the point of the minimum angle with the ray as
		// connection point

		Node stop = m;
		double mx = m.x;
		double my = m.y;
		double tanMin = Double.MAX_VALUE;
		double tan;

		p = m.next;

		while (p != stop)
		{
			if (hx >= p.x && p.x >= mx && pointInTriangle(hy < my ? hx : qx, hy, mx, my, hy < my ? qx : hx, hy, p.x, p.y))
			{

				tan = Math.abs(hy - p.y) / (hx - p.x); // tangential

				if ((tan < tanMin || (tan == tanMin && p.x > m.x)) && locallyInside(p, hole))
				{
					m = p;
					tanMin = tan;
				}
			}

			p = p.next;
		}

		return m;
	}

	private static boolean locallyInside(Node a, Node b)
	{
		return area(a.prev, a, a.next) < 0 ? area(a, b, a.next) >= 0 && area(a, a.prev, b) >= 0 : area(a, b, a.prev) < 0 || area(a, a.next, b) < 0;
	}

	private static boolean pointInTriangle(double ax, double ay, double bx, double by, double cx, double cy, double px, double py)
	{
		return (cx - px) * (ay - py) - (ax - px) * (cy - py) >= 0 && (ax - px) * (by - py) - (bx - px) * (ay - py) >= 0 && (bx - px) * (cy - py) - (cx - px) * (by - py) >= 0;
	}

	private static Node getLeftmost(Node start)
	{
		Node p = start;
		Node leftmost = start;
		do
		{
			if (p.x < leftmost.x)
				leftmost = p;
			p = p.next;
		}
		while (p != start);
		return leftmost;
	}

	private static Node linkedList(double[] data, int start, int end, int dim, boolean clockwise)
	{
		Node last = null;
		if (clockwise == (signedArea(data, start, end, dim) > 0))
		{
			for (int i = start; i < end; i += dim)
			{
				last = insertNode(i, data[i], data[i + 1], last);
			}
		}
		else
		{
			for (int i = (end - dim); i >= start; i -= dim)
			{
				last = insertNode(i, data[i], data[i + 1], last);
			}
		}

		if (last != null && equals(last, last.next))
		{
			removeNode(last);
			last = last.next;
		}
		return last;
	}

	private static void removeNode(Node p)
	{
		p.next.prev = p.prev;
		p.prev.next = p.next;

		if (p.prevZ != null)
		{
			p.prevZ.nextZ = p.nextZ;
		}
		if (p.nextZ != null)
		{
			p.nextZ.prevZ = p.prevZ;
		}
	}

	private static Node insertNode(int i, double x, double y, Node last)
	{
		Node p = new Node(i, x, y);

		if (last == null)
		{
			p.prev = p;
			p.next = p;
		}
		else
		{
			p.next = last.next;
			p.prev = last;
			last.next.prev = p;
			last.next = p;
		}
		return p;
	}

	private static double signedArea(double[] data, int start, int end, int dim)
	{
		double sum = 0;
		int j = end - dim;
		for (int i = start; i < end; i += dim)
		{
			sum += (data[j] - data[i]) * (data[i + 1] + data[j + 1]);
			j = i;
		}
		return sum;
	}

	private static class Node
	{

		int i;
		double x;
		double y;
		double z;
		boolean steiner;

		Node prev;
		Node next;
		Node prevZ;
		Node nextZ;

		Node(int i, double x, double y)
		{
			// vertice index in coordinates array
			this.i = i;

			// vertex coordinates
			this.x = x;
			this.y = y;

			// previous and next vertice nodes in a polygon ring
			this.prev = null;
			this.next = null;

			// z-order curve value
			this.z = Double.MIN_VALUE;

			// previous and next nodes in z-order
			this.prevZ = null;
			this.nextZ = null;

			// indicates whether this is a steiner point
			this.steiner = false;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("{i: ").append(i).append(", x: ").append(x).append(", y: ").append(y).append(", prev: ").append(prev).append(", next: ").append(next);
			return sb.toString();
		}
	}
}