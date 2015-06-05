package br.com.thiagobarso.model;

import java.io.Serializable;

public class PropertyRdf implements Serializable{

	private static final long serialVersionUID = -5158053169622103300L;
	
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
