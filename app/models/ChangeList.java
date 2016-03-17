package models;

import javax.persistence.*;

@Entity
//@NamedQuery(name="ChangeList.findByversionId",query="SELECT c.id FROM ChangeList c WHERE c.versionId = :idversion")

public class ChangeList {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String id;

    public String description;
    
    public String versionId;
    
    public String user;
    
    public void setversionId(String id)
    {
        this.versionId=id;
    }
}