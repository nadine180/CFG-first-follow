import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Table {

	CFG cfg;
	List<String> key;
	Hashtable<String,String> predictingTable;
	String parsingTable;
	public Table(CFG cfg)
	{
		this.cfg = cfg;
		cfg.First();
		cfg.Follow();
		Hashtable<String,String> first = cfg.first;
		Hashtable<String,String> follow = cfg.follow;
		Set<String> variables = cfg.getVariables();
		String t = cfg.getTerminals()+"$";
		String terminals ="";
		for(int i = 0; i < t.length();i++)
		if(!(t.charAt(i) == 'e'))
		{
			terminals+= t.charAt(i);
		}
		predictingTable = new Hashtable<String,String>();
		
		for(String variable : variables)
		{
		
			for(int i = 0; i < terminals.length();i++)
			{
				if(terminals.charAt(i) != 'e')
				{
				String [] keyPair = new String[2];
				keyPair[0] = variable;
				keyPair[1] = terminals.charAt(i) + "";
				String key = arrayToString(keyPair);
				predictingTable.put(key, "");
				}
			}
		}
		for(String variable : variables)
		{
			String[] rules = cfg.cfg.get(variable).split(",");
			for(int i = 0; i < terminals.length();i++)
			{
				String terminal = terminals.charAt(i) + "";
				for(int j= 0; j < rules.length;j++)
				{
					String rule = rules[j];
					String firstOfRule = cfg.getFirstOfBeta(rule);
					String followOfA = follow.get(variable);
					if(firstOfRule.contains(terminal) ||
							(firstOfRule.contains("e") && followOfA.contains(terminal)))
					{
						String[] keyPair = new String[2];
						keyPair[0] = variable;
						keyPair[1] = terminal;
						String key = arrayToString(keyPair);
						predictingTable.put(key, rule);
					}
				
				}
			}
		}
		
		parsingTable = outputFormat(predictingTable);
		System.out.println(parsingTable);
	}
	
	public static String arrayToString(String [] array)
	{
		String output = "[";
		for(int i = 0; i < array.length;i++)
			output+= array[i] + ",";
		output = output.substring(0,output.length()-1) + "]";
		return output;
		
	}
	
	
	public String outputFormat(Hashtable<String,String> table)
	{
		String output = "";
		String t = cfg.sortString(cfg.getTerminals());
		String terminals = "";
		for(int i = 0; i < t.length();i++)
		if(!(t.charAt(i) == 'e'))
		{
			terminals+=t.charAt(i);
		}
		
		terminals += "$";
		
		String[] tokens = cfg.inputString.split(";");
		for(int i = 0; i < tokens.length;i++)
		{
			for(int j = 0; j < terminals.length();j++)
			{
				
				String[] keyPair = new String[2];
				keyPair[0] = tokens[i].charAt(0)+"";
				keyPair[1] = terminals.charAt(j)+"";
				String key = arrayToString(keyPair);
				String rule = table.get(key);
				if(!rule.equals(""))
				{
				output+= tokens[i].charAt(0)+",";
				output+=terminals.charAt(j)+",";
				output+= rule+";" ;
				}
				
			}
		}
		
		return output.substring(0,output.length()-1);
	}
	
	public String getParsingTable()
	{
		return parsingTable;
	}

	public  String Parse(String input)
	{
		Stack<String> pda = new Stack<String>();
		pda.push("$");
		pda.push("S");
		int ip = 0;
		String output="S,";
		String X = pda.peek();
		
		while(!StackEmpty(pda))
		{
			if(X.equals(input.charAt(ip)+""))
			{
				ip++;
				pda.pop();
			}
			else
			{
				if(this.cfg.getTerminals().contains(X))
				{
					output+="ERROR";
					System.out.println(output);
					return output;
				}
				else
				{
					String [] keyPair = new String[2];
					keyPair[0] = X;
					keyPair[1] = input.charAt(ip)+"";
					String key = arrayToString(keyPair);
					if(!predictingTable.containsKey(key) || predictingTable.get(key).equals(""))
					{
						output+="ERROR";
						System.out.println(output);
						return output;
					}
					else
					{
						String [] outputArray = output.split(",");
						String rule1 = outputArray[outputArray.length-1];
						String rule = predictingTable.get(key);
						
						if(rule.equals("e"))
						{
							int pft = rule1.length();
							for(int i = 0; i < pft;i++)
							{
								if(X.equals(rule1.charAt(i)+""))
								{
									String newrule1 = rule1.substring(0,i)+rule1.substring(i+1);
									output+=newrule1+",";
//									while(!StackEmpty(pda) && !pda.peek().equals(X))
//										pda.pop();
									pda.pop();
									break;
//									
								}
							}
						}
						else
						{
							int pft = rule1.length();
							for(int i = 0; i < pft;i++)
							{
								if(X.equals(rule1.charAt(i)+""))
								{
									String newrule1 = rule1.substring(0,i)+rule+rule1.substring(i+1);
									
									output+= newrule1+",";
									
//									while(!StackEmpty(pda) && !pda.peek().equals(X))
//										pda.pop();
//									
									pda.pop();
									for(int j = rule.length()-1;j>-1;j--)
									{
										pda.push(rule.charAt(j)+"");
									}
									break;
									
								}
							}
							
						}
						
					}
				}
				
			}
				
			X = pda.peek();
			
			
		}
		
		System.out.println(output.substring(0,output.length()-1));
		return output.substring(0,output.length()-1);
	}
	
	public static boolean StackEmpty(Stack s)
	{
		if(s.peek().equals("$"))
			return true;
		
		return false;
	}

	
	public static void main(String[] args) {
		String input = "S,iST,e;T,cS,a";
		CFG cfg = new CFG(input);
		Table b = new Table(cfg);
		b.Parse("iiac$");
		b.Parse("iia$");
	}

}
