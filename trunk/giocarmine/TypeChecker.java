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
			
			a.set_type(exprType);
		}
		/* BOOLEAN */
		else if(nodeType.equalString("Bool", 4)) {
			bool_const bool = (bool_const)t;
			bool.set_type(TreeConstants.Bool);
		}
		/* INT */
		else if(nodeType.equalString("Int", 3)) {
			int_const ic = (int_const)t;
			ic.set_type(TreeConstants.Int);
		}
		/* STRING */
		else if(nodeType.equalString("String", 6)) {
			string_const sc = (string_const)t;
			sc.set_type(TreeConstants.Str);
		}
		/* NEW */
		else if(nodeType.equalString("_new", 4)) {
			
			//AGGIUNGERE PER IL SELF_TYPE ------------------------
			
			new_ n = (new_)t;
			n.set_type(n.getTypeName());
		}
		/* IF-THEN-ELSE */
		else if(nodeType.equalString("_cond", 5)) {
			cond con = (cond)t;
			
			Expression e0 = con.getCondition();
			Expression e1 = con.getThenExpression();
			Expression e2 = con.getElseExpression();
			
			checkElement(e0);
			checkElement(e1);
			checkElement(e2);
			
			if(!e0.get_type().getString().equals("Bool"))
				throw new RuntimeException(con.getLineNumber() + ": The condition in the if-then-else expression is of type " + e0.get_type().getString() + ". Boolean required.");
			
			AbstractSymbol lub = g.getLub(e1.get_type(), e2.get_type());
			con.set_type(lub);
		}
		/* CASE */
		else if(nodeType.equalString("_case", 5)) {
			typcase tc = (typcase)t;
			
			Expression e0 = tc.getExpression();
			Cases cases = tc.getCases();
			
			checkElement(e0);
			
			AbstractSymbol commonLub = e0.get_type();
			
			Enumeration caseList = cases.getElements();
			while(caseList.hasMoreElements()){
				branch singleCase = (branch)caseList.nextElement();
				checkElement(singleCase.getExpression());
				
				commonLub = g.getLub(commonLub, singleCase.getBranchType());
			}
			
			tc.set_type(commonLub);
		}
		/* ADD */
		else if(nodeType.equalString("_plus", 5)) {
			plus somma = (plus)t;
			
			Expression e1 = somma.getFirstOperand();
			Expression e2 = somma.getSecondOperand();
			
			checkElement(e1);
			checkElement(e2);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(somma.getLineNumber() + ": Type mismatch: sum operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(somma.getLineNumber() + ": Type mismatch: sum operand is " + e2.get_type().getString() + ". Int required.");
			
			somma.set_type(TreeConstants.Int);
		}
		/* SUB */
		else if(nodeType.equalString("_sub", 4)) {
			sub sottr = (sub)t;
			
			Expression e1 = sottr.getFirstOperand();
			Expression e2 = sottr.getSecondOperand();
			
			checkElement(e1);
			checkElement(e2);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(sottr.getLineNumber() + ": Type mismatch: sub operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(sottr.getLineNumber() + ": Type mismatch: sub operand is " + e2.get_type().getString() + ". Int required.");
			
			sottr.set_type(TreeConstants.Int);
		}
		/* MULT */
		else if(nodeType.equalString("_mul", 4)) {
			mul mult = (mul)t;
			
			Expression e1 = mult.getFirstOperand();
			Expression e2 = mult.getSecondOperand();
			
			checkElement(e1);
			checkElement(e2);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(mult.getLineNumber() + ": Type mismatch: mul operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(mult.getLineNumber() + ": Type mismatch: mul operand is " + e2.get_type().getString() + ". Int required.");
			
			mult.set_type(TreeConstants.Int);
		}
		/* DIVIDE */
		else if(nodeType.equalString("_divide", 7)) {
			divide div = (divide)t;
			
			Expression e1 = div.getFirstOperand();
			Expression e2 = div.getSecondOperand();
			
			checkElement(e1);
			checkElement(e2);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(div.getLineNumber() + ": Type mismatch: divide operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(div.getLineNumber() + ": Type mismatch: divide operand is " + e2.get_type().getString() + ". Int required.");
			
			div.set_type(TreeConstants.Int);
		}
		/* NOT */
		else if(nodeType.equalString("_not", 4)) {
			comp not = (comp)t;
			
			Expression e1 = not.getExpression();
			checkElement(e1);
			
			if(!e1.get_type().getString().equals("Bool"))
				throw new RuntimeException(e1.getLineNumber() + ": Type mismatch: operator NOT requires Bool instead of " + e1.get_type().getString());
			
			not.set_type(TreeConstants.Bool);
		}
		/* LOOP */
		else if(nodeType.equalString("_loop", 5)) {
			loop ciclo = (loop)t;
			
			Expression condizione = ciclo.getCondition();
			Expression body = ciclo.getBody();
			
			checkElement(condizione);
			checkElement(body);
			
			if(!condizione.get_type().getString().equals("Bool"))
				throw new RuntimeException(ciclo.getLineNumber() + ": Type mismatch: loop condition must to be Boolean instead of " + condizione.get_type().getString());
			
			ciclo.set_type(TreeConstants.Object_);
		}
		/* DISPATCH */
		else if(nodeType.equalString("_dispatch", 9)) {
			
		}
		/* STATIC DISPATCH */
		else if(nodeType.equalString("_static_dispatch", 16)) {
			
		}
		
	}
	
	
	

}
