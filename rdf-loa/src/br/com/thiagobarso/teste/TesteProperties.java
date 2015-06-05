package br.com.thiagobarso.teste;

import java.util.ArrayList;

import br.com.thiagobarso.service.SearchInRdf;

public class TesteProperties {

	public static void main(String[] args) {
		String singleroot = "/rdf-timoteo/loa2015/loa2015.nt";
		String t = "loa:ItemDespesa";
		ArrayList<String> teste = new SearchInRdf().getTables(singleroot);

	}

}
