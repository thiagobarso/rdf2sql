package br.com.thiagobarso.system;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class RdfLoa {

	public static void main(String[] args) {

		ArrayList<String> tabelas = new ArrayList<String>();
		ArrayList<String> colunas = new ArrayList<String>();

		FileReader arq;
		try {
			arq = new FileReader("/rdf-timoteo/mapping_loa.ttl");
			BufferedReader lerArq = new BufferedReader(arq);
			// lê a primeira linha
			String linha = lerArq.readLine();
			// a variável "linha" recebe o valor "null" quando o processo
			// de repetição atingir o final do arquivo texto
			while (linha != null) {
				// se tabela
				if (linha.contains("map:") && linha.contains("d2rq:ClassMap")) {
					String linhaTabela = linha.substring(
							linha.indexOf("map:") + 4,
							linha.indexOf("a d2rq:ClassMap;"));
					System.out.printf("%s\n", linhaTabela);
					tabelas.add(linhaTabela);

				}
				// se coluna

				if (linha.contains("map:")
						&& linha.contains("d2rq:PropertyBridge")) {
					String linhaColuna = linha.substring(
							linha.indexOf("map:") + 4,
							linha.indexOf("a d2rq:PropertyBridge;"));
					System.out.printf("%s\n",
							linhaColuna.replaceFirst("_", " "));
					colunas.add(linhaColuna.replaceFirst("_", " "));
				}

				linha = lerArq.readLine(); // lê da segunda até a última linha
			}

			arq.close();
		} catch (IOException e) {
			System.err.printf("Erro na abertura do arquivo: %s.\n",
					e.getMessage());
		}

		System.out.println("Numero de colunas: " + colunas.size());
		System.out.println("Numero de tabelas: " + tabelas.size());
		System.out.println();

		System.out
				.println("==============GERANDO SCRIPT DE BANCO===============");

		StringBuilder sql = geraScriptSQL(tabelas, colunas);

		System.out
				.println("==============EXECUTANDO SCRIPT DE BANCO===============");

		//executaSql(sql.toString());

		System.out
				.println("==============PESQUISANDO E SALVANDO CONTEUDO COM APACHE JENA===============");

		//gerarSparql(tabelas, colunas);

	}

	private static void gerarSparql(ArrayList<String> tabelas,
			ArrayList<String> colunas) {

		System.out.println("Começando -- função gerarSparql");
		ArrayList<String> colunasPertencentesATabela = new ArrayList<String>();

		for (String t : tabelas) {
			for (String c : colunas) {
				if (c.contains(t)) {
					colunasPertencentesATabela.add(c.substring(
							c.indexOf(t) + t.length(), c.length()).replace("_",
							""));
				}
			}
			pesquisarSparql(t, colunasPertencentesATabela);
			colunasPertencentesATabela.clear();
		}

	}

	private static void pesquisarSparql(String t,
			ArrayList<String> colunasPertencentesATabela) {

		StringBuilder query = new StringBuilder();

		query.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		query.append("PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> ");
		query.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		query.append("SELECT DISTINCT ");
		for (String c : colunasPertencentesATabela) {
			query.append("?" + c.toLowerCase() + " ");
		}
		query.append(" WHERE {");
		for (String c : colunasPertencentesATabela) {
			query.append("?" + t.toLowerCase() + " ");
			if(c.contains("codigo")){
				query.append("loa:codigo");
			}
			if(c.matches("_cod(_[a-zA-Z]*)*")){
				query.append("loa:codigo");
			}if(c.matches("(_[a-zA-Z]*)_cod")){
				query.append("loa:codigo");
			}
			query.append(" .");
			
		}
		query.append("}");

	}

	private static void executaSql(String sql) {
		// conectando
		Connection con = new ConnectionFactory().getConnection();

		// cria um preparedStatement
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.execute();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Gravado!");
	}

	private static StringBuilder geraScriptSQL(ArrayList<String> tabelas,
			ArrayList<String> colunas) {
		System.out.println("Começando -- função geraScriptSQL");
		StringBuilder sql = new StringBuilder();

		ArrayList<String> colunasPertencentesATabela = new ArrayList<String>();

		for (String t : tabelas) {
			for (String c : colunas) {
				if (c.contains(t)) {
					colunasPertencentesATabela.add(c.substring(
							c.indexOf(t) + t.length(), c.length()).replace("_",
							""));
				}
			}
			sql.append(criarTabela(t, colunasPertencentesATabela));
			colunasPertencentesATabela.clear();
		}

		return sql;

	}

	private static StringBuilder criarTabela(String t,
			ArrayList<String> colunasPertencentesATabela) {

		StringBuilder sqlTable = new StringBuilder();
		sqlTable.append("CREATE TABLE ");
		sqlTable.append(t);
		sqlTable.append("(");
		for (String c : colunasPertencentesATabela) {
			sqlTable.append(c);
			sqlTable.append("character varying(300)");
			if (c != colunasPertencentesATabela.get(colunasPertencentesATabela
					.size() - 1)) {
				sqlTable.append(", ");
			}
		}
		sqlTable.append(");");

		return sqlTable;
	}
}
