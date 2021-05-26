package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
	private Map<String, Double> recensioni; // b, r

	public Model()
	{
		this.dao = new YelpDao();
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
	}

	public List<String> getCitta()
	{
		return dao.getAllCitta();
	}

	public Collection<Business> getBusinessYearCity(String citta, int anno)
	{
		this.mappaB = new HashMap<>();
		this.recensioni = new HashMap<>();

		dao.getBusinessCityYear(this.mappaB, citta, anno);

		// vertici
		Graphs.addAllVertices(this.grafo, this.mappaB.values());

		// archi
		this.dao.getRecensioni(this.recensioni, anno);
//		System.out.println(this.recensioni.values().size());
		this.calcolaArchi(this.mappaB);
		System.out.println("vertici: " + this.grafo.vertexSet().size() + "\narchi: " + this.grafo.edgeSet().size());

		// locale migliore
		System.out.println("BEST LOCALE: " + this.calcolaMigliore());

		return this.mappaB.values();
	}

	private Business calcolaMigliore()
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
			if(sum > bestSum)
			{
				bestSum = sum;
				besta = b;
			}
		}
		return besta;
	}

	private void calcolaArchi(Map<String, Business> mappaB)
	{
		for (Business b1 : mappaB.values())
		{
			for (Business b2 : mappaB.values())
			{
				double avg1 = this.recensioni.get(b1.getBusinessId());
				double avg2 = this.recensioni.get(b2.getBusinessId());

				double dif = Math.abs(avg1 - avg2);
//				System.out.println(dif);

				// archi
				if (dif == 0) break;

				if (avg1 > avg2) Graphs.addEdgeWithVertices(this.grafo, mappaB.get(b1.getBusinessId()),
						mappaB.get(b2.getBusinessId()), dif);
				else if (avg1 < avg2) Graphs.addEdgeWithVertices(this.grafo, mappaB.get(b2.getBusinessId()),
						mappaB.get(b1.getBusinessId()), dif);
			}
		}
	}
}
