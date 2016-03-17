package models;

import javax.persistence.*;

@Entity
//@NamedQuery(name="Version.findByName",query="SELECT c.name FROM Version c WHERE c.name = :name")
public class Version{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String id;

    public String name;
    
    public String headId;
    
    public String baseId;
    
    public void setheadId(String id)
    {
        
        this.headId=id;
    }
    
        public void setbaseId(String id)
    {
        
        this.baseId=id;
    }
}
