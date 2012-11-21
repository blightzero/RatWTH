package edu.rwth.datacenterrats.L2P;


/**
 * Class that describes a L2P Room.
 * NAME, L2P Link, Campus Link, RSS Link
 * @author blightzero
 *
 */
public class LearnRoom {
	
	private String name;
	private String linkl2p;
	private String linkcampus;
	private String rssfeed;
	
	LearnRoom(){
		
	}
	
	LearnRoom(String name, String linkl2p, String linkcampus, String rssfeed){
		this.name = name;
		this.linkl2p = linkl2p;
		this.linkcampus = linkcampus;
		this.rssfeed = rssfeed;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setlinkl2p(String linkl2p){
		this.linkl2p = linkl2p;
	}
	
	public void setlinkcampus(String linkcampus){
		this.linkcampus = linkcampus;
	}
	
	public void setrssfeed(String rssfeed){
		this.rssfeed = rssfeed;
	}
	
	public String getName(){
		return this.name;
	}
	public String getlinkl2p(){
		return this.linkl2p;
	}
	public String getlinkcampus(){
		return this.linkcampus;
	}
	public String getrssfeed(){
		return this.rssfeed;
	}
}
