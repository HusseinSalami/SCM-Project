package models;

import javax.persistence.*;

@Entity
public class TestRun{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String idTestRun;
	
	public String idTestDef;

    
    
    public String idChangeList;
    
}
