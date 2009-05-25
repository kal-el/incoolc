/**
 * This class implements the Symbol Table for the COOL
 * programming language.
 * @author Vincenzo De Maio
 */

public class CoolSymbolTable  {
	/**
	 * Creates the Symbol table, defining a new scope
	 * that will be the top most in the scoping hierarchy.
	 */
	public CoolSymbolTable(){
		rootScope = new Scope();
		currentScope = rootScope;
	}
	/** 
	 * Sets the current scope as the parent scope.
	 * @throws Exception if the current scope is the root scope.
	 */
	public void exitScope() throws Exception{
		if(currentScope.getParent()!=null) currentScope = currentScope.getParent();
		else throw new Exception("No more scope levels.");
		
	}
	/** 
	 * Creates a new Scope object.
	 * @return a new Scope object, with the current one as parent.
	 */
	public Scope createScope(){
		Scope s = new Scope(getCurrentScope());
		return s;
	}
	/**
	 * Defines a new scope, setting it as a child of the current
	 * scope.
	 */
	public void enterScope(){
		Scope s = new Scope(currentScope);
		currentScope.addChild(s);
		currentScope = s;
	}
	
	/**
	 * Enters the scope s, setting it as a child of the current
	 * scope.
	 * @param s the scope in which we want to enter.
	 */
	public void enterScope(Scope s){
		currentScope.addChild(s);
		currentScope = s;
	}
	
	/** 
	 * Adds a new id to the Symbol table.
	 * @param s the id symbol
	 * @param data informations carried by the Symbol
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s,Object data)throws Exception{
		currentScope.addId(s,data); 
	}
	/** 
	 * @return the current scope.
	 */
	public Scope getCurrentScope(){
		return currentScope;
	}
	
	public Object lookup(AbstractSymbol s){
		return currentScope.lookup(s);
	}
	
	private Scope rootScope,currentScope;
}
