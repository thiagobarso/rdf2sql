package br.com.thiagobarso.system;

import java.util.ArrayList;
import br.com.thiagobarso.service.SearchInRdf;

public class DoRdf {

	private static SearchInRdf search = new SearchInRdf();

	public static void main(String[] args) {
		search.testeConexao();
		try {
			String singleroot = search.getArquivoRdf();
			if (singleroot == null) {
				System.out.print("Endereco do arquivo invalido");
			}
			ArrayList<String> tabelas = search.getTables(singleroot);
			singleroot = null;
			for (String t : tabelas) {
				String root = search.getArquivoRdf();
				StringBuffer queryCriacao = new StringBuffer();
				queryCriacao.append(search.getQueryCreateTable(t, root));
				System.out.println("inicio_criação_tabela: " + t);
				SearchInRdf.executaSql(queryCriacao.toString());
				System.out.println("fim_criação_tabela: " + t);
				root = null;
				queryCriacao = null;
				System.gc();
			}
			for (String t : tabelas) {
				String root = search.getArquivoRdf();
				int countTabela = search.getQueryCountRdf(t, root);				
				System.out.println("Tabela " + t +" contem "  + countTabela + "registros.");
				ArrayList<String> colunasPertencentesATabela = search
						.getColunas(t, root);
				search.getQuerySelectRdf(t, root, colunasPertencentesATabela);
				root = null;
				colunasPertencentesATabela = null;
				System.gc();
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Ops! Algo deu errado");
			throw new RuntimeException(e);
		}

	}

}
