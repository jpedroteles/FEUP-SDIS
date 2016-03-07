package multicast;

import java.util.*;

public class Data_base {
	Vector<Vector<String>> data_base = new Vector<Vector<String>>();
	
	public int register(String owner, String plate)
	{
		if(check_plate(plate))
		{
			System.out.println("The plate is already in the data base");
			return -1;
		}
		else if(!check_owner(owner, plate))
		{
			Vector<String> temp = new Vector<String>();
			temp.addElement(owner);
			temp.addElement(plate);
			data_base.addElement(temp);
		}
		System.out.println("New register submited to the data base");
		return count_cars();
	}
	
	public boolean check_owner(String owner, String plate)
	{
		for(int i=0; i<data_base.size();i++)
		{
			if(data_base.get(i).get(0).equals(owner))
			{
				data_base.get(i).add(plate);
				return true;
			}
		}
		return false;
	}
	
	public boolean check_plate(String plate)
	{
		for(int i=0; i<data_base.size();i++)
		{
			for(int j=1; j<data_base.get(i).size();j++)
			{
				if(data_base.get(i).get(j).equals(plate))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int count_cars()
	{
		int sum=0;
		for(int i=0; i<data_base.size();i++)
		{
			for(int j=1; j<data_base.get(i).size();j++)
			{
				sum++;
			}
		}
		return sum;
	}

	public String lookup(String plate)
	{
		if(check_plate(plate))
		{
			for(int i=0; i<data_base.size();i++)
			{
				for(int j=1; j<data_base.get(i).size();j++)
				{
					if(data_base.get(i).get(j).equals(plate))
					{
						return data_base.get(i).get(0) + " " + plate;
					}
				}
			}
		}
		return "NOT_FOUND";
	}
}
