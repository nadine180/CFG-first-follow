import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class CFG {
	String inputString;
	Hashtable<String,String> cfg;
	String terminals;
	Hashtable<String,String> first;
	Hashtable<String,String> follow;
	
	public CFG(String input)
	{
		inputString = input;
		String[] rules = input.split(";");
		cfg = new Hashtable<String,String>();
		
		for(int i =0; i < rules.length;i++)
		{
			String ruleName = rules[i].charAt(0)+"";
			cfg.put(ruleName, rules[i].substring(2,rules[i].length()));
		}
		
		this.terminals = getTerminals();
		
		
	}
	
	public Set<String> getVariables()
	{
		return cfg.keySet();
	}
	
	public String getTerminals()
	{
		String terminals = "";
		for(int i = 0; i < inputString.length();i++)
		{
			if((inputString.charAt(i) > 96 && inputString.charAt(i) < 123)
					&& inputString.charAt(i) != 'e')
			{
				terminals +=inputString.charAt(i);
			}
		}
		
		return terminals;
	}

	public static String check(String hash,String newinput)
	{
		String output = "";
		for(int i = 0; i < newinput.length();i++)
		{
			if(!hash.contains(newinput.charAt(i)+""))
			{
				output+= newinput.charAt(i);
			}
		}
		return output;
	}
	
	public static String outputFormat(Hashtable<String,String> input,String original)
	{
		String output = "";
		String[] tokens = original.split(";");
		for(int i = 0; i < tokens.length;i++)
		{

			String key = tokens[i].charAt(0)+"";
			output+= key+","+input.get(key)+";";
		}
		
		return output.substring(0,output.length()-1);
	}
	
	public static String sortString(String before)
	{
		String after = "";
		char [] chararray = before.toCharArray();
		Arrays.sort(chararray);
		for(int i = 0; i < chararray.length;i++)
			after += chararray[i];
		
		return after;
	}

	public String getFirstOfBeta(String input) {
		String out = "";
		for (int i = 0; i < input.length(); i++) {
			String var = input.charAt(i) + "";
			if (input.charAt(i) > 64 && 
					input.charAt(i) < 91) {
				String firstString = first.get(var);
				String check = check(out,firstString);
				if(!check.equals(""))
					out+= check;
				if (!firstString.contains("e")) {
					return out;
				}

			} else {
				String terminals = "";
				terminals+= var;
				String check = check(out,terminals);
				if(!check.equals(""))
					out+= check;
				return out;
			}

		}

		return out;

	}
	
	public String First()
	{
		Hashtable<String,String>first = new Hashtable<String,String>();
		Set<String> variables = getVariables();
		for(String variable : variables)
		{
			first.put(variable, "");
		}
		
		for(int i = 0;i<terminals.length();i++)
		{
			first.put(terminals.charAt(i)+"", ""+terminals.charAt(i));
		}
		
		
		boolean change = true;

		while(change)
		{
			change = false;
			for(String variable : variables)
			{
				
				String rules = cfg.get(variable);
				String [] tokens = rules.split(",");
				
				for(int i = 0; i < tokens.length;i++)
				{
					String rule = tokens[i];
					//handling case if a rule begins with a terminal
					if(!variables.contains(rule.charAt(0)+""))
					{
						String firstOfVariable = first.get(variable);
						if(!firstOfVariable.contains(rule.charAt(0)+""))
						{
							firstOfVariable += rule.charAt(0);
							first.put(variable, firstOfVariable);
							change = true;
						}
					}
					
					else
					{
						//handling case if a rule all leads to epsilon
						boolean epsCheck = true;				
						
						for(int j = 0; j < rule.length();j++)
						{
							String firstOfVar = first.get(rule.charAt(j)+"");
							epsCheck &= firstOfVar.contains("e");
						}
						
						if(epsCheck)
						{
							String firstOfVariable = first.get(variable);
							if(!firstOfVariable.contains("e"))
							{
								firstOfVariable += "e";
								first.put(variable, firstOfVariable);
								change = true;
							}
						}
						

						
						//handling case of multiple variables:
						
						boolean pop = true;
						int count = 0;
						String firstOfVariable = first.get(variable);
						while(pop && count < rule.length() )
						{
							
							String var = rule.charAt(count)+"";
							if(variables.contains(var))
							{
								String firstOfVar = first.get(var);
								String output = check(firstOfVariable,firstOfVar);
								if(output.contains("e"))
								{
									int index = output.indexOf('e');
									output = output.substring(0,index) + output.substring(index+1);
								}
								if(!output.equals(""))
								{
									
									firstOfVariable += output;
									first.put(variable, firstOfVariable);
									change = true;
								}
								if(firstOfVar.contains("e"))
								{
									count++;
								}
								else
								{
									pop = false;
								}
							}
							else
							{
								if(!var.equals("e"))
								{
									String output = check(firstOfVariable,var);
									if(!output.equals("") && !output.equals("e"))
									{
										//output.replace("e", "");
										firstOfVariable += output;
										first.put(variable, firstOfVariable);
										change = true;
										
									}
									pop = false;
								}
								else
									pop = false;
							}
						}
						
					
					}
				
					
				
				
			}
			}
			
		}
		
		for(String variable : variables)
		{
			String oldFirst=  first.get(variable);
			String newFirst = sortString(oldFirst);
			first.put(variable, newFirst);
		}
		
		this.first = first;
		
		String output = outputFormat(first,inputString);
		return output;
	}
	
	public String Follow()
	{
		Hashtable<String,String> follow = new Hashtable<String,String>();
		
		Set<String> variables = getVariables();
		
		for(String variable : variables)
		{
			String test = "";
			if(variable.equals("S"))
				test = "$";
			follow.put(variable, test);
		}
		
		boolean change = true;
		while(change)
		{
			change = false;
			for(String variable : variables)
			{
				String rules = cfg.get(variable);
				String[] tokens = rules.split(",");
				for(int i = 0; i < tokens.length;i++)
				{
					String rule = tokens[i];
					
					for(int j = 0; j < rule.length();j++)
					{
						String var = rule.charAt(j)+"";
						
						
						
						if(rule.charAt(j) > 64 && rule.charAt(j) < 91)
						{
							boolean epsForAll = true;
							
							
							for(int k = j+1; k < rule.length();k++)
							{
								epsForAll &= first.get(rule.charAt(k) + "").contains("e");
							}
							
							
							String beta = rule.substring(j+1);
							
							String firstBeta = getFirstOfBeta(beta);
							String firstOfBeta = firstBeta;
							if(firstOfBeta.contains("e"))
							{
								int index = firstOfBeta.indexOf('e');
								firstOfBeta = firstOfBeta.substring(0,index) + firstOfBeta.substring(index+1);
								
							}
							String followOfVar = follow.get(var);
							String output = check(followOfVar,firstOfBeta);
							if(!output.equals(""))
							{
								followOfVar += output;
								follow.put(var, followOfVar);
								change = true;
							}
							
							boolean epsCheck = true;
							for(int k = 1; k < j;k++)
								epsCheck &= getFirstOfBeta(rule.substring(0,k+1)).contains("e");
							if(!epsCheck)
								j = 1000000000;
							
							
							if((epsForAll) || beta.length() == 0)
							{
								String followOfA = follow.get(variable);
								output = check(followOfVar,followOfA);
								if(!output.equals(""))
								{
									followOfVar += output;
									follow.put(var, followOfVar);
									change = true;
								}
							}
							

							
						}
					}
					
				}
			}
		}
		
		for(String variable : variables)
		{
			String oldFollow = follow.get(variable);
			String newFollow = sortString(oldFollow);
			
			if(newFollow.contains("$"))
			{
				newFollow = newFollow.substring(1) + "$";
			}
			follow.put(variable, newFollow);
		}
		String output = outputFormat(follow,inputString);
		this.follow = follow;
		return output;
	}
	public static void main(String[] args) {
		String input = "S,ScT,T;T,aSb,iaLb,e;L,SdL,S"; 
		CFG cfg = new CFG(input); 
		String firstEncoding = cfg.First(); 
		String followEncoding = cfg.Follow(); 
		System.out.println("First: " + firstEncoding); 
		System.out.println("Follow: " + followEncoding);

	}
}
