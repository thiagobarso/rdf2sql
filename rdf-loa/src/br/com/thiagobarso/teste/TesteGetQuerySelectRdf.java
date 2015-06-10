package br.com.thiagobarso.teste;

import java.util.ArrayList;

import br.com.thiagobarso.service.SearchInRdf;

public class TesteGetQuerySelectRdf {

	public static void main(String[] args) {
		String singleroot = "/rdf-timoteo/loa2015/loa2015.nt";
		String t = "loa:Esfera";
		ArrayList<String> colunas = new SearchInRdf().getColunas(t, singleroot);
		String teste = new SearchInRdf().getQuerySelectRdf(t, singleroot, colunas);
		System.out.println(teste);

	}

}
