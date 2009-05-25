import java.util.Enumeration;


public class TypeChecker {
	
	private Classes classList;
	private CoolSymbolTable b;
	private InheritanceGraph g;
	
	
	public TypeChecker(Classes cl) throws RuntimeException {
		classList = cl;
		b = new CoolSymbolTable();
		g = new InheritanceGraph(cl);
		
		Enumeration<Class_> e = classList.getElements();
		
		while(e.hasMoreElements()) {
			checkElement((Class_)e.nextElement());
		}
	}
	
	
	
	
	public void checkElement(TreeNode t) throws RuntimeException {
		
		AbstractSymbol nodeType = t.getNodeType();
		
		/*	FEATURES */
		if(nodeType.equalString("_features", 9)) {
			Features f = (Features)t;
			
			Enumeration e = f.getElements();
			while(e.hasMoreElements())
				checkElement((Feature)e.nextElement());
		}
		/*	ASSIGN */
		else if(nodeType.equalString("_assign", 7)) {
			assign a = (assign) t;
			
			AbstractSymbol id = a.getName();
			
			checkElement(a.getExpression());
			
			ObjectInfo idType = b.lookup(id);
			AbstractSymbol exprType = a.getExpression().get_type();
			
			//se expr è sottoclasse di id ok
			//exprType <= idType
			if(!g.inheritsFrom(exprType, idType.getType()))
				throw new RuntimeException(a.getLineNumber() + ": Type mismatch: can't convert from " + exprType.getString() + " to " + idType.getType());
			
			
		}
		
	}
	
	
	private boolean inheritsFrom(String)

}
