package br.com.thiagobarso.system;

import java.util.ArrayList;

import br.com.thiagobarso.service.SearchInRdf;

public class DoRdf {

	private static SearchInRdf search = new SearchInRdf();

	public static void main(String[] args) {

		try{
		String singleroot = args[0];
		if(singleroot == null){
			System.out.print("Endereco do arquivo invalido");
		}
		ArrayList<String> tabelas = search.getTables(singleroot);
		StringBuilder queryCriacao = new StringBuilder();
		for (String t : tabelas) {
			queryCriacao.append(search.getQueryCreateTable(t, singleroot));
			System.out.println("EXECUTANDO SCRIPT DE BANCO: inicio_table: " + t
					+ "=============================");
			SearchInRdf.executaSql(queryCriacao.toString());
			System.out.println("FIM SCRIPT DE BANCO fim_table: " + t
					+ "=============================");
			queryCriacao = new StringBuilder();
		}
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Sem argumentos!!");
		}

	}

}
