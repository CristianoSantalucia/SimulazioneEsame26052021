package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model
{
	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private Map<String, Business> mappaB;
	private Map<String, Double> recensioni;

	public Model()
	{
		this.dao = new YelpDao();
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

	}

	public List<String> getCitta()
	{
		ArrayList<String> citta = new ArrayList<>(this.dao.getAllCitta());
		citta.sort((c1, c2) -> c1.compareTo(c2));
		return citta;
	}

	public String creaGrafo(String citta, int anno)
	{
		this.mappaB = new HashMap<>();
		this.recensioni = new HashMap<>();
		this.mappaB.clear();

		this.dao.getBusinessCityYear(this.mappaB, citta, anno);
		this.dao.getRecensioni(this.recensioni, citta, anno);

		// vertici
		Graphs.addAllVertices(this.grafo, this.mappaB.values());

		// archi
		this.calcolaArchi();

		return "Vertici: " + this.grafo.vertexSet().size() + "\nArchi: " + this.grafo.edgeSet().size();
	}

	private void calcolaArchi()
	{
		for (Business b1 : this.grafo.vertexSet())
		{
			for (Business b2 : this.grafo.vertexSet())
			{
				if (!b1.equals(b2))
				{
					Double avg1 = this.recensioni.get(b1.getBusinessId());
					Double avg2 = this.recensioni.get(b2.getBusinessId());
					if (avg1 != null && avg2 != null)
					{
						Double dif = Math.abs(avg1 - avg2); 
						
						if (avg1 > avg2)
						{
							Graphs.addEdgeWithVertices(this.grafo, mappaB.get(b2.getBusinessId()),
									mappaB.get(b1.getBusinessId()), dif);
//							System.out.println("diff: " + b1.getBusinessName() + " " + b2.getBusinessName() + ": " + dif
//									+ "EDGE: " + this.grafo.getEdge(b2, b1));
						}
						else if (avg1 < avg2)
						{
							Graphs.addEdgeWithVertices(this.grafo, mappaB.get(b1.getBusinessId()),
									mappaB.get(b2.getBusinessId()), dif);
//							System.out.println("diff: " + b1.getBusinessName() + " " + b2.getBusinessName() + ": " + dif
//									+ "EDGE: " + this.grafo.getEdge(b1, b2));
						}
					}
				}
			}
		}
	}

	public Collection<Business> getBusiness()
	{
		return this.grafo.vertexSet();
	}

	public Business calcolaMigliore()
	{
		Business besta = null;
		double bestSum = 0;
		for (Business b : this.grafo.vertexSet())
		{
			double in = 0;
			double out = 0;

			Set<DefaultWeightedEdge> outgoing = this.grafo.outgoingEdgesOf(b);
			for (DefaultWeightedEdge de : outgoing)
			{
				out += this.grafo.getEdgeWeight(de);
			}
			Set<DefaultWeightedEdge> incoming = this.grafo.incomingEdgesOf(b);
			for (DefaultWeightedEdge de : incoming)
			{
				in += this.grafo.getEdgeWeight(de);
			}

			double sum = in - out;
			if (sum > bestSum)
			{
				bestSum = sum;
				besta = b;
			}
		}
		return besta;
	}
}
