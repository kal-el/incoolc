import java.util.Enumeration;
import java.util.Iterator;
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
		hanging = new LinkedList<object>();
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
		hanging = new LinkedList<object>();
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
	
	public void installClasses(Classes list)throws Exception{
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
			AbstractSymbol abort,type_name,copy,self;
			abort = AbstractTable.idtable.addString("abort");
			type_name = AbstractTable.idtable.addString("type_name");
			copy = AbstractTable.idtable.addString("copy");
			self = AbstractTable.idtable.addString("self");
			currentScope.addId(abort, TreeConstants.method,0,AbstractTable.idtable.addString("Object"),null);
			currentScope.addId(type_name, TreeConstants.method,0,AbstractTable.idtable.addString("String"),null);
			currentScope.addId(copy,TreeConstants.method,0,AbstractTable.idtable.addString("SELF_TYPE"),null);
			currentScope.addId(self,TreeConstants.type_name,0,this.self.name,null);
			currentScope.setClassname(getSelf().name);
		}
		scopeLevel++;
	}
	
	/**
	 * Enters the scope s, setting it as a child of the current
	 * scope.
	 * @param s the scope in which we want to enter.
	 */
	public void enterScope(Scope s){
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
		currentScope.addId(s,data,lineno,value);
		while(hanging.contains(s)) {
				hanging.remove(s);
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
		currentScope.addId(s,data,lineno,typename,classname);
		while(hanging.contains(s)) {
			hanging.remove(s);
		}
	}
	
		
	/** 
	 * Adds a new method id to the Symbol table.
	 * @param s the id symbol
	 * @param data informations carried by the Symbol
	 * @throws Exception if Symbol is already defined in the Symbol
	 * table.
	 */
	public void addId(AbstractSymbol s,AbstractSymbol data,int lineno,AbstractSymbol rtntype,AbstractSymbol classname,Formals f)throws Exception{
		currentScope.addId(s,data,lineno,rtntype,classname,f);
		while(hanging.contains(s)) {
			hanging.remove(s);
		}
	}
		
	public void addId(AbstractSymbol s, AbstractSymbol data, int lineno)
	throws Exception {
		currentScope.addId(s,data,lineno,null);
		while(hanging.contains(s)) {
			hanging.remove(s);
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
		return obj;
	}
	
	public ObjectInfo lookup(AbstractSymbol classname,AbstractSymbol featname,AbstractSymbol feattype){
			ObjectInfo result = null;
			if(feattype.getString().equals(TreeConstants.method.getString()))
				result = lookupMethod(classname,featname);
			else
				result = lookupAttr(classname,featname);
			if(result==null){
				class_c cls = classtable.lookup(classname);
				if(cls!=null &&  !(cls.parent.getString().equals(TreeConstants.No_class.getString())))
					lookup(cls.parent,featname,feattype);
			}
			return result;
	}
	
	private ObjectInfo lookupAttr(AbstractSymbol classname,
			AbstractSymbol featname) {
		class_c cls = classtable.lookup(classname);
		attr result = null;
		ObjectInfo obj=null;
		if(cls!=null){
			Features f = cls.getFeatures();
			if(f!=null){
				Enumeration e = f.getElements();
				while(e.hasMoreElements()){
					Feature temp = (Feature)e.nextElement();
						if(temp.getNodeType().getString().equals(TreeConstants.attr.getString())){
							attr att = (attr)temp;
							if(att.name.getString().equals(featname.getString())) result = att;
						}
				}
			}
		}
		//if result == null, the method that we need doesn't exists in this class
		if(result!=null) obj = new ObjectInfo(TreeConstants.attr,result.lineNumber,result.type_decl,cls.name);
		return obj;
	}

	private ObjectInfo lookupMethod(AbstractSymbol classname,AbstractSymbol featurename){
		class_c cls = classtable.lookup(classname);
		method result = null;
		ObjectInfo obj = null;
		if(cls!=null){
		Features f = cls.getFeatures();
		if(f!=null){
		Enumeration e = f.getElements();
		while(e.hasMoreElements()){
			Feature temp = (Feature)e.nextElement();
			if(temp.getNodeType().getString().equals(TreeConstants.method.getString())){
				method mtd = (method)temp;
				if(mtd.name.getString().equals(featurename.getString())) result = mtd;
			}
			
		}
		}
		}
		//if result == null, the method that we need doesn't exists in this class
		if(result!=null) obj = new ObjectInfo(TreeConstants.method,result.lineNumber,result.getReturnType(),classname,result.formals); 
		return obj;
	}
	
	public void hangingIds(){
		if(!hanging.isEmpty()){
			Iterator<object> it = hanging.iterator();
			while(it.hasNext()){
				object obj = (object)it.next();
				System.err.println("ERROR: Undeclared "+obj.name.getString()+" at line "+obj.lineNumber);
			}
		}
		if(!ismain) System.err.println("ERROR: Could not find Main method.");
	}
	
	public class_c getSelf() {
		return self;
	}
	
	public Scope getClassScope(AbstractSymbol name){
		ScopeList children = programScope.getChildList();
		Iterator<Scope> it = children.iterator();
		Scope s=null;
		while(it.hasNext()){
			Scope temp = (Scope)it.next();
			if(temp.getClassname().getString().equals(name.getString())){
				s = temp;
				break;
			}
		}
		return s;
	}
	
	public void fill(programc p) throws Exception{
		Classes clss = p.classes;
		Enumeration e = clss.getElements();
		while(e.hasMoreElements()){
			class_c cl = (class_c)e.nextElement();
			self = cl;
			addId(cl.name,TreeConstants._class,cl.lineNumber);
			enterScope();
			classWalk(cl);
			exitScope();
		}
		//printAll(); decomment for debug
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
				//here we check if there's a main method
				if(!ismain) ismain = mtd.name.getString().equalsIgnoreCase("main");
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
		if(body.getNodeType().getString().equals(TreeConstants.block.getString())){
			enterScope();
			blockWalk(body);
			exitScope();
		}
		else expressionWalk(body);
	}

	
	private void blockWalk(Expression body)throws Exception {
		block bl = (block)body;
		Expressions list = bl.body;
		Enumeration e = list.getElements();
		while(e.hasMoreElements()){
			Expression expr = (Expression)e.nextElement();
			expressionWalk(expr);
		}
	}

	private void expressionWalk(Expression body)throws Exception {
		String strtype = body.getNodeType().getString();
		if(strtype.equals(TreeConstants.assign.getString())){
			assign ex = (assign)body;
			if(lookup(ex.name)==null){
				//maybe it's an instance variable from one of his ancestors...
				if(lookup(self.parent,ex.name,TreeConstants.attr)==null)
					hanging.add(new object(ex.lineNumber,ex.name));
			}
			expressionWalk(ex.expr);
		}
		else if(strtype.equals(TreeConstants.block.getString())){
			blockWalk(body);
		}
		else if(strtype.equals(TreeConstants.comp.getString())){
			comp cmp = (comp)body;
			expressionWalk(cmp.e1);
		}
		else if(strtype.equals(TreeConstants.cond.getString())){
			cond cnd = (cond)body;
			expressionWalk(cnd.pred);
			expressionWalk(cnd.then_exp);
			if(cnd.else_exp!=null) expressionWalk(cnd.else_exp);
		}
		else if(strtype.equals(TreeConstants.dispatch.getString())){
			dispatch dis = (dispatch)body;
			lookup(self.name,dis.name,TreeConstants.method);
		}
		else if(strtype.equals(TreeConstants.static_dispatch.getString())){
			static_dispatch dis = (static_dispatch)body;
			lookup(dis.type_name,dis.name,TreeConstants.method);
			expressionWalk(dis.expr);
			Enumeration<Expression> e = dis.actual.getElements();
			while(e.hasMoreElements()){
				Expression expr = e.nextElement();
				expressionWalk(expr);
			}
		}
		else if(strtype.equals(TreeConstants.divide.getString())){
			divide div = (divide)body;
			expressionWalk(div.e1);
			expressionWalk(div.e2);
		}
		else if(strtype.equals(TreeConstants.eq.getString())){
			eq e = (eq)body;
			expressionWalk(e.e1);
			expressionWalk(e.e2);
		}
		else if(strtype.equals(TreeConstants.isvoid.getString())){
			isvoid isv = (isvoid)body;
			expressionWalk(isv.e1);
			
		}
		else if(strtype.equals(TreeConstants.leq.getString())){
			leq l = (leq)body;
			expressionWalk(l.e1);
			expressionWalk(l.e2);
		}
		else if(strtype.equals(TreeConstants.let.getString())){
			let le = (let)body;
			enterScope();
			letWalk(le); 
			exitScope();
		}
		else if(strtype.equals(TreeConstants.loop.getString())){
			loop lp = (loop)body;
			expressionWalk(lp.pred);
			expressionWalk(lp.body);
		}
		else if(strtype.equals(TreeConstants.lt.getString())){
			lt lessthan = (lt)body;
			expressionWalk(lessthan.e1);
			expressionWalk(lessthan.e2);
		}
		else if(strtype.equals(TreeConstants.mul.getString())){
			mul mult = (mul)body;
			expressionWalk(mult.e1);
			expressionWalk(mult.e2);
		}
		else if(strtype.equals(TreeConstants.neg.getString())){
			neg n = (neg)body;
			expressionWalk(n.e1);
		}
		else if(strtype.equals(TreeConstants._new.getString())){
			new_ n = (new_)body;
			if(classtable.lookup(n.type_name)==null) System.err.println("Symbol "+n.type_name.getString()+" not in scope.");
		}
		else if(strtype.equals(TreeConstants.sub.getString())){
			sub s = (sub)body;
			expressionWalk(s.e1);
			expressionWalk(s.e2);
		}
		else if(strtype.equals(TreeConstants.case_expr.getString())){
			typcase t = (typcase)body;
			expressionWalk(t.expr);
			
			Enumeration<branch> e = t.cases.getElements();
			while(e.hasMoreElements()){
				enterScope();
				branch b = e.nextElement();
				if(classtable.lookup(b.type_decl)==null) hanging.add(new object(b.lineNumber,b.type_decl));
				addId(b.name,TreeConstants.objectID,b.lineNumber);
				expressionWalk(b.expr);
				exitScope();
			}
			
			
		}
		else if(strtype.equals(TreeConstants.plus.getString())){
			plus p = (plus)body;
			expressionWalk(p.e1);
			expressionWalk(p.e2);
		}
		else if(strtype.equals(TreeConstants.objectID.getString())){
			object obj = (object)body;
			if(lookup(obj.name)==null){
				if(lookup(self.parent,obj.name,TreeConstants.attr)==null)
					hanging.add(obj);
			}
		}
	}

	private void letWalk(let le) throws Exception{
		addId(le.identifier,TreeConstants.objectID,le.lineNumber);
		classtable.lookup(le.type_decl);
		expressionWalk(le.init);
		expressionWalk(le.body);
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
	private LinkedList<object> hanging;
	private ClassTable classtable;
	private class_c self;
	private boolean ismain;
		
}
