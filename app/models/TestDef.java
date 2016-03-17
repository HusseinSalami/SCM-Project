package models;

import javax.persistence.*;

@Entity
public class TestDef{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String idTestDef;

    public String name;
    
    public String description;
    
}
