package models;

import javax.persistence.*;

@Entity
public class MetricDef{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String idMetricDef;
    
    public String name;
    
    public String description;
    
    public String tolerance;
    
}
