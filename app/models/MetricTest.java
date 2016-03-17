package models;

import javax.persistence.*;

@Entity
public class MetricTest{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public String idMetricTest;
    
	public String idMetricDef;

	public String idTestDef;
    
}
