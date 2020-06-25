// https://en.wikipedia.org/wiki/Disjoint-set_data_structure
// Union by size with path compression

package util.alternateImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DisjointSet<E>
{
	private Map<E, DisjointSetNode>	nodeMap		= new HashMap<>();
	private Set<DisjointSetNode>    rootNodes	= new HashSet<>();
	boolean isCompressPath = false;

	public DisjointSet()
	{
		nodeMap = new HashMap<>();
		rootNodes = new HashSet<>();
	}

	public DisjointSetNode makeSet(E o)
	{
		DisjointSetNode node = nodeMap.get(o);
		if(node==null)
		{
			node = new DisjointSetNode();
			nodeMap.put(o, node);
			rootNodes.add(node);
		}
		return node;
	}

	public DisjointSetNode find(DisjointSetNode node)
	{
		if (node != node.getParent())
		{
			DisjointSetNode parentNode = find(node.getParent());
			if(isCompressPath) {
				node.setParent(parentNode);
			}
			return parentNode;
		}
		return node;
	}

	public DisjointSetNode union(DisjointSetNode x, DisjointSetNode y)
	{
		x = find(x);
		y = find(y);

		if (x == y)
		{
			return x;
		}

		if (x.getSize() >= y.getSize())
		{
			y.setParent(x);
			x.setSize(x.getSize() + y.getSize());
			rootNodes.remove(y);
			return x;
		}
		else
		{
			x.setParent(y);
			y.setSize(y.getSize()+x.getSize());
			rootNodes.remove(x);
			return y;
		}
	}

	public DisjointSetNode getDisjointSetNode(E o)
	{
		return nodeMap.get(o);
	}
	
	public Map<E,DisjointSetNode> getNodeMap()
	{
		return Collections.unmodifiableMap(nodeMap);
	}
	
	public Set<DisjointSetNode> getRootNodes(){
		return Collections.unmodifiableSet(rootNodes);
	}
	
	public void doPathCompression(boolean b) {
		isCompressPath=b;
	}

	public static class DisjointSetNode
	{
		private DisjointSetNode	parent;
		private int				size;

		public DisjointSetNode()
		{
			this.parent = this;
			this.size = 1;
		}

		public DisjointSetNode getParent()
		{
			return parent;
		}

		public void setParent(DisjointSetNode parent)
		{
			this.parent = parent;
		}

		public int getSize()
		{
			return size;
		}

		public void setSize(int rank)
		{
			this.size = rank;
		}
		
		public String toString() {
			return super.toString()+" size:"+size;
		}
	}
	
	public static void main(String[] args)
	{
		DisjointSet<Integer> ds = new DisjointSet<>();
		try
		{
			ds.makeSet(1);
			ds.makeSet(2);
			ds.makeSet(3);
			ds.makeSet(4);
			ds.makeSet(5);
			ds.union(ds.getDisjointSetNode(1), ds.getDisjointSetNode(2));
			ds.union(ds.getDisjointSetNode(4), ds.getDisjointSetNode(5));
			System.out.println(ds.getRootNodes());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
