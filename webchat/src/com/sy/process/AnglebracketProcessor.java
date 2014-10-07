package com.sy.process;

public class AnglebracketProcessor extends StringProcess
{

	@Override
	public String processString(String myString) 
	{
		// TODO Auto-generated method stub
		String str = myString.replace("<", "&lt;").replace(">", "&gt;");
		return str;
	}
	
	public AnglebracketProcessor(StringProcess successor)
	{
		this.successor = successor;
	}
	
}
