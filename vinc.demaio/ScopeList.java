import java.util.ArrayList;
/** 
 * This class models a list of child scope
 * (maybe we actually don't need it)
 * @author Vincenzo De Maio 
 */
public class ScopeList extends ArrayList<Scope>{
	
	public ScopeList(){
		super();
	}
	public void addScope(Scope s){
		super.add(s);
	}

}
