package it.polito.tdp.crimes.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> percorsoMigliore;
		
	public Model() {
		dao = new EventsDao();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	public void creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		
		for(Adiacenza a: dao.getAdiacenze(categoria, mese)) {
			if(grafo.getEdge(a.getV1(), a.getV2()) == null) {
				Graphs.addEdge(grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		
		System.out.println(grafo.vertexSet().size()+"	"+grafo.edgeSet().size());
	
	}
	
	public List<Adiacenza> getArchi(){
		double pesoMedio = 0.0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			pesoMedio += grafo.getEdgeWeight(e);
		}
		
		pesoMedio = pesoMedio/grafo.edgeSet().size();
		
		List<Adiacenza> result = new LinkedList<>();
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e) > pesoMedio)
				result.add(new Adiacenza(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
		}
		
		return result;
	}
	
	public List<String> trovaPercorso(String sorgente, String destinazione){
		this.percorsoMigliore = new LinkedList<>();
		List<String> parziale = new LinkedList<>();
		parziale.add(sorgente);
		cerca(destinazione, parziale);
		return percorsoMigliore;
	}
	
	private void cerca(String destinazione, List<String> parziale) {
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size()>percorsoMigliore.size()) {
				percorsoMigliore = new LinkedList<>(parziale);
			}
			
			return;
		}
		
		for(String vicino: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(destinazione, parziale);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
}
