package com.namal.reing.output;
import com.namal.reing.models.*;

public interface IPrint{
	public String print(MMethod m);
	public String print(MClass c);
	public String print(MField f);
	public String print(MType t);
	public String print(MInterface i);
	public String print(MModifier m);
}
