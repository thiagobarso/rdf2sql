package br.com.thiagobarso.system;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import br.com.thiagobarso.service.SearchInRdf;

public class DoRdf {

	private static SearchInRdf search = new SearchInRdf();

	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream("./dados.properties");
		props.load(file);
		return props;
	}

	public static void main(String[] args) {
		search.testeConexao();
		try {
			String singleroot = getArquivoRdf();
			if (singleroot == null) {
				System.out.print("Endereco do arquivo invalido");
			}
			ArrayList<String> tabelas = search.getTables(singleroot);
			StringBuffer queryCriacao = new StringBuffer();
			for (String t : tabelas) {
				queryCriacao.append(search.getQueryCreateTable(t, singleroot));
				System.out.println("inicio_criação_tabela: " + t);
				SearchInRdf.executaSql(queryCriacao.toString());
				System.out.println("fim_criação_tabela: " + t);
				queryCriacao.delete(0, queryCriacao.length() - 1);
			}
			for (String t : tabelas) {
				String root = getArquivoRdf();
				ArrayList<String> colunasPertencentesATabela = search
						.getColunas(t, root);
				search.getQuerySelectRdf(t, root,
						colunasPertencentesATabela);
				System.gc();
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Ops! Algo deu errado");
			throw new RuntimeException(e);
		}

	}

	private static String getArquivoRdf() {
		String arquivoRdf = new String();
		try {
			Properties prop = DoRdf.getProp();
			arquivoRdf = prop.getProperty("prop.server.arquivo.rdf");
		} catch (IOException e) {
			System.out
					.println("Ops!Algo deu errado com o nome da localização do arquivo rdf! ");
			System.out.println("Reveja o arquivo dados.properties");
			e.printStackTrace();
		}
		return arquivoRdf;
	}

}
