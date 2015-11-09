package br.com.thiagobarso.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

import br.com.thiagobarso.system.ConnectionFactory;

public class SearchInRdf {

	public ArrayList<String> getTables(String singleroot) {
		Model model = FileManager.get().loadModel(singleroot);
		ArrayList<String> result = new ArrayList<String>();
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT distinct ?nome "
				+ "WHERE { "
				+ " [] rdf:type ?nome . " + "} ";
		Query query = QueryFactory.create(queryString);
		System.out.println("Pegando Tabelas:");
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				Resource r = soln.getResource("nome");
				if (!(r.getLocalName().equals("Class") || r.getLocalName()
						.equals("Property"))) {
					result.add("loa:" + r.getLocalName());
				}
			}

		}
		System.out.println("Numero de Classes: " + result.size());
		return result;
	}

	public String getQueryCreateTable(String t, String singleroot) {
		ArrayList<String> colunasPertencentesATabela = new ArrayList<String>();
		colunasPertencentesATabela = getColunas(t, singleroot);
		return criarTabela(t, colunasPertencentesATabela, singleroot)
				.toString();
	}

	public ArrayList<String> getColunas(String t, String singleroot) {
		Model model = FileManager.get().loadModel(singleroot);
		ArrayList<String> result = new ArrayList<String>();
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT distinct ?property "
				+ "WHERE { "
				+ " ?i a "
				+ t
				+ " ." + " ?i ?property ?value . " + "} ";
		Query query = QueryFactory.create(queryString);
		System.out.println("Propriedades encontradas para tabela: " + t);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				Resource r = soln.getResource("property");
				if (!(r.getLocalName().equals("type") || r.getLocalName()
						.equals("Property"))) {
					System.out.println("loa:" + r.getLocalName());
					result.add("loa:" + r.getLocalName());
				}
			}

		}
		System.out.println("Numero de Propriedades: " + result.size()
				+ " Para a tabela " + t);
		return result;
	}

	private StringBuffer criarTabela(String t,
			ArrayList<String> colunasPertencentesATabela, String singleroot) {

		StringBuffer sqlTable = new StringBuffer();
		sqlTable.append("CREATE TABLE ");
		sqlTable.append(t.replace("loa:", "").toLowerCase());
		sqlTable.append(" ( ");
		for (String c : colunasPertencentesATabela) {
			sqlTable.append(c.replace("loa:", "").toLowerCase());
			sqlTable.append(" character varying(300) ");
			if (c != colunasPertencentesATabela.get(colunasPertencentesATabela
					.size() - 1)) {
				sqlTable.append(", ");
			}
		}
		sqlTable.append(");");
		return sqlTable;
	}

	public void getQuerySelectRdf(String tabela, String singleroot,
			ArrayList<String> colunas, Map<String, String> config) {
		System.out
				.println("=================Começando - getQuerySelectRdf - tabela:"
						+ tabela);
		StringBuffer querySqlInsert = new StringBuffer();
		querySqlInsert.append(createBeginInsert(tabela, colunas));
		Model model = FileManager.get().loadModel(singleroot);
		StringBuilder queryString = new StringBuilder();
		queryString
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append("PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> ");
		queryString.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		queryString
				.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append("SELECT ");
		for (String c : colunas) {
			queryString.append("?" + c.toLowerCase().replace("loa:", "") + " ");
		}
		queryString.append("WHERE { ");
		for (String c : colunas) {
			queryString.append("?" + tabela.toLowerCase().replace("loa:", ""));
			queryString.append(" ");
			queryString.append(getPredicate(c));
			queryString.append(" ");
			queryString.append(getWhereRdf(c, colunas));
			queryString.append(" ");
			queryString.append(". ");
		}
		queryString.append("?" + tabela.toLowerCase().replace("loa:", "")
				+ " a " + tabela);
		queryString.append(". ");
		queryString.append("} ");
		Query query = QueryFactory.create(queryString.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			ArrayList<String> valores = new ArrayList<String>();
			int i = 0;
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				for (String c : colunas) {
					RDFNode x = soln.get("?"
							+ c.toLowerCase().replace("loa:", ""));
					if (!x.equals(null)) {
						valores.add(x
								.toString()
								.replace(
										"^^http://www.w3.org/2001/XMLSchema#decimal",
										"")
								.replace(
										"^^http://www.w3.org/2001/XMLSchema#integer",
										"")
								.replace(
										"^^http://www.w3.org/2001/XMLSchema#dateTime",
										""));
					}
					x = null;
				}
				querySqlInsert.append(createFinalInsert(valores));
				i++;
				if ((i % 1000) == 0) {
					executaSql(
							querySqlInsert
									.deleteCharAt(querySqlInsert.length() - 1)
									.append(";").toString(), config);
					querySqlInsert.delete(0, querySqlInsert.length());
					querySqlInsert.append(createBeginInsert(tabela, colunas));
					i = 0;
					System.gc();
				}
				valores.clear();
			}
			executaSql(querySqlInsert.deleteCharAt(querySqlInsert.length() - 1)
					.append(";").toString(), config);
			System.gc();
		}
		System.out
				.println("=================Terminando - getQuerySelectRdf - tabela: "
						+ tabela);
	}

	public String getOffset(int offset) {
		return "OFFSET " + offset * 200000;
	}

	public String getWhereRdf(String c, ArrayList<String> colunas) {
		StringBuilder queryString = new StringBuilder();
		if (c.equals("loa:temIdentificadorUso") || c.equals("loa:temOrgao")
				|| c.equals("loa:temModalidadeAplicacao")
				|| c.equals("loa:temUnidadeOrcamentaria")
				|| c.equals("loa:temSubfuncao") || c.equals("loa:temFuncao")
				|| c.equals("loa:temPrograma")
				|| c.equals("loa:temFonteRecursos") || c.equals("loa:temGND")
				|| c.equals("loa:temCategoriaEconomica")
				|| c.equals("loa:temElementoDespesa")
				|| c.equals("loa:temPlanoOrcamentario")
				|| c.equals("loa:temResultadoPrimario")
				|| c.equals("loa:temAcao") || c.equals("loa:temEsfera")
				|| c.equals("loa:temSubtitulo")) {
			queryString.append("?" + colunas.indexOf(c) + ". ");
			queryString.append("?" + colunas.indexOf(c) + " a " + getNameLOA(c)
					+ " . ");
			queryString.append("?" + colunas.indexOf(c) + " loa:codigo " + "?"
					+ c.toLowerCase().replace("loa:", ""));
		}
		if (c.equals("loa:temExercicio")) {
			queryString.append("?" + colunas.indexOf(c) + ". ");
			queryString.append("?" + colunas.indexOf(c) + " a " + getNameLOA(c)
					+ " . ");
			queryString.append("?" + colunas.indexOf(c) + " loa:identificador "
					+ "?" + c.toLowerCase().replace("loa:", ""));
		}
		if (!(c.equals("loa:temIdentificadorUso") || c.equals("loa:temOrgao")
				|| c.equals("loa:temExercicio")
				|| c.equals("loa:temModalidadeAplicacao")
				|| c.equals("loa:temUnidadeOrcamentaria")
				|| c.equals("loa:temSubfuncao") || c.equals("loa:temFuncao")
				|| c.equals("loa:temPrograma")
				|| c.equals("loa:temFonteRecursos") || c.equals("loa:temGND")
				|| c.equals("loa:temCategoriaEconomica")
				|| c.equals("loa:temElementoDespesa")
				|| c.equals("loa:temPlanoOrcamentario")
				|| c.equals("loa:temResultadoPrimario")
				|| c.equals("loa:temAcao") || c.equals("loa:temEsfera") || c
					.equals("loa:temSubtitulo"))) {
			queryString.append("?" + c.toLowerCase().replace("loa:", "") + " ");
		}
		return queryString.toString();
	}

	public String getNameLOA(String c) {
		if (c.equals("loa:temGND")) {
			return "loa:GrupoNatDespesa";
		} else {
			return c.replace("loa:tem", "loa:");
		}
	}

	public String getPredicate(String c) {
		StringBuilder query = new StringBuilder();
		if (c.equals("loa:codigo") || c.equals("loa:identificador")
				|| c.equals("loa:temOrgao")
				|| c.equals("loa:temIdentificadorUso")
				|| c.equals("loa:valorLeiMaisCredito")
				|| c.equals("loa:temModalidadeAplicacao")
				|| c.equals("loa:temUnidadeOrcamentaria")
				|| c.equals("loa:temSubfuncao")
				|| c.equals("loa:valorLiquidado") || c.equals("loa:temFuncao")
				|| c.equals("loa:valorDotacaoInicial")
				|| c.equals("loa:temPrograma")
				|| c.equals("loa:temFonteRecursos") || c.equals("loa:temGND")
				|| c.equals("loa:temCategoriaEconomica")
				|| c.equals("loa:temElementoDespesa")
				|| c.equals("loa:valorEmpenhado")
				|| c.equals("loa:temPlanoOrcamentario")
				|| c.equals("loa:valorProjetoLei") || c.equals("loa:valorPago")
				|| c.equals("loa:temResultadoPrimario")
				|| c.equals("loa:temAcao") || c.equals("loa:temExercicio")
				|| c.equals("loa:temEsfera") || c.equals("loa:temSubtitulo")
				|| c.equals("loa:dataUltimaAtualizacao")

		) {
			query.append(c);
		}
		if (c.equals("loa:label")) {
			query.append("rdfs:label");
		}
		return query.toString();
	}

	public StringBuffer createBeginInsert(String tabela,
			ArrayList<String> colunas) {
		StringBuffer querySqlInsert = new StringBuffer();
		querySqlInsert.append("INSERT INTO "
				+ tabela.replace("loa:", "").toLowerCase() + "(");
		for (String c : colunas) {
			querySqlInsert.append(c.replace("loa:", "").toLowerCase());
			if (!(c.equals(colunas.get(colunas.size() - 1)))) {
				querySqlInsert.append(", ");
			}
		}
		querySqlInsert.append(") VALUES ");
		return querySqlInsert;
	}

	public String createFinalInsert(ArrayList<String> valores) {
		StringBuilder querySqlInsert = new StringBuilder();
		querySqlInsert.append("(");
		int i = 0;
		for (String v : valores) {
			querySqlInsert.append("'" + v.replace("'", "''") + "'");
			if (i == valores.size() - 1) {
				querySqlInsert.append("),");
			} else {
				querySqlInsert.append(",");
			}
			i++;
		}
		return querySqlInsert.toString();
		// teste
	}

	public static void executaSql(String sql, Map<String, String> config) {
		// conectando
		Connection con = new ConnectionFactory().getConnection(config);

		// cria um preparedStatement
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.execute();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.print("."); // Gravado no banco de dados.
	}

	public void testeConexao(Map<String, String> config) {
		System.out.println("Teste de conexao com banco");
		// conectando
		Connection con = new ConnectionFactory().getConnection(config);

		// cria um preparedStatement
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement("SELECT 1");
			stmt.execute();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Conectado com sucesso!");
	}

	public Map<String, String> getProp(String[] args) throws Exception {
		Map<String, String> configuracao = new HashMap<String, String>();
		if (args.length > 0) {
			for (String arg : args) {
				if (arg.contains("--help")) {
					System.out.println(".::RDF2RDB::.");
					System.out.println("To run execute:");
					System.out.println(" ");
					System.out
							.println("java -jar -Xmx<number_max_memory>m /path/to/rdftorbd.jar --prop.config.file=/path/to/file.nt --prop.config.host=<host> --prop.config.user=<user> --prop.config.password=<password> --prop.config.database=<database>");
					System.out.println(" ");
					System.out.println("Args opcionals:");
					System.out.println("--prop.config.port=<port>");
					System.out.println(" ");
					System.out.println("Args required:");
					System.out.println("--prop.config.user=<user_of_database>");
					System.out.println("--prop.config.password=<password>");
					System.out.println("--prop.config.database=<database>");
					System.out.println("--prop.config.file=<file>");

					System.exit(0);
				}
				if (arg.contains("--prop.config.host")
						|| arg.contains("--prop.config.user")
						|| arg.contains("--prop.config.password")
						|| arg.contains("--prop.config.database")
						|| arg.contains("--prop.config.port")
						|| arg.contains("--prop.config.file")) {
					try {
						String[] argumentosValor = arg.split("=");
						String valor = argumentosValor[1];
						String[] argumentosChave = argumentosValor[0]
								.split("\\.");
						String chave = argumentosChave[2];
						configuracao.put(chave, valor);
					} catch (Exception e) {
						System.err.println("Something wrong with the args!");
						e.printStackTrace();
						throw new Exception();
					}
				}
			}
		} else {
			System.out.println(".::RDF2RDB::.");
			System.out.println("Execute:");
			System.out.println(" ");
			System.out.println("java -jar /path/to/rdftorbd.jar --help");
			System.out.println(" ");
			System.out.println("to help. ");
			System.exit(0);
		}

		String keyToSearch = "port";
		if (!configuracao.containsKey(keyToSearch)) {
			String valorPadrao = "5432";
			configuracao.put("port", valorPadrao);
		}

		for (String key : configuracao.keySet()) {
			String value = configuracao.get(key);
			System.out.println(key + " = " + value);
		}

		return configuracao;

	}

	public String getArquivoRdf(Map<String, String> config) {
		return config.get("file");
	}

	public int getQueryCountRdf(String tabela, String root) {
		int retorno = 0;
		Model model = FileManager.get().loadModel(root);
		StringBuilder queryString = new StringBuilder();
		queryString
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append("PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> ");
		queryString.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		queryString
				.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append("SELECT (COUNT(");
		queryString.append("?" + tabela.toLowerCase().replace("loa:", ""));
		queryString.append(") as ?total) ");
		queryString.append("WHERE { ");
		queryString.append("?" + tabela.toLowerCase().replace("loa:", ""));
		queryString.append(" rdf:type ");
		queryString.append(tabela + ".");
		queryString.append("}");
		Query query = QueryFactory.create(queryString.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("?total");
				retorno = (int) Integer.parseInt(x.toString().replace(
						"^^http://www.w3.org/2001/XMLSchema#integer", ""));
			}
		}
		return retorno;
	}

	public int getLoopRegistros(int i) {
		int divisao = i / 200000;
		int modulo = i % 200000;
		int retorno;
		if (modulo > 0) {
			retorno = divisao + 1;
		} else {
			retorno = divisao;
		}
		return retorno;
	}
}
