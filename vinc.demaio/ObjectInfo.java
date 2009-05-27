
/**
 * @author Vincenzo De Maio
 * Class that wraps useful infos about the Symbol.
 */
public class ObjectInfo {

	/**
	 * @param data constant from TreeConstants that identifies the Symbol class.
	 */
	public ObjectInfo(AbstractSymbol data) {
		this.data = data;
	}
	/**
	 * @param data constant from TreeConstants that identifies the Symbol class.
	 * @param lineno the line number
	 */
	public ObjectInfo(AbstractSymbol data, int lineno) {
		this.data = data;
		this.lineno = lineno;
	}
	/**
	 * @param data constant from TreeConstants that identifies the Symbol class.
	 * @param lineno the line number
	 * @param typename type of Symbol
	 * @param classname class that contains the symbol
	 */
	public ObjectInfo(AbstractSymbol data, int lineno, AbstractSymbol typename,
			AbstractSymbol classname) {
		this.data = data;
		this.lineno = lineno;
		this.typename = typename;
		this.classname = classname;
	}
	/**
	 * 
	 * @param data constant from TreeConstants that identifies the Symbol class.
	 * @param lineno the line number
	 * @param rtntype return value of method
	 * @param classname class that contains this method
	 * @param f formal parameters of method
	 */
	public ObjectInfo(AbstractSymbol data, int lineno, AbstractSymbol rtntype,
			AbstractSymbol classname, Formals f) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return return value of the method.
	 */
	 
	public AbstractSymbol getReturnValue() {
		return rtnvalue;
	}
	/**
	 * @return name of symbol type
	 */
	public AbstractSymbol getTypename() {
		return typename;
	}
	/**
	 * @param typename the type of the symbol.
	 */
	public void setTypename(AbstractSymbol typename) {
		this.typename = typename;
	}
	/**
	 * @return the data constant from TreeConstant that identifies the symbol class.
	 */
	public AbstractSymbol getData() {
		return data;
	}
	/**
	 * @param data constant from TreeConstants that identifies the symbol class.
	 */
	public void setData(AbstractSymbol data) {
		this.data = data;
	}
	/**
	 * @return the line number
	 */
	public int getLineno() {
		return lineno;
	}
	/**
	 * @return formal parameters of the method.
	 */
	public Formals getParameters() {
		return parameters;
	}
	/**
	 * @return class name where Symbol is situated.
	 */
	public AbstractSymbol getClassname() {
		return classname;
	}
	
	private AbstractSymbol typename,rtnvalue,classname,data;
	private int lineno;
	private Formals parameters;
}
