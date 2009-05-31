
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
	public ObjectInfo(AbstractSymbol data, int lineno,Object value) {
		this.data = data;
		this.lineno = lineno;
		this.value = value;
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
			this.data = data;
			this.lineno = lineno;
			this.rtnvalue = rtntype;
			this.classname = classname;
			this.parameters = f;
	}
	/**
	 * @return return value of the method.
	 */
	
	public ObjectInfo(class_c cl){
		this.classname = cl.name;
		this.features = cl.features;
		this.lineno = cl.lineNumber;
		this.parent = cl.parent;
	}
	
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
	
	public Object getValue(){
		return value;
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
	
	public Features getFeatures() {
		return features;
	}
	
	public AbstractSymbol getParent() {
		return parent;
	}
	
	private AbstractSymbol typename,rtnvalue,classname,data,parent;
	private Object value;
	private int lineno;
	private Formals parameters;
	private Features features;
	
}
