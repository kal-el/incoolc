import graph.ILDirectedGraph;

import iteratori.VertexIteratorAdapter;

import java.util.Enumeration;

import queue.ArrayQueue;

import vector.ArrayVector;

import decorator.ILVertexDecorator;

import node.ILVertex;

/**
 * @author Enzo
 *
 * @Time 19:36:39
 * 
 * @Date 24/mag/09
 */

public class InheritsGraphClasses{

	private Enumeration<Class_> e;
	private ILDirectedGraph inheritGraph;
	private Classes classes;
	//private ClassTable clt;
	private int numLinea;
	
	
	/* La variabile verticesKey viene usata per numerare i vertici del grafo di inheritance, 
	 * dato che, dopo aver costruito il grafo di inheritance, usiamo l'algoritmo di buona 
	 * numerazione( implementato nel metodo IsWellFormedGraph ) per rilevare se il grafo è aciclico.
     * In particolare, essa viene usata dal  metodo insertVertexInGraph() che appunto inserisce 
     * il vertici nel grafo e quindi prima di essere inseriti vengono numerati attraverso verticesKey.
     */
	
	int verticesKey = -1;
	
	/* questi abstract Symbol sono stati usati nel metodo checkInheritBasicClass, 
	 * che sono serviti per fare alcuni controlli sulle restrizioni in relazione 
	 * all'inheritance per quanto riguarda le classi di base.(per maggiori informazioni sulle 
	 * restrizioni leggi il cool-manual).
	 */
	AbstractSymbol IO = TreeConstants.IO;
	AbstractSymbol Bool = TreeConstants.Bool;
	AbstractSymbol Int = TreeConstants.Int;
	AbstractSymbol Obj = TreeConstants.Object_;
	AbstractSymbol Str = TreeConstants.Str;
	
	
	/* 
	 * Costruttore, qui vengono inizializzate alcune variabili di istanza  utili come ClassTable e 
	 * inheritsGraph ed inoltre viene costruito il grafo di inheritance mediante la chiamata
	 * alla funzione builtInheritanceGraph(e) usando un grafo direzionato( inheritsGraph )ed 
	 * infine controlliamo se è ben formato attraverso la chiamata a funzione isWellFormedGraph().
	 * 
	 */
	public InheritsGraphClasses(Classes cls) throws RuntimeException {
	
		this.classes = cls;
		//clt = new ClassTable(classes);
		inheritGraph = new ILDirectedGraph();
		e = classes.getElements();
		builtInheritanceGraph(e);
	    if(!isWellFormedGraph()){
	    	System.err.println("error: bad inherits class at line "+ numLinea +", sorry check the inheritance class of your program, please!!");
	    	System.exit(1);
	    }	
	}
	
	
	public AbstractSymbol getLub(AbstractSymbol type1, AbstractSymbol type2){
		ILVertexDecorator ver1 = null;
		ILVertexDecorator ver2 = null;
		ILVertexDecorator appoggio;
		AbstractSymbol temp;
		
		VertexIteratorAdapter via = inheritGraph.vertices();
		while(via.hasNext()){
			appoggio = (ILVertexDecorator) via.nextVertex();
			temp = (AbstractSymbol) appoggio.element();
			if(temp.equals(type1))
				ver1 = appoggio;
			if(temp.equals(type2))
				ver2 = appoggio;
		}
		if(type1.equals(type2))
			return type1;
		else
			temp = searchCommonAncestor(ver1, ver2);
		return temp; 
	}
	
	
	private AbstractSymbol searchCommonAncestor(ILVertexDecorator ver1, ILVertexDecorator ver2) {

		ILVertexDecorator predecessor_2 = (ILVertexDecorator) ver2.getPredecessore();
		AbstractSymbol symbol_ver1 = (AbstractSymbol) ver1.element();
		AbstractSymbol symbol_predecessor_2 = (AbstractSymbol) predecessor_2.element();
		if(this.inheritsFrom(symbol_ver1, symbol_predecessor_2)){
			return symbol_predecessor_2;
		}
		else{
			return searchCommonAncestor(ver1, predecessor_2); 
		}
	}

	
	
	public boolean inheritsFrom(AbstractSymbol type1, AbstractSymbol type2){
		
		VertexIteratorAdapter via = inheritGraph.vertices();
		ILVertexDecorator ver = null;
		ILVertexDecorator predecessor = null;
		AbstractSymbol temp;
		
		while(via.hasNext()){
			ILVertexDecorator temp_vertice = (ILVertexDecorator) via.nextVertex();
			temp = (AbstractSymbol) temp_vertice.element();
			if(temp.equals(type1)){
				ver  = temp_vertice;
			}
		}
		predecessor = (ILVertexDecorator) ver.getPredecessore();
		while(predecessor != null ){
			temp = (AbstractSymbol) predecessor.element();
			if(temp.equals(type2))
				return true;
			predecessor = (ILVertexDecorator) predecessor.getPredecessore();
		}
		return false;
	}
	
	
	public ILDirectedGraph getInheritanceGraph(){
		return inheritGraph;
	}
	
    private void builtInheritanceGraph(Enumeration<Class_> e) throws RuntimeException{

    	while(e.hasMoreElements()){
        class_c cl  = (class_c) e.nextElement();
        numLinea = cl.getLineNumber();
        AbstractSymbol cl_Name = cl.getName();
        AbstractSymbol parent = cl.getParent();
        checkInheritBasicClass(cl_Name, parent);
		ILVertexDecorator ver1 = ContainGraphVertex(inheritGraph.vertices(), cl_Name);	
	    ILVertexDecorator ver2 = ContainGraphVertex(inheritGraph.vertices(), parent);
	    insertVertexInGraph(ver1, ver2, cl_Name, parent);	
        
        }
	}



	private void checkInheritBasicClass(AbstractSymbol cl_Name, AbstractSymbol parent) throws RuntimeException{
		
		 if(parent.equals(cl_Name)){
			 System.err.println("error: bad inherits class at line "+ numLinea +", sorry check the inheritance class of your program, please!!");
			 System.exit(1);
	     }
		 if(parent.equals(Str) || parent.equals(Int) || parent.equals(Bool)){
			 System.err.println("error: bad inherits class at line "+ numLinea +", sorry check the inheritance class of your program, please!!");
			 System.exit(1);
		 }
		 if(cl_Name.equals(Str) || cl_Name.equals(Int) || cl_Name.equals(Bool) || cl_Name.equals(IO) || cl_Name.equals(Obj)){
			 System.err.println("error: bad inherits class at line "+ numLinea +", sorry check the inheritance class of your program, please!!");
			 System.exit(1);
		 }
		 
		 if(parent.equals(IO)){
			 AbstractSymbol ancestor = Obj;
			 ILVertexDecorator ver1 = ContainGraphVertex(inheritGraph.vertices(), parent);	
			 ILVertexDecorator ver2 = ContainGraphVertex(inheritGraph.vertices(), ancestor);
			 insertVertexInGraph(ver1, ver2, parent, ancestor);	
		 }	
	}


	/*
     * Inseriamo vertici nel grafo di inheritance dopo aver controllato con il metodo 
     * ContainGraphVertex che non già siano stati inseriti e quindi evitare che si
     *  creano vertici doppioni ovvero che contengono la stessa definizione di Classe come elemento 
     * La variabile intera verticesKey è utilizzata per numerare i vertici del grafo.
     */    
	private void insertVertexInGraph(ILVertexDecorator ver1, ILVertexDecorator ver2, AbstractSymbol cl_Name, AbstractSymbol parent) {
			
    	if((ver1 != null) && (ver2 != null)){
    		ver1.setPredecessore(ver2);
    		inheritGraph.insertEdge(ver1, ver2, new Integer(0));
    	}
    	else{
    		if(ver1 != null){
    			ver2 = (ILVertexDecorator) inheritGraph.insertVertex(parent);
    			verticesKey = verticesKey + 1;
    			ver2.setKey(new Integer(verticesKey));
    			ver1.setPredecessore(ver2);
    			inheritGraph.insertEdge(ver1, ver2, new Integer(0));
    		}
    		else{
    			if(ver2 != null){
    				ver1 = (ILVertexDecorator) inheritGraph.insertVertex(cl_Name);
    				verticesKey = verticesKey + 1;
    				ver1.setKey(new Integer(verticesKey));
    				ver1.setPredecessore(ver2);
        			inheritGraph.insertEdge(ver1, ver2, new Integer(0));
    			}
    			else{
    	    		ver1 = (ILVertexDecorator) inheritGraph.insertVertex(cl_Name);
    	    		verticesKey = verticesKey + 1;
    	    		ver1.setKey(new Integer(verticesKey));
    	    		ver2 = (ILVertexDecorator) inheritGraph.insertVertex(parent);
    	    		verticesKey = verticesKey + 1;
    	    		ver2.setKey(new Integer(verticesKey));
    	    		ver1.setPredecessore(ver2);
    	    		inheritGraph.insertEdge(ver1, ver2, new Integer(0));
    			}
    			
    		}
    	}
		
	}
	
	/*
	 * Controlliamo che non ci siano già dei vertici nel grafo che contengono l'elemento cl_Name e quindi evitare di avere 
	 * piu vertici vertici che hanno come elemento lo stesso elemento cl_Name, altrimenti ci troviamo con piu vertici
	 * nel grafo che hanno lo stesso elemento.  
	 */
	private ILVertexDecorator ContainGraphVertex(VertexIteratorAdapter vertices, AbstractSymbol cl_Name) {
	
		while(vertices.hasNext()){
			ILVertexDecorator ver = (ILVertexDecorator) vertices.nextVertex();
			AbstractSymbol temp = (AbstractSymbol) ver.element();
			if(temp.equals(cl_Name))
				return ver;
		}
		return null;
		
	}



	/*Check if inheritance graph is well-formed, that is, inheritance graph is acyclic*/
	private boolean isWellFormedGraph() {
    	
		ArrayVector etichetta = new ArrayVector();
		ArrayVector contatore = new ArrayVector();
		ArrayQueue coda = new ArrayQueue(); 
		int i;
		int k = 0;
		VertexIteratorAdapter vertices = inheritGraph.vertices();
		while(vertices.hasNext()){		
			ILVertexDecorator temp =  (ILVertexDecorator) vertices.nextVertex();
			i = ((Integer)temp.getKey()).intValue();
			etichetta.insertAtRank(i, new Integer(0));
			contatore.insertAtRank(i, new Integer(temp.getInDegree()));
			if(temp.getInDegree() == 0 )
				coda.enqueue(temp);
		}
		
		int j, cont;
		while(coda.size() != 0){
			ILVertexDecorator ver = (ILVertexDecorator) coda.dequeue();
			k = k+1;
			j = ((Integer)ver.getKey()).intValue();
			etichetta.replaceAtRank(j, new Integer(k));
			VertexIteratorAdapter adjVertices = (VertexIteratorAdapter) inheritGraph.outAdjacentVertices(ver);
			while(adjVertices.hasNext()){
				ILVertexDecorator ver2 = (ILVertexDecorator) adjVertices.nextVertex();
				j = ((Integer)ver2.getKey()).intValue();
				cont = ((Integer)contatore.elementAtRank(j)).intValue();
				contatore.replaceAtRank(j, new Integer(cont-1));
				if((cont-1) == 0 )
					coda.enqueue(ver2);
			}
		}
		if(k==inheritGraph.numVertices())
			return true;
		else
			return false;	
		
	}	
}


	

