package edu.rwth.datacenterrats.LectureRooms;

/**
 * 
 * Class that the defines the data stored in a hoersaal item.
 * @author Benjamin Grap
 *
 */
public class hoersaal {
	public String RaumID;
	public String Raumname;
	public String Adresse;
	public String Sitzplaetze;
	public String Raumart;

	public hoersaal()
		{
			// TODO Auto-generated constructor stub
		}

	public hoersaal(String RaumID, String Raumname, String Adresse, String Sitzplaetze, String Raumart)
		{
			this.RaumID = RaumID;
			this.Raumname = Raumname;
			this.Adresse= Adresse;
			this.Sitzplaetze = Sitzplaetze;
			this.Raumart = Raumart;
		}
	
	public String getRaumID(){
		return this.RaumID;
	}
	public String getRaumname(){
		return this.Raumname;
	}
	public String getAdresse(){
		return this.Adresse;
	}
	public String getSitzplaetze(){
		return this.Sitzplaetze;
	}
	public String getRaumart(){
		return this.Raumart;
	}
	
	public boolean equals(hoersaal obj){
		if(this.RaumID.equals(obj.getRaumID())){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public String toString()
		{
			return  this.RaumID + " ("  + this.Raumname +")";
		}
}


