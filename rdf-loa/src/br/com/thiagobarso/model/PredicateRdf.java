package br.com.thiagobarso.model;

import java.io.Serializable;

public class PredicateRdf implements Serializable{

	private static final long serialVersionUID = 7639045041343995040L;
	
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
