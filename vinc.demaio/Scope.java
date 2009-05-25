import java.util.HashMap;

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
		map = new HashMap<AbstractSymbol,Object>();
		children = new ScopeList();
	}

	/** Class constructor, initializes map and sets the parent to the
	 * Scope passed as input.
	 * @param parent the parent Scope
	 * @return a new Scope object
	 */
	public Scope(Scope parent){
		this.parent = parent;
		map = new HashMap<AbstractSymbol,Object>();
		children = new ScopeList();
	}
	
	/** This method adds a Symbol to the Scope Symbol Table.
	 * @param s Symbol that we must add 
	 * @return the object index in the hash map
	 * 		   0 if Object is already present
	 */
	public void addId(AbstractSymbol s,Object data) throws Exception{
		if(inScope(s)) throw new Exception("Symbol "+s.toString()+" already defined"); // redefining symbol...
		map.put(s,data);
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
	public Object lookup(AbstractSymbol s){
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
	private Object getData(AbstractSymbol s) {
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
	
	private Scope parent;
	private ScopeList children;
	private HashMap<AbstractSymbol,Object> map;
}
