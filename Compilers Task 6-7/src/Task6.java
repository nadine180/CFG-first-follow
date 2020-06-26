import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class Task6 {

	public static void main(String[] args) {
		String input =  "S,iST,e;T,cS,a";
		CFG cfg = new CFG(input);
		System.out.println(cfg.First());
		System.out.println(cfg.Follow());
		
		
		
		//wrong
		//correct follow: S,$;A,bc;B,c;C,dz$;D,z$;Z,$
		
		
	}
	
	
}
