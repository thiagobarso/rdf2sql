package br.com.thiagobarso.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassRdf implements Serializable{
	
	private static final long serialVersionUID = 4405040001046707604L;
	
	private String name;
	private ArrayList<PredicateRdf> predicates;
	private ArrayList<PropertyRdf> properties;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<PredicateRdf> getPredicates() {
		return predicates;
	}
	public void setPredicates(ArrayList<PredicateRdf> predicates) {
		this.predicates = predicates;
	}
	public ArrayList<PropertyRdf> getProperties() {
		return properties;
	}
	public void setProperties(ArrayList<PropertyRdf> properties) {
		this.properties = properties;
	}
	
	
	

}
