package edu.rwth.datacenterrats.L2P;

/**
 * Class that describes a Materiallist-item.
 * Name and Link.
 * @author 
 *
 */
public class Materiallist {
	
	private String name;
	private String link;

	Materiallist(){
		
	}
	
	Materiallist(String name, String link){
		this.name = name;
		this.link = link;
	
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setlink(String link){
		this.link = link;
	}
	
	
	public String getName(){
		return this.name;
	}
	public String getlink(){
		return this.link;
	}
	
}
