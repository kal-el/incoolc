public interface CSymbolTable {

	/** 
	 * Sets the current scope as the parent scope.
	 * @throws Exception if the current scope is the root scope.
	 */
	public void exitScope() throws Exception;

	/** 
	 * Creates a new Scope object.
	 * @return a new Scope object, with the current one as parent.
	 */
	public Scope createScope();

	/**
	 * Defines a new scope, setting it as a child of the current
	 * scope.
	 */
	public void enterScope();

	/**
	 * Enters the scope s, setting it as a child of the current
	 * scope; if the new scope is a class scope (scopeLevel == 1)
	 * adds to the symbol table methods inherited from Object.
	 * @param s the scope in which we want to enter.
	 */
	public void enterScope(Scope s);

	/** 
	 * Adds a new id to the Symbol table; if it's an objectid, checks
	 * if it's already declared in the symbol table and, if not, adds
	 * it to the hanging list.
	 * @param s the id symbol
	 * @param data informations carried by the Symbol
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s, AbstractSymbol data,int lineno) throws Exception;

	/** 
	 * @return the current scope.
	 */
	public Scope getCurrentScope();

	/**
	 * This method returns the informations associated with Symbol s
	 * @param s the symbol 
	 * @return the object that wraps informations associated with s
	 */
	public ObjectInfo lookup(AbstractSymbol s);
	
	/**
	 * @return true if there are hanging id's.
	 */
	public boolean hangingIds();

}