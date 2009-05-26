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
	 * scope.
	 * @param s the scope in which we want to enter.
	 */
	public void enterScope(Scope s);

	/** 
	 * Adds a new id to the Symbol table.
	 * @param s the id symbol
	 * @param data informations carried by the Symbol
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s, Object data) throws Exception;

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

}