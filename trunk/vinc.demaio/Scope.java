import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** 
 *  This class models a Scope object. Every Scope has its own symbol table,
 *  represented as an HashMap.
 *  
 *  @author Vincenzo De Maio
 */
public class Scope {
	
	/** Class constructor, initializes map and sets the parent to null;
	 *  This Scope has no parent, so maybe represents the "root" scope 	
	 *  @return a new Scope object. 
	 */
	public Scope(){
		parent = null;
		level = 0;
		map = new HashMap<AbstractSymbol,ObjectInfo>();
		children = new ScopeList();
	}

	/** Class constructor, initializes map and sets the parent to the
	 * Scope passed as input.
	 * @param parent the parent Scope
	 * @return a new Scope object
	 */
	public Scope(Scope parent,int level){
		this.parent = parent;
		this.level = level;
		map = new HashMap<AbstractSymbol,ObjectInfo>();
		children = new ScopeList();
	}
	
	/** This method adds a Symbol to the Scope Symbol Table.
	 * @param s Symbol that we must add 
	 * 
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data){
		 if(inScope(s)) System.err.println("Redefining symbol "+s.toString());
		 map.put(s,new ObjectInfo(data));
	}
	
	/** This method adds a Symbol to the Scope Symbol Table.
	 * @param s Symbol that we must add 
	 * 
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,Object value){
		 if(inScope(s)) System.err.println("Redefining symbol "+s.toString()+" at line "+lineno);
		 else map.put(s,new ObjectInfo(data,lineno,value));
	}
	
	/** This method adds a Symbol to the Scope Symbol Table.
	 * @param s Symbol that we must add  
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,AbstractSymbol typename,AbstractSymbol classname){
		 if(inScope(s)) System.err.println("Redefining symbol "+s.toString()+"at line "+lineno);
		 map.put(s,new ObjectInfo(data,lineno,typename,classname));
	}
	
	/** This method adds a Symbol to the Scope Symbol Table.
	 * @param s Symbol that we must add  
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,AbstractSymbol rtntype,AbstractSymbol classname,Formals f){
		 if(inScope(s)) System.err.println("Redefining symbol "+s.toString()+"at line "+lineno);
		 map.put(s,new ObjectInfo(data,lineno,rtntype,classname,f));
	}
	public void addId(AbstractSymbol s, AbstractSymbol data, int lineno,
			AbstractSymbol typename, Object value) {
		map.put(s,new ObjectInfo(data,lineno,typename,value));		
	}
	/**
	 * Returns the scope in which the Symbol s is visible
	 * @param s the symbol 
	 * @return the scope in which symbol s is defined.
	 */
	
	private Scope isVisible(AbstractSymbol s){
		Scope temp = this;
		if(map.containsKey(s)) return temp;
		else if(this.getParent()!=null){
			temp = this.getParent();
			return temp.isVisible(s);
		}
		return null;
	}
	
	/**
	 * Predicate to see if s is in the current scope.
	 * @param s the symbol that we need to check
	 * @return true if s is in the current scope, false otherwise.
	 */
	
	public boolean inScope(AbstractSymbol s){
			return map.containsKey(s);		
	}
	
	/**
	 * Returns the value associated with symbol s
	 * @param s the symbol in which we're interested
	 * @return data associated with symbol s
	 */
	public ObjectInfo lookup(AbstractSymbol s){
			if(inScope(s)) return getData(s);
			Scope symbolScope = isVisible(s);
			if(symbolScope!=null)
				return symbolScope.getData(s);
			return null;
	}
	
	/**
	 * @param s symbol in which we're interested
	 * @return
	 */
	private ObjectInfo getData(AbstractSymbol s) {
		return map.get(s);		
	}

	/** This method sets the parent scope Of the current scope
	 *  @param s the parent scope
	 */
	public void setParent(Scope s){
		parent = s;		
	}
	
	/**
	 * This method returns the parent scope 
	 * @return the parent scope
	 */
	public Scope getParent(){
		return parent;		
	}
	
	/**
	 * This method adds a child scope to the current scope.
	 * @param s Scope that we must append as child of the current scope.
	 */
	public void addChild(Scope s){
		children.addScope(s);		
	}
	/**
	 * @return the children list of the current scope
	 */
	public ScopeList getChildList(){
		return children;
	}
	
	public int getLevel() {
		return level;
	}
	public int getChildNum(){
		return children.size();
	}
	public AbstractSymbol getClassname() {
		return classname;
	}

	public void setClassname(AbstractSymbol classname) {
		this.classname = classname;
	}
	public void setObjectInfo(AbstractSymbol s, ObjectInfo obj) {
		map.remove(s);
		map.put(s,obj);
	}
	public void dumpAll(){
		Set<AbstractSymbol> set = (Set<AbstractSymbol>)map.keySet();
		Iterator<AbstractSymbol> it = set.iterator();
		while(it.hasNext()){
			AbstractSymbol s = (AbstractSymbol)it.next();
			System.out.println(s.getString());
		}
		Iterator<Scope> childit = children.iterator();
		while(childit.hasNext()) childit.next().dumpAll();
	}
	/**
	 * 
	 */
	private Scope parent;
	private ScopeList children;
	private HashMap<AbstractSymbol,ObjectInfo> map;
	private int level;
	private AbstractSymbol classname;
	
	
}
