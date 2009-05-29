import java.util.Enumeration;
import java.util.LinkedList;

/**
 * This class implements the Symbol Table for the COOL
 * programming language.
 * @author Vincenzo De Maio
 */

public class CoolSymbolTable implements CSymbolTable  {
	/**
	 * Creates the Symbol table, defining a new scope
	 * that will be the top most in the scoping hierarchy,
	 * and installs the default methods from Object class.
	 * @throw Exception if some error occurs when adding methods.
	 */
	public CoolSymbolTable(){
		programScope = new Scope();
		scopeLevel = 0;
		hanging = new LinkedList<AbstractSymbol>();
		/* in the root scope we need to add class name
		 * of the builtin types Int,String and IO						 */
		AbstractSymbol IO,String,Int,Bool;
		IO = AbstractTable.idtable.addString("IO");
		String = AbstractTable.idtable.addString("String");
		Int = AbstractTable.idtable.addString("Int");
		Bool = AbstractTable.idtable.addString("Bool");
		programScope.addId(IO,TreeConstants._class);
		programScope.addId(String,TreeConstants._class);
		programScope.addId(Int,TreeConstants._class);
		programScope.addId(Bool,TreeConstants._class);
		currentScope = programScope;		
	}
	
	public CoolSymbolTable(ClassTable ctable){
		programScope = new Scope();
		scopeLevel = 0;
		hanging = new LinkedList<AbstractSymbol>();
		/* in the root scope we need to add class name
		 * of the builtin types Int,String and IO						 */
		AbstractSymbol IO,String,Int,Bool;
		IO = AbstractTable.idtable.addString("IO");
		String = AbstractTable.idtable.addString("String");
		Int = AbstractTable.idtable.addString("Int");
		Bool = AbstractTable.idtable.addString("Bool");
		programScope.addId(IO,TreeConstants._class);
		programScope.addId(String,TreeConstants._class);
		programScope.addId(Int,TreeConstants._class);
		programScope.addId(Bool,TreeConstants._class);
		currentScope = programScope;
		classtable = ctable;
	}
	
	public void installClasses(Classes list){
		classtable = new ClassTable(list);
	}
	
	/** 
	 * Sets the current scope as the parent scope.
	 * @throws Exception if the current scope is the root scope.
	 */
	public void exitScope() throws Exception{
		if(currentScope.getParent()!=null){
			currentScope = currentScope.getParent();
			scopeLevel--;
		}
		else throw new Exception("No more scope levels.");
		
	}
	/** 
	 * Creates a new Scope object.
	 * @return a new Scope object, with the current one as parent.
	 */
	public Scope createScope(){
		Scope s = new Scope(getCurrentScope(),scopeLevel+1);
		return s;
	}
	/**
	 * Defines a new scope, setting it as a child of the current
	 * scope.
	 */
	public void enterScope(){
		Scope s = new Scope(currentScope,scopeLevel+1);
		currentScope.addChild(s);
		currentScope = s;
		if(scopeLevel==0){ 
			/* then it's a class scope, so we need to
			 * add methods inherited from Object to its scope... 
			 */
			AbstractSymbol abort,type_name,copy;
			abort = AbstractTable.idtable.addString("abort");
			type_name = AbstractTable.idtable.addString("type_name");
			copy = AbstractTable.idtable.addString("copy");
			currentScope.addId(abort, TreeConstants.method,0,AbstractTable.idtable.addString("Object"),null);
			currentScope.addId(type_name, TreeConstants.method,0,AbstractTable.idtable.addString("String"),null);
			currentScope.addId(copy,TreeConstants.method,0,AbstractTable.idtable.addString("SELF_TYPE"),null);
		}
		scopeLevel++;
	}
	
	/**
	 * Enters the scope s, setting it as a child of the current
	 * scope.
	 * @param s the scope in which we want to enter.
	 */
	public void enterScope(Scope s){
		currentScope.addChild(s);
		currentScope = s;
		scopeLevel++;
	}
	
	/** 
	 * Adds a new id to the Symbol table.
	 * @param s the id symbol
	 * @param data constant that defines the symbol type (a TreeConstant)
	 * @param lineno the line number in the source program.
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,Object value)throws Exception{
		if(data.getString().equals(TreeConstants.objectID.getString())||data.getString().equals(TreeConstants.type_name.getString())){
			if(lookup(s)==null){
				hanging.add(s);
			}
		}
		else{
			currentScope.addId(s,data,lineno,value);
			while(hanging.contains(s)) {
				hanging.remove(s);
			}
		}
	}
	
	/** 
	 * Adds a new instance variable to the Symbol table.
	 * @param s the id symbol
	 * @param data informations carried by the Symbol
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,AbstractSymbol typename,AbstractSymbol classname)throws Exception{
		if(data.getString().equals(TreeConstants.objectID.getString())){
			if(lookup(s)==null){
				hanging.add(s);
			}
		}
		else{
			currentScope.addId(s,data,lineno,typename,classname);
			while(hanging.contains(s)) {
				hanging.remove(s);
			}
		}
	}
	
		
	/** 
	 * Adds a new id to the Symbol table.
	 * @param s the id symbol
	 * @param data informations carried by the Symbol
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,AbstractSymbol rtntype,AbstractSymbol classname,Formals f)throws Exception{
		if(data.getString().equals(TreeConstants.objectID.getString())){
			if(lookup(s,classname)==null){
				hanging.add(s);
			}
		}
		else{
			currentScope.addId(s,data,lineno,rtntype,classname,f);
			while(hanging.contains(s)) {
				hanging.remove(s);
			}
		}
	}
		
	public void addId(AbstractSymbol s, AbstractSymbol data, int lineno)
	throws Exception {
		if(data.getString().equals(TreeConstants.objectID.getString())){
			if(lookup(s)==null){
				hanging.add(s);
			}
		}
		else{
			currentScope.addId(s,data,lineno,null);
			while(hanging.contains(s)) {
				hanging.remove(s);
			}
		}
	}
	/** 
	 * @return the current scope.
	 */
	public Scope getCurrentScope(){
		return currentScope;
	}
	/**
	 * This method returns the informations associated with Symbol s
	 * @param s the symbol 
	 * @return the object that wraps informations associated with s
	 */
	public ObjectInfo lookup(AbstractSymbol s){
		ObjectInfo obj = currentScope.lookup(s);
		if(obj==null){
			 class_c cl = classtable.lookup(s);
			 if(cl!=null) obj = new ObjectInfo(cl);
			 return obj;
		}
		else return obj;
	}
	
	public ObjectInfo lookup(AbstractSymbol classname,AbstractSymbol methodname){
			ObjectInfo result = null;
			result = lookupMethod(classname,methodname);
			if(result==null){
				class_c cls = classtable.lookup(classname);
				if(!cls.parent.getString().equals(TreeConstants.No_class.getString()))lookup(cls.parent,methodname);
			}
			return result;
	}
	
	private ObjectInfo lookupMethod(AbstractSymbol classname,AbstractSymbol methodname){
		class_c cls = classtable.lookup(classname);
		Features f = cls.getFeatures();
		Enumeration e = f.getElements();
		method result = null;
		ObjectInfo obj=null;
		while(e.hasMoreElements()){
			Feature temp = (Feature)e.nextElement();
			if(temp.getNodeType().equals(TreeConstants.method)){
				method mtd = (method)temp;
				if(mtd.name.getString().equals(methodname)) result = mtd;
			}
		}
		//if result == null, the method that we need doesn't exists in this class
		if(result!=null) obj = new ObjectInfo(TreeConstants.method,result.lineNumber,result.getReturnType(),classname,result.formals); 
		return obj;
	}
	
	public boolean hangingIds(){
		return !hanging.isEmpty();
	}
	
	public void fill(programc p) throws Exception{
		Classes clss = p.classes;
		Enumeration e = clss.getElements();
		while(e.hasMoreElements()){
			class_c cl = (class_c)e.nextElement();
			addId(cl.name,TreeConstants._class,cl.lineNumber);
			enterScope();
			classWalk(cl);
			exitScope();
		}
		printAll();
	}
	
	private void classWalk(class_c cl) throws Exception{
		Features fts = cl.getFeatures();
		Enumeration e = fts.getElements();
		while(e.hasMoreElements()){
			Feature f = (Feature)e.nextElement();
			if(f.getNodeType().getString().equals(TreeConstants.attr.getString())){
				attr a = (attr)f;
				addId(a.name,TreeConstants.attr,a.lineNumber,a.type_decl,cl.name);
			}
			else{
				method mtd = (method)f;
				addId(mtd.name,TreeConstants.method,mtd.lineNumber,mtd.return_type,cl.name,mtd.formals);
				enterScope();
				methodWalk(mtd);
				exitScope();
			}
		}
	}
	
	private void methodWalk(method mtd)throws Exception {
		Formals f = mtd.getFormals();
		Enumeration e = f.getElements();
		while(e.hasMoreElements()){
			formalc par = (formalc)e.nextElement();
			addId(par.name,TreeConstants.formalc,par.lineNumber,null);
		}
		Expression body = mtd.expr;
		expressionWalk(body);
	}

	
	private void expressionWalk(Expression body) {
				
	}

	public ClassTable getClassTable(){
		return classtable;
	}
	
	//THIS SHOULD BE USED ONLY FOR DEBUG PURPOSE.
	private void printAll(){
		programScope.dumpAll();
	}
	private Scope programScope,currentScope;
	private int scopeLevel;
	private LinkedList<AbstractSymbol> hanging;
	private ClassTable classtable;
		
}
