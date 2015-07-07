package br.com.thiagobarso.teste;

import br.com.thiagobarso.service.SearchInRdf;

public class TesteGetQueryCreateTable {

	private static SearchInRdf search = new SearchInRdf();
	
	public static void main(String[] args) {
		String t = "loa:Exercicio";
		String root = search.getArquivoRdf();
		StringBuffer queryCriacao = new StringBuffer();
		queryCriacao.append(search.getQueryCreateTable(t, root));
		SearchInRdf.executaSql(queryCriacao.toString());
		System.gc();	
	}

}
