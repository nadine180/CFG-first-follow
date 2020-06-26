import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Run_Simulator {

	/*
	 * Please update the file/class name, and the following comment
	 */
	
	// T11_37_1156_Nadine_Tarek
	static class CFG{
		String grammar;
		Hashtable<String,String> cfg;
		String terminals;
		Hashtable<String,String> first;
		Hashtable<String,String> follow;
		List<String> key;
		Hashtable<String,String> predictingTable;
		String parsingTable;
		
		/**
		 * Creates an instance of the CFG class. This should parse a string
		 * representation of the grammar and set your internal CFG attributes
		 * 
		 * @param grammar A string representation of a CFG
		 */
		
		public CFG(String grammar)
		{
			this.grammar = grammar;
			String[] rules = grammar.split(";");
			cfg = new Hashtable<String,String>();
			
			for(int i =0; i < rules.length;i++)
			{
				String ruleName = rules[i].charAt(0)+"";
				cfg.put(ruleName, rules[i].substring(2,rules[i].length()));
			}
			
			this.terminals = getTerminals();
			
			
		}

		
		/**
		 * Generates the parsing table for this context free grammar. This should set
		 * your internal parsing table attributes
		 * 
		 * @return A string representation of the parsing table
		 */
		
		public String table()
		{
			First();
			Follow();
			Set<String> variables = getVariables();
			String t = getTerminals()+"$";
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
				String[] rules = cfg.get(variable).split(",");
				for(int i = 0; i < terminals.length();i++)
				{
					String terminal = terminals.charAt(i) + "";
					for(int j= 0; j < rules.length;j++)
					{
						String rule = rules[j];
						String firstOfRule = getFirstOfBeta(rule);
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
			return parsingTable;
		}
		
		

		/**
		 * Parses the input string using the parsing table
		 * 
		 * @param s The string to parse using the parsing table
		 * @return A string representation of a left most derivation
		 */
		
		public  String parse(String s)
		{
			String input = s;
			input += "$";
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
					if(this.getTerminals().contains(X))
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
//										while(!StackEmpty(pda) && !pda.peek().equals(X))
//											pda.pop();
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
										
//										while(!StackEmpty(pda) && !pda.peek().equals(X))
//											pda.pop();
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
			
			return output.substring(0,output.length()-1);
		}
		
		
		public Set<String> getVariables()
		{
			return cfg.keySet();
		}
		
		public String getTerminals()
		{
			String terminals = "";
			for(int i = 0; i < grammar.length();i++)
			{
				if((grammar.charAt(i) > 96 && grammar.charAt(i) < 123)
						&& grammar.charAt(i) != 'e')
				{
					if(!terminals.contains(grammar.charAt(i) + ""))
					terminals +=grammar.charAt(i);
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
			
			String output = outputFormat(first,grammar);
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
			String output = outputFormat(follow,grammar);
			this.follow = follow;
			return output;
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
			String t = sortString(getTerminals());
			String terminals = "";
			for(int i = 0; i < t.length();i++)
			if(!(t.charAt(i) == 'e'))
			{
				terminals+=t.charAt(i);
			}
			
			terminals += "$";
			Set<String> variables = getVariables();
			String[] tokens = grammar.split(";");
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
	
		public static boolean StackEmpty(Stack s)
		{
			if(s.peek().equals("$"))
				return true;
			
			return false;
		}
	}
	
	public static void main(String[] args) {
		
		/*
		 * Please make sure that this EXACT code works. This means that the method
		 * and class names are case sensitive
		 */
		
		String grammar = "S,iST,e;T,cS,a";
		String input1 = "iiac";
		String input2 = "iia";
		CFG g = new CFG(grammar);
		System.out.println(g.table());
		System.out.println(g.parse(input1));
		System.out.println(g.parse(input2));
	}
}
