package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph <ArtObject, DefaultWeightedEdge> grafo;
	//Per essere sicuri di creare con delle new i vertici una sola volta
	//salviamo gli oggetti della mappa e tutte le volte che ci servono andiamo a prenderli da lì
	//senza creare una new
	private Map <Integer, ArtObject> idMap;
	
	public Model() {
		idMap = new HashMap<>(); //La passo al DAO chiedendo al DAO di riempirla, se per il DAO contiene già
								//degli elementi ci restituisce la mappa stessa
	}
	
	//Creo il grafo qui dentro, in modo tale che se dovessi cambiare i dati di input, il grafo viene ogni volta
	//cancellato e ricreato da zero
	public void creaGrafo() {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		ArtsmiaDAO dao = new ArtsmiaDAO();
		
		dao.listObjects(idMap); //conterrà tutti gli oggetti presenti ella tabella object
		//Aggiungere i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungere gli archi ai vertici
		
		//APPROCCIO 1, più facile, non funziona spesso
		//Doppio ciclo for sui vertici, dati due vertici controllo se sono collegati
		/*for (ArtObject a1: this.grafo.vertexSet()) {
			for (ArtObject a2: this.grafo.vertexSet()) { //con il doppio ciclo for avremo anche le coppie invertire
				//devo collegare a1 con a2? 
				//controllo se non esiste già l'arco
				int peso = dao.getPeso(a1,a2);

				if (peso>0) {
					if (!this.grafo.containsEdge(a1, a2)) {
						Graphs.addEdge(this.grafo, a1, a2, peso);
					}
				}
			}
		}*/
		
		
		//APPROCCIO 2
		//Recupero un adiacente a ciascun arco, prendo tutti gli adiacenti ad un oggetto e conto quante volte
		//questi sono stati esposti insieme
		//Anche questo metodo ci metterebbe dei minuti per finire di girare su un database così grande
		
		//APPROCCIO 3
		//Ci facciamo dare dal db con un'unica query una serie di coppie oggetto 1 ed oggetto 2 
		//Imponendo che l'id dell'oggetto 1 sia sempre > dell'id dell'oggetto 2 avremo sempre le coppie
		//1 volta sola
		for (Adiacenza a: dao.getAdiacenze()) {
			if (a.getPeso()>0) {
				Graphs.addEdge(this.grafo, idMap.get(a.getObj1()), idMap.get(a.getObj2()), a.getPeso());
			}
		}
		
		
		System.out.println(String.format("Grafo creato! # vertici %d, # Archi %d",this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

}
