package br.com.thiagobarso.system;

import java.util.ArrayList;

import br.com.thiagobarso.service.SearchInRdf;

public class DoRdf {

	private static SearchInRdf search = new SearchInRdf();

	public static void main(String[] args) {

		try {
			String singleroot = args[0];
			if (singleroot == null) {
				System.out.print("Endereco do arquivo invalido");
			}
			ArrayList<String> tabelas = search.getTables(singleroot);
			StringBuffer queryCriacao = new StringBuffer();
			for (String t : tabelas) {
				queryCriacao.append(search.getQueryCreateTable(t, singleroot));
				System.out
						.println("inicio_criação_tabela: "	+ t);
				SearchInRdf.executaSql(queryCriacao.toString());
				System.out.println("fim_criação_tabela: " + t);
				queryCriacao = new StringBuffer();
			}
			for (String t : tabelas) {
				ArrayList<String> colunasPertencentesATabela = search
						.getColunas(t, singleroot);
				search.getQuerySelectRdf(t, singleroot,
						colunasPertencentesATabela);				
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Sem argumentos!!");
		}

	}

}
