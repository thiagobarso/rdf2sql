package br.com.thiagobarso.system;

import java.util.ArrayList;
import java.util.Map;

import br.com.thiagobarso.service.SearchInRdf;

public class DoRdf {

	private static SearchInRdf search = new SearchInRdf();
	private static Map<String,String> config;

	public static void main(String[] args) throws Exception {
		config = search.getProp(args);
		search.testeConexao(config);		
		try {
			String singleroot = search.getArquivoRdf(config);
			if (singleroot == null) {
				System.out.print("Endereco do arquivo invalido");
			}
			ArrayList<String> tabelas = search.getTables(singleroot);
			singleroot = null;
			for (String t : tabelas) {
				String root = search.getArquivoRdf(config);
				StringBuffer queryCriacao = new StringBuffer();
				queryCriacao.append(search.getQueryCreateTable(t, root));
				System.out.println("inicio_criação_tabela: " + t);
				SearchInRdf.executaSql(queryCriacao.toString(),config);
				System.out.println("fim_criação_tabela: " + t);
				root = null;
				queryCriacao = null;
				System.gc();
			}
			for (String t : tabelas) {
				String root = search.getArquivoRdf(config);
				int countTabela = search.getQueryCountRdf(t, root);				
				System.out.println("Tabela " + t +" contem "  + countTabela + "registros.");
				ArrayList<String> colunasPertencentesATabela = search
						.getColunas(t, root);
				search.getQuerySelectRdf(t, root, colunasPertencentesATabela,config);
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
