package com.trc202.helpers;

import org.bukkit.Material;

public class commandHelpers {
	
	public static boolean isInteger(String possableNum){
		try{
			Integer.valueOf(possableNum);
			return true;
		}
		catch(NumberFormatException e){
			return false;
		}
	}

	public static Material getMaterial(String aMaterial){
		Material materialOut = Material.AIR;
		if(isInteger(aMaterial)){
			int materialNum = Integer.valueOf(aMaterial);
			if((materialNum >= 0) && (materialNum <= Material.values().length)){
				materialOut = Material.values()[materialNum];
			}
		}
		else{
			aMaterial = aMaterial.toUpperCase();
			materialOut = Material.valueOf(aMaterial);
		}
		return materialOut;
	}
}
