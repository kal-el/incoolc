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
		String errStr = "";
		
		while(e.hasMoreElements()) {
			try {
				checkElement((Class_)e.nextElement());
			}
			catch(RuntimeException e) {
				errStr += e.getMessage() + "\n";
			}
		}
		
		if(!errStr.equals(""))
			throw new RuntimeException(errStr);
	}
	
	
	
	
	public void checkElement(TreeNode t) throws RuntimeException {
		
		AbstractSymbol nodeType = t.getNodeType();
		
		/*	FEATURES */
		if(nodeType.equalString("_features", 9)) {
			Features f = (Features)t;
			String errMsg = "";
			
			Enumeration e = f.getElements();
			while(e.hasMoreElements()) {
				try {
					checkElement((Feature)e.nextElement());
				}
				catch(RuntimeException e) {
					errMsg += e.getMessage() + "\n";
				}
			}
				
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
		
		}
		/*	ASSIGN */
		else if(nodeType.equalString("_assign", 7)) {
			assign a = (assign) t;
			String errMsg;
			
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
			String errMsg;
			
			Expression e0 = con.getCondition();
			Expression e1 = con.getThenExpression();
			Expression e2 = con.getElseExpression();
			
			try{
				checkElement(e0);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex3) {
				errMsg += ex3.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e0.get_type().getString().equals("Bool"))
				throw new RuntimeException(con.getLineNumber() + ": The condition in the if-then-else expression is of type " + e0.get_type().getString() + ". Boolean required.");
			
			AbstractSymbol lub = g.getLub(e1.get_type(), e2.get_type());
			con.set_type(lub);
		}
		/* CASE */
		else if(nodeType.equalString("_case", 5)) {
			typcase tc = (typcase)t;
			String errMsg;
			
			Expression e0 = tc.getExpression();
			Cases cases = tc.getCases();
			
			try {
				checkElement(e0);
			}
			catch(RuntimeException ex0) {
				errMsg += ex0.getMessage() + "\n";
			}
			
			
			AbstractSymbol commonLub = TreeConstants.Object_;
			if(errMsg.equals(""))
				commonLub = e0.get_type();
			
			Enumeration caseList = cases.getElements();
			while(caseList.hasMoreElements()){
				branch singleCase = (branch)caseList.nextElement();
				try {
					checkElement(singleCase.getExpression());
				}
				catch(RuntimeException ex1) {
					errMsg += ex1.getMessage() + "\n";
				}
				
				
				commonLub = g.getLub(commonLub, singleCase.getBranchType());
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			tc.set_type(commonLub);
		}
		/* ADD */
		else if(nodeType.equalString("_plus", 5)) {
			plus somma = (plus)t;
			String errMsg;
			
			Expression e1 = somma.getFirstOperand();
			Expression e2 = somma.getSecondOperand();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			//se c'è errore negli addendi non si può controllare il tipo della somma
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(somma.getLineNumber() + ": Type mismatch: sum operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(somma.getLineNumber() + ": Type mismatch: sum operand is " + e2.get_type().getString() + ". Int required.");
			
			somma.set_type(TreeConstants.Int);
		}
		/* SUB */
		else if(nodeType.equalString("_sub", 4)) {
			sub sottr = (sub)t;
			String errMsg;
			
			Expression e1 = sottr.getFirstOperand();
			Expression e2 = sottr.getSecondOperand();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(sottr.getLineNumber() + ": Type mismatch: sub operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(sottr.getLineNumber() + ": Type mismatch: sub operand is " + e2.get_type().getString() + ". Int required.");
			
			sottr.set_type(TreeConstants.Int);
		}
		/* MULT */
		else if(nodeType.equalString("_mul", 4)) {
			mul mult = (mul)t;
			String errMsg;
			
			Expression e1 = mult.getFirstOperand();
			Expression e2 = mult.getSecondOperand();
			
			try{
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(mult.getLineNumber() + ": Type mismatch: mul operand is " + e1.get_type().getString() + ". Int required.");
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(mult.getLineNumber() + ": Type mismatch: mul operand is " + e2.get_type().getString() + ". Int required.");
			
			mult.set_type(TreeConstants.Int);
		}
		/* DIVIDE */
		else if(nodeType.equalString("_divide", 7)) {
			divide div = (divide)t;
			String errMsg;
			
			Expression e1 = div.getFirstOperand();
			Expression e2 = div.getSecondOperand();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
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
			String errMsg;
			
			Expression condizione = ciclo.getCondition();
			Expression body = ciclo.getBody();
			
			//se ci sono errori sia nella condizione che nel body
			//li restituisco entrambi
			try {
				checkElement(condizione);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(body);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!condizione.get_type().getString().equals("Bool"))
				throw new RuntimeException(ciclo.getLineNumber() + ": Type mismatch: loop condition must to be Boolean instead of " + condizione.get_type().getString());
			
			ciclo.set_type(TreeConstants.Object_);
		}
		/* VARIABLES */
		else if(nodeType.equalString("_objectID", 9)) {
			object id = (object)t;
			
			ObjectInfo idType = b.lookup(id);
			id.set_type(idType.getType);
		}
		/* LET */
		else if(nodeType.equalString("_let", 4)) {
			let l = (let)t;
			String errMsg;
			
			//il body può contenere altre dichiarazioni del let
			//che verranno controllate ricorsivamente
			//il tipo di ritorno versrà settato partendo dall'ultima dichiarazione del let
			//e risalendo nella ricorsione fino alla prima
			Expression init = l.getInitialization();
			Expression body = l.getBody();
			
			try {
				checkElement(init);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(body);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!init.get_type().getString().equals("_no_expr")) {
				if(!g.inheritsFrom(init.get_type(), l.get_type()))
					throw new RuntimeException(l.getLineNumber() + ": Type mismatch: let identifier " + l.geIdentifier().getString() + " cannot be initialized with type " + init.get_type().getString());
			}
			
			l.set_type(body.get_type());
		}
		/* BLOCCO */
		else if(nodeType.equalString("_block", 6)) {
			block bl = (block)t;
			String errMsg;
			
			AbstractSymbol lastType = TreeConstants.No_type;
			
			Expressions blockExpressions = bl.getBody();
			Enumeration<Expression> e = blockExpressions.getElements();
			while(e.hasMoreElements()) {
				Expression expr = (Expression)e.nextElement();
				
				//eventualmente riporto gli errori nelle singole
				//espressioni
				try {
					checkElement(expr);
					lastType = expr.get_type();
				}
				catch(RuntimeException e) {
					errMsg += e.getMessage() + "\n";
				}
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			bl.set_type(lastType);
		}
		/* ISVOID */
		else if(nodeType.equalString("_isvoid", 7)) {
			isvoid expr = (isvoid)t;
			String errMsg;
			
			Expression e1 = expr.getExpression();
			checkElement(e1);
			
			expr.set_type(TreeConstants.Bool);
		}
		/* LT */
		else if(nodeType.equalString("_lt", 3)) {
			lt less = (lt)t;
			String errMsg;
			
			Expression e1 = less.getFirst();
			Expression e2 = less.getSecond();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(less.getLineNumber() + ": Type mismatch: first operand in LT need to be Int instead of " + e1.get_type().getString());
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(less.getLineNumber() + ": Type mismatch: second operand in LT need to be Int instead of " + e2.get_type().getString());
			
			less.set_type(TreeConstants.Bool);
		}
		/* LE */
		else if(nodeType.equalString("_leq", 4)) {
			leq lesseq = (leq)t;
			String errMsg;
			
			Expression e1 = lesseq.getFirst();
			Expression e2 = lesseq.getSecond();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(lesseq.getLineNumber() + ": Type mismatch: first operand in LEQ need to be Int instead of " + e1.get_type().getString());
			if(!e2.get_type().getString().equals("Int"))
				throw new RuntimeException(lesseq.getLineNumber() + ": Type mismatch: second operand in LEQ need to be Int instead of " + e2.get_type().getString());
			
			lesseq.set_type(TreeConstants.Bool);
		}
		/* EQUAL */
		else if(nodeType.equalString("_eq", 3)) {
			eq equal = (eq)t;
			String errMsg;
			
			Expression e1 = equal.getFirst();
			Expression e2 = equal.getSecond();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException ex1) {
				errMsg += ex1.getMessage() + "\n";
			}
			try {
				checkElement(e2);
			}
			catch(RuntimeException ex2) {
				errMsg += ex2.getMessage() + "\n";
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			if(!e1.get_type().getString().equals("Int") || !e1.get_type().getString().equals("String") || !e1.get_type().getString().equals("Bool"))
				throw new RuntimeException(equal.getLineNumber() + ": Type mismatch: first operand in EQ need to be Int, String, or Bool instead of " + e1.get_type().getString());
			if(!e2.get_type().getString().equals("Int") || !e2.get_type().getString().equals("String") || !e2.get_type().getString().equals("Bool"))
				throw new RuntimeException(equal.getLineNumber() + ": Type mismatch: second operand in EQ need to be Int, String, or Bool instead of " + e2.get_type().getString());
			
			if(!e1.get_type().getString().equals(e2.get_type().getString()))
				throw new RuntimeException(equal.getLineNumber() + ": Type mismatch: the operands for the EQ operator must to be of the same type (" + e1.get_type().getString() + ", " + e2.get_type().getString() + ")");
			
			equal.set_type(TreeConstants.Bool);
		}
		/* NEG */
		else if(nodeType.equalString("_neg", 4)) {
			neg negaz = (neg)t;
			
			Expression e1 = negaz.getExpression();
			checkElement(e1);
			
			if(!e1.get_type().getString().equals("Int"))
				throw new RuntimeException(negaz.getLineNumber() + ": Type mismatch: operator Neg requires Int instead of " + e1.get_type().getString());
			
			negaz.set_type(TreeConstants.Int);
		}
		/* ATTR */
		else if(nodeType.equalString("_attr", 5)) {
			attr attributo = (attr)t;
			
			Expression init = attributo.getExpression();
			
			//se non c'è l'inizializzazione non c'è nient'altro da fare
			if(!init.getNodeType().getString().equals("_no_expr")) {
				checkElement(init);
				
				AbstractSymbol attrType = attributo.getType();
				if(!g.inheritsFrom(init.get_type(), attrType))
					throw new RuntimeException(attributo.getLineNumber() + ": Type mismatch: cannot convert from " + init.get_type().getString() + " to " + attrType.getString());
			}
			
			//l'attr non restituisce nessun tipo
		}
		/* METODI */
		else if(nodeType.equalString("_method", 8)) {
			method met = (method)t;
			
			AbstractSymbol returnType = met.getReturnType();
			Expression expr = met.getExpression();
			checkElement(expr);
			
			//il tipo di ritorno del metodo: T1
			//tipo di ritorno del body: T2
			//T2 <= T1
			if(!g.inheritsFrom(expr.get_type(), met.getReturnType()))
				throw new RuntimeException(met.getLineNumber() + ": Type mismatch: method must return the type " + met.getReturnType().getString() + " instead of " + expr.get_type());
			
			
		}
		/* DISPATCH */
		else if(nodeType.equalString("_dispatch", 9)) {
			dispatch call = (dispatch)t;
			String errMsg;
			
			//forma: e1.nomeMetodo(args)
			AbstractSymbol methodName = call.getName();
			Expression e1 = call.getExpression();
			Expressions args = call.getArgs();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException e) {
				errMsg += e.getMessage() + "\n";
			}
			
			//nome della classe dove risiede il metodo (o sottoclasse)
			AbstractSymbol className = e1.get_type();
			
			//ottengo le info sul metodo chiamato dalla Symbol Table
			ObjectInfo methodInfo = b.lookUp(className, methodName);
			Formals parameters = methodInfo.getFormals();
			
			//il numero di parametri passati alla chiamata dev'essere
			//uguale al numero di parametri che il metodo si aspetta
			if(args.getLength() != parameters.getLength())
				errMsg = call.getLineNumber() + ": the number of parameters for method " + call.getName().getString() + " is different from what expected\n";
			
			//controllo tipi argomenti
			Enumeration formalList = parameters.getElements(); 
			Enumeration<Expression> exprList = parameters.getElements();
			int i = 1;
			while(exprList.hasMoreElements() && formalList.hasMoreElements()){
				Expression ei = (Expression)exprList.nextElement();
				formalc f = (formalc)formalList.nextElement();
				
				try {
					checkElement(ei);
				}
				catch(RuntimeException ex) {
					errMsg += ex.getMessage() + "\n";
				}
				
				//per ogni parametro di tipo Ti
				//Ti <= T
				if(!g.inheritsFrom(ei.get_type(), f.getType()))
					errMsg += call.getLineNumber() + ": Type mismatch: " + f.getType().getString() + " instead of " + ei.get_type().getString() +" as " + i + " parameter in method " + methodName.getString() +"\n";
				
				i++;
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			call.set_type(methodInfo.getType());
		}
		/* STATIC DISPATCH */
		else if(nodeType.equalString("_static_dispatch", 16)) {
			static_dispatch call = (static_dispatch)t;
			String errMsg;
			
			AbstractSymbol methodName = call.getName();
			Expression e1 = call.getExpression();
			Expressions args = call.getArgs();
			AbstractSymbol staticType = call.get_type();
			
			try {
				checkElement(e1);
			}
			catch(RuntimeException e) {
				errMsg += e.getMessage() + "\n";
			}
			
			AbstractSymbol className = e1.get_type();
			
			//il metodo da cercare è definito nella classe dello
			//staticType
			ObjectInfo methodInfo = b.lookUp(staticType, methodName);
			Formals parameters = methodInfo.getFormals();
			
			//T0: tipo di e1
			//T: tipo a destra di @
			//T0 <= T
			if(!g.ihneritsFrom(e1.get_type(), staticType))
				errMsg += call.getLineNumber() + ": Type mismatch: expression type " + e1.get_type().getString() + " is not a subclass of " + staticType.getString() + "\n";
			
			//il numero di parametri passati alla chiamata dev'essere
			//uguale al numero di parametri che il metodo si aspetta
			if(args.getLength() != parameters.getLength())
				errMsg = call.getLineNumber() + ": the number of parameters for method " + call.getName().getString() + " is different from what expected\n";
			
			//controllo tipi argomenti
			Enumeration formalList = parameters.getElements(); 
			Enumeration<Expression> exprList = parameters.getElements();
			int i = 1;
			while(exprList.hasMoreElements() && formalList.hasMoreElements()){
				Expression ei = (Expression)exprList.nextElement();
				formalc f = (formalc)formalList.nextElement();
				
				try {
					checkElement(ei);
				}
				catch(RuntimeException ex) {
					errMsg += ex.getMessage() + "\n";
				}
				
				//per ogni parametro di tipo Ti
				//Ti <= T
				if(!g.inheritsFrom(ei.get_type(), f.getType()))
					errMsg += call.getLineNumber() + ": Type mismatch: " + f.getType().getString() + " instead of " + ei.get_type().getString() +" as " + i + " parameter in method " + methodName.getString() +"\n";
				
				i++;
			}
			
			if(!errMsg.equals(""))
				throw new RuntimeException(errMsg);
			
			call.set_type(methodInfo.getType());
			
		}
		
	}
	
	
	

}
