package Models;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Exceptions.InvalidMap;
import Services.MapService;
import Utils.CommonUtil;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;

public class Map {
    String d_mapFile;
    List<Continent> d_continents;
    List<Country> d_countries;
    HashMap<Integer, Boolean> d_countryReach = new HashMap<Integer, Boolean>();

    public String getD_mapFile() {
        return d_mapFile;
    }

    public void setD_mapFile(String p_mapFile) {
        this.d_mapFile = p_mapFile;
    }

    public List<Continent> getD_continents() {
        return d_continents;
    }

    public void setD_continents(List<Continent> p_continents) {
        this.d_continents = p_continents;
    }

    public List<Country> getD_countries() {
        return d_countries;
    }

    public void setD_countries(List<Country> p_countries) {
        this.d_countries = p_countries;
    }

    public List<Integer> getContinentIDs(){
        List<Integer> l_continentIDs = new ArrayList<Integer>();
        if (!d_continents.isEmpty()) {
            for(Continent c: d_continents){
                l_continentIDs.add(c.getD_continentID());
            }
        }
        return l_continentIDs;
    }

    public List<Integer> getCountryIDs(){
        List<Integer> l_countryIDs = new ArrayList<Integer>();
        if(!d_countries.isEmpty()){
            for(Country c: d_countries){
                l_countryIDs.add(c.getD_countryId());
            }
        }
        return l_countryIDs;
    }

    public void checkContinents() {
        for (Continent c : d_continents) {
            System.out.println("Continent ID "+ c.getD_continentID());
            System.out.println("Corresponding Countries");
            if (c.getD_countries()!=null) {
                for (Country country: c.getD_countries()){
                    System.out.println("Country : "+ country.getD_countryId());
                    System.out.println("Neighbours in Continent:");
                    if (!CommonUtil.isNull(country.getD_adjacentCountryIds())) {
                        for(Integer i: country.getD_adjacentCountryIds()){
                            System.out.println(i);
                        }
                    }
                }
            }
        }
    }

    public void checkCountries() {
        for (Country c : d_countries) {
            System.out.println("Country Id " + c.getD_countryId());
            System.out.println("Continent Id " + c.getD_continentId());
            System.out.println("Neighbours in Map:");
            if (!CommonUtil.isNull(c.getD_adjacentCountryIds())) {
                for (Integer i: c.d_adjacentCountryIds){
                    System.out.println(i);
                }
            }
        }
    }

    /**
     * Validates the complete map
     *
     * @return Bool Value if map is valid
     * @throws InvalidMap Exception
     */
    public Boolean Validate() throws InvalidMap {
        return (!checkForNullObjects() && checkContinentConnectivity() && checkCountryConnectivity());
    }

    public Boolean checkForNullObjects() throws InvalidMap{
        if(d_continents==null || d_continents.isEmpty()){
            throw new InvalidMap("Map must possess atleast one continent!");
        }
        if(d_countries==null || d_countries.isEmpty()){
            throw new InvalidMap("Map must possess atleast one country!");
        }
        for(Country c: d_countries){
            if(c.getD_adjacentCountryIds().size()<1){
                throw new InvalidMap(c.getD_countryName()+" does not possess any neighbour, hence isn't reachable!");
            }
        }
        return false;
    }

    /**
     * Checks All Continent's Inner Connectivity
     *
     * @return Bool Value if all are connected
     * @throws InvalidMap if any continent is not Connected
     */
    public Boolean checkContinentConnectivity() throws InvalidMap {
        boolean l_flagConnectivity = true;
        for (Continent c : d_continents) {
            if (null == c.getD_countries() || c.getD_countries().size() < 1) {
                throw new InvalidMap(c.getD_continentName() + " has no countries, it must possess atleast 1 country");
            }
            if (!subGraphConnectivity(c)) {
                l_flagConnectivity = false;
            }
        }
        return l_flagConnectivity;
    }

    /**
     * Checks Inner Connectivity of a Continent
     *
     * @param p_c Continent being checked
     * @return Bool Value if Continent is Connected
     * @throws InvalidMap Which country is not connected
     */
    public boolean subGraphConnectivity(Continent p_c) throws InvalidMap {
        HashMap<Integer, Boolean> l_continentCountry = new HashMap<Integer, Boolean>();

        for (Country c : p_c.getD_countries()) {
            l_continentCountry.put(c.getD_countryId(), false);
        }
        dfsSubgraph(p_c.getD_countries().get(0), l_continentCountry, p_c);
        for (Entry<Integer, Boolean> entry : l_continentCountry.entrySet()) {
            if (!entry.getValue()) {
                Country l_country = getCountry(entry.getKey());
                String l_messageException = l_country.getD_countryName() + " in Continent " + p_c.getD_continentName() + " is not reachable";
                throw new InvalidMap(l_messageException);
            }
        }
        return !l_continentCountry.containsValue(false);
    }

    /**
     * DFS Applied to the Continent Subgraph
     *
     * @param p_c                Country visited
     * @param p_continentCountry Hashmap of Visited Bool Values
     * @param p_continent        continent being checked for connectivity
     */
    public void dfsSubgraph(Country p_c, HashMap<Integer, Boolean> p_continentCountry, Continent p_continent) {
        p_continentCountry.put(p_c.getD_countryId(), true);
        for (Country c : p_continent.getD_countries()) {
            if (p_c.getD_adjacentCountryIds().contains(c.getD_countryId())) {
                if (!p_continentCountry.get(c.getD_countryId())) {
                    dfsSubgraph(c, p_continentCountry, p_continent);
                }
            }
        }
    }

    /**
     * Checks country connectivity in the map
     *
     * @return bool value for condition if all the countries are connected
     * @throws InvalidMap pointing out which Country is not connected
     */
    public boolean checkCountryConnectivity() throws InvalidMap {
        for (Country c : d_countries) {
            d_countryReach.put(c.getD_countryId(), false);
        }
        dfsCountry(d_countries.get(0));
        for (Entry<Integer, Boolean> entry : d_countryReach.entrySet()) {
            if (!entry.getValue()) {
                String l_exceptionMessage = getCountry(entry.getKey()).getD_countryName() + " country is not reachable";
                throw new InvalidMap(l_exceptionMessage);
            }
        }
        return !d_countryReach.containsValue(false);
    }

    /**
     * Iteratively applies the DFS search from the entered node
     *
     * @param p_c Country visited first
     * @throws InvalidMap Exception
     */
    public void dfsCountry(Country p_c) throws InvalidMap {
        d_countryReach.put(p_c.getD_countryId(), true);
        for (Country l_nextCountry : getAdjacentCountry(p_c)) {
            if (!d_countryReach.get(l_nextCountry.getD_countryId())) {
                dfsCountry(l_nextCountry);
            }
        }
    }

    /**
     * Gets the Adjacent Country Objects
     *
     * @param p_c Country
     * @return list of Adjacent Country Objects
     * @throws InvalidMap Exception
     */
    public List<Country> getAdjacentCountry(Country p_c) throws InvalidMap {
        List<Country> l_adjCountries = new ArrayList<Country>();
        if (p_c.getD_adjacentCountryIds().size() > 0) {
            for (int i : p_c.getD_adjacentCountryIds()) {
                l_adjCountries.add(getCountry(i));
            }
        } else {
            throw new InvalidMap(p_c.getD_countryId() + " doesn't have any adjacent countries");
        }
        return l_adjCountries;
    }


    /**
     * Finds the Country object from a given country ID
     *
     * @param p_countryId ID of the country object to be found
     * @return matching country object
     */
    public Country getCountry(Integer p_countryId) {
        return d_countries.stream().filter(l_country -> l_country.getD_countryId().equals(p_countryId)).findFirst().orElse(null);
    }

    public Country getCountryByName(String p_countryName){
        return d_countries.stream().filter(l_country -> l_country.getD_countryName().equals(p_countryName)).findFirst().orElse(null);
    }

    /**
     * Returns Continent Object for given continent Id
     * @param p_continentName Continent Name to be found
     * @return matching continent object
     */
    public Continent getContinent(String p_continentName){
       return d_continents.stream().filter(l_continent -> l_continent.getD_continentName().equals(p_continentName)).findFirst().orElse(null);
    }

    public Continent getContinentByID(Integer p_continentID){
        return d_continents.stream().filter(l_continent -> l_continent.getD_continentID().equals(p_continentID)).findFirst().orElse(null);
    }

    public void addContinent(String p_continentName, Integer p_controlValue) throws InvalidMap{
        int l_continentId;
        if (d_continents!=null) {
            l_continentId=d_continents.size()>0?Collections.max(getContinentIDs())+1:1;
            if(CommonUtil.isNull(getContinent(p_continentName))){
                d_continents.add(new Continent(l_continentId, p_continentName, p_controlValue));
            }else{
                throw new InvalidMap("Continent cannot be added! It already exists!");
            }
        }else{
            d_continents= new ArrayList<Continent>();
            d_continents.add(new Continent(1, p_continentName, p_controlValue));
        }
    }

    public void removeContinent(String p_continentName) throws InvalidMap{
        if (d_continents!=null) {
            if(!CommonUtil.isNull(getContinent(p_continentName))){
                if (getContinent(p_continentName).getD_countries()!=null) {
                    for(Country c: getContinent(p_continentName).getD_countries()){
                        removeCountryNeighboursFromAll(c.getD_countryId());
                        updateNeighboursCont(c.getD_countryId());
                        d_countries.remove(c);
                    }
                }
                d_continents.remove(getContinent(p_continentName));
            }else{
                throw new InvalidMap("No such Continent exists!");
            }
        } else{
            throw new InvalidMap("No continents in the Map to remove!");
        }
    }

    public void addCountry(String p_countryId, String p_continentId) throws InvalidMap{
        int l_countryId;
        if(d_countries==null){
            d_countries= new ArrayList<Country>();
        }
        if(CommonUtil.isNull(getCountryByName(p_countryId))){
            l_countryId=d_countries.size()>0? Collections.max(getCountryIDs())+1:1;
            if(d_continents!=null && getContinentIDs().contains(getContinent(p_continentId).getD_continentID())){
                d_countries.add(new Country(l_countryId,p_countryId,getContinent(p_continentId).getD_continentID()));
                for (Continent c: d_continents) {
                    if (c.getD_continentName().equals(p_continentId)) {
                        c.addCountry(new Country(l_countryId, p_countryId, getContinent(p_continentId).getD_continentID()));
                    }
                }
            } else{
                throw new InvalidMap("Cannot add Country to a Continent that doesn't exist!");
            }
        }else{
            throw new InvalidMap("Country with name "+ p_countryId+" already Exists!");
        }
    }

    public void removeCountry(String p_countryId) throws InvalidMap{
        if(d_countries!=null && !CommonUtil.isNull(getCountryByName(p_countryId))) {
            for(Continent c: d_continents){
                if(c.getD_continentID().equals(getCountryByName(p_countryId).getD_continentId())){
                    c.removeCountry(getCountryByName(p_countryId));
                }
                c.removeCountryNeighboursFromAll(getCountryByName(p_countryId).getD_countryId());
            }
            d_countries.remove(getCountryByName(p_countryId));
            removeCountryNeighboursFromAll(getCountryByName(p_countryId).getD_countryId());

        }else{
           throw new InvalidMap("Country:  "+ p_countryId+" does not exist!");
        }
    }

    public void addCountryNeighbour(String p_countryId, String p_neighbourCountryId) throws InvalidMap{
        if(d_countries!=null){
            if(!CommonUtil.isNull(getCountryByName(p_countryId)) && !CommonUtil.isNull(getCountryByName(p_neighbourCountryId))){
                d_countries.get(d_countries.indexOf(getCountryByName(p_countryId))).addNeighbour(getCountryByName(p_neighbourCountryId).getD_countryId());
                d_continents.get(d_continents.indexOf(getContinentByID(getCountryByName(p_countryId).getD_continentId()))).addCountryNeighbours(getCountryByName(p_countryId).getD_countryId(), getCountryByName(p_neighbourCountryId).getD_countryId());
            } else{
                throw new InvalidMap("Invalid Neighbour Pair! Either of the Countries Doesn't exist!");
            }
        }
    }

    public void removeCountryNeighbour(String p_countryId, String p_neighbourCountryId) throws InvalidMap{
        if(d_countries!=null){
            if(!CommonUtil.isNull(getCountryByName(p_countryId)) && !CommonUtil.isNull(getCountryByName(p_neighbourCountryId))) {
                d_countries.get(d_countries.indexOf(getCountryByName(p_countryId))).removeNeighbour(getCountryByName(p_neighbourCountryId).getD_countryId());
                d_continents.get(d_continents.indexOf(getContinentByID(getCountryByName(p_countryId).getD_continentId()))).removeSpecificNeighbour(getCountryByName(p_countryId).getD_countryId(), getCountryByName(p_neighbourCountryId).getD_countryId());
            } else{
                throw new InvalidMap("Invalid Neighbour Pair! Either of the Countries Doesn't exist!");
            }
        }
    }
    public void updateNeighboursCont(Integer p_countryId){
        for(Continent c: d_continents){
            c.removeCountryNeighboursFromAll(p_countryId);
        }
    }

    public void removeCountryNeighboursFromAll(Integer p_countryID){
        for (Country c: d_countries) {
            if (!CommonUtil.isNull(c.getD_adjacentCountryIds())) {
                if (c.getD_adjacentCountryIds().contains(p_countryID)) {
                    c.removeNeighbour(p_countryID);
                }
            }
        }
    }

    public static void main(String[] args) {
        MapService ms = new MapService();
        GameState gs = new GameState();
        ms.loadMap(gs, "europe.map");
    }
}
