
public class SymDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		AbstractSymbol s1,s2,s3,s4,s5,s6,s7;
		CoolSymbolTable tbl;
		
		s1 = AbstractTable.idtable.addString("id1");
		s2 = AbstractTable.idtable.addString("id2");
		s3 = AbstractTable.idtable.addString("id1");
		s4 = AbstractTable.idtable.addString("id1");
		s5 = AbstractTable.idtable.addString("id4");
		s6 = AbstractTable.idtable.addString("id5");
		s7 = AbstractTable.idtable.addString("id5");
		
		tbl = new CoolSymbolTable();
		tbl.addId(s1,new Integer(10));
		tbl.addId(s2,"una stringa");
		/* tbl.addId(s4,"che bella stringa! secondo me dà errore");
		 * qui dà errore perchè tento di ridefinire id1 
		 */
		tbl.enterScope(tbl.createScope());
		System.out.println(tbl.lookup(s1));
		System.out.println(tbl.lookup(s2));
		//qui posso aggiungerla, in quanto sono in un'altro scope
		tbl.addId(s3,"un'altra stringa");
		//qui mi deve restituire la most closely nested
		System.out.println(tbl.lookup(s3));
		System.out.println(tbl.getCurrentScope().inScope(s1));
		System.out.println(tbl.getCurrentScope().inScope(s2));
		System.out.println(tbl.getCurrentScope().inScope(s3));
		/* tbl.addId(s4,new Object());
		 * Anche qui dà errore perchè tento di sovrascrivere id1 
		 */
		tbl.addId(s5,"foo");
		tbl.exitScope();
		tbl.addId(s6,"bar");
		ScopeList list = tbl.getCurrentScope().getChildList();
		Scope cl = list.get(0);
		tbl.enterScope(cl);
		tbl.addId(s7,new Integer(34));
	}

}
