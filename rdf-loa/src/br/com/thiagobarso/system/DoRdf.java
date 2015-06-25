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
			StringBuilder queryCriacao = new StringBuilder();
			for (String t : tabelas) {
				queryCriacao.append(search.getQueryCreateTable(t, singleroot));
				System.out
						.println("inicio_criação_tabela: "	+ t);
				SearchInRdf.executaSql(queryCriacao.toString());
				System.out.println("fim_criação_tabela: " + t);
				queryCriacao = new StringBuilder();
			}
			for (String t : tabelas) {
				ArrayList<String> colunasPertencentesATabela = search
						.getColunas(t, singleroot);
				queryCriacao.append(search.getQuerySelectRdf(t, singleroot,
						colunasPertencentesATabela));
				String[] valores = queryCriacao.toString().split("end;;");
				queryCriacao = new StringBuilder();
				int i = 0;
				System.out.println("Inserindo valores na tabela: " + t);						
				for (String v : valores) {
					queryCriacao.append(v);
					i++;
					if ((i % 1000) == 0) {
						SearchInRdf.executaSql(queryCriacao.toString());
						queryCriacao = new StringBuilder();
					}
				}
				System.out.println("Inserindo resto de: " + t
						+ " valor de  i: " + i);
				SearchInRdf.executaSql(queryCriacao.toString());
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Sem argumentos!!");
		}

	}

}
