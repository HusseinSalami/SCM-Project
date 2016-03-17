package models;

import javax.persistence.*;

@Entity
public class MetricValue{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String idMetricValue;

    public String idTestRun;
    
    public String idMetricDef;
    
    public String value;
    
}
