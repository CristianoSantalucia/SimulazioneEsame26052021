package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		
		dao.getBusinessCityYear(this.mappaB,citta, anno);
 
		// vertici
		Graphs.addAllVertices(this.grafo, this.mappaB.values());

		// archi
		this.dao.getRecensioni(this.recensioni, anno);
		System.out.println(this.recensioni.values().size());
		this.calcolaStars(this.mappaB.values());

		return this.mappaB.values();
	}

	private void calcolaStars(Collection<Business> business)
	{
		for (Business b1 : business)
		{
			for (Business b2 : business)
			{
				double avg1 = this.recensioni.get(b1.getBusinessId());
				double avg2 = this.recensioni.get(b2.getBusinessId());
				
				double dif = avg1 - avg2;
				System.out.println(dif);
			}
		}
	}
}
