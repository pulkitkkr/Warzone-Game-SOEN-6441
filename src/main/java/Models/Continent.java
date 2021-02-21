package Models;

import Utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class Continent {
	Integer d_continentID;
	String d_continentName;
	Integer d_continentValue;
	List<Country> d_countries;

	public Continent(Integer p_continentID, String p_continentName, int p_continentValue) {
		this.d_continentID=p_continentID;
		this.d_continentName=p_continentName;
		this.d_continentValue=p_continentValue;
	}
	public Continent(){

	}
	public Integer getD_continentID() {
		return d_continentID;
	}
	public void setD_continentID(Integer p_continentID) {
		this.d_continentID = p_continentID;
	}
	public String getD_continentName() {
		return d_continentName;
	}
	public void setD_continentName(String p_continentName) {
		this.d_continentName = p_continentName;
	}
	public Integer getD_continentValue() {
		return d_continentValue;
	}
	public void setD_continentValue(Integer p_continentValue) {
		this.d_continentValue = p_continentValue;
	}
	public List<Country> getD_countries() {
		return d_countries;
	}
	public void setD_countries(List<Country> p_countries) {
		this.d_countries = p_countries;
	}
	public void addCountry(Country p_c){
		if (d_countries!=null){
			this.d_countries.add(p_c);
		}
		else{
			this.d_countries=new ArrayList<Country>();
			this.d_countries.add(p_c);
		}
	}
	public void removeCountry(Country p_c){
		if(d_countries==null){
			System.out.println("No such Country Exists");
		}else {
			d_countries.remove(p_c);
		}
	}

	public void removeCountryNeighbours(Integer p_countryId){
		if (null!=d_countries && !d_countries.isEmpty()) {
			for (Country c: d_countries){
				if (!CommonUtil.isNull(c.d_adjacentCountryIds)) {
					if (c.getD_adjacentCountryIds().contains(p_countryId)){
						c.removeNeighbour(p_countryId);
					}
				}
			}
		}
	}

	public void addCountryNeighbours(Integer p_countryInContinent, Integer p_neighbourCountry){
		for (Country c: d_countries){
			if (c.getD_countryId().equals(p_countryInContinent)){
				c.addNeighbour(p_neighbourCountry);
			}
		}
	}

	public Boolean checkCountry(Integer p_countryId){
		boolean l_flag=false;
		for (Country c: d_countries){
			if (c.getD_countryId().equals(p_countryId)) {
				l_flag = true;
				break;
			}
		}
		return l_flag;
	}

}
