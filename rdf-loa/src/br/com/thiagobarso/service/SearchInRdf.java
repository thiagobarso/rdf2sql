package br.com.thiagobarso.service;

import java.util.ArrayList;

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
		System.out.println("Função: getTables()");
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				Resource r = soln.getResource("nome");
				if (!(r.getLocalName().equals("Class") || r.getLocalName()
						.equals("Property"))) {
					System.out.println("loa:" + r.getLocalName());
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
		return criarTabela(t, colunasPertencentesATabela, singleroot).equals(null) ? null
				: criarTabela(t, colunasPertencentesATabela, singleroot).toString();
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

	private StringBuilder criarTabela(String t,
			ArrayList<String> colunasPertencentesATabela, String singleroot) {

		StringBuilder sqlTable = new StringBuilder();
		sqlTable.append("CREATE TABLE ");
		sqlTable.append(t);
		sqlTable.append(" ( ");
		for (String c : colunasPertencentesATabela) {
			sqlTable.append(c);
			sqlTable.append(" character varying(300) ");
			if (c != colunasPertencentesATabela.get(colunasPertencentesATabela
					.size() - 1)) {
				sqlTable.append(", ");
			}
		}
		sqlTable.append(");");
		sqlTable.append(getQuerySelectRdf(t,singleroot,colunasPertencentesATabela));

		return sqlTable;
	}
	
	public String getQuerySelectRdf(String tabela, String singleroot, ArrayList<String> colunas){
		System.out.println("=================Começando - getQuerySelectRdf");
		StringBuilder querySqlInsert = new StringBuilder();
		Model model = FileManager.get().loadModel(singleroot);
		StringBuilder queryString = new StringBuilder(); 
		queryString.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append("PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> ");
		queryString.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		queryString.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append("SELECT distinct ");
		for(String c : colunas){
			queryString.append("?" + c.toLowerCase().replace("loa:", "") + " ");
		}
		queryString.append("WHERE { ");
		for(String c : colunas){
			queryString.append("?" + tabela.toLowerCase().replace("loa:", ""));
			queryString.append(" ");
			queryString.append(getPredicate(c));
			queryString.append(" ");
			queryString.append("?" + c.toLowerCase().replace("loa:",""));
			queryString.append(" ");
			queryString.append(". ");			
		}
		queryString.append("?" + tabela.toLowerCase().replace("loa:", "") + " a " + tabela);
		queryString.append(". ");
		queryString.append("} ");
		Query query = QueryFactory.create(queryString.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			ArrayList<String> valores = new ArrayList<String>();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				for (String c : colunas) {
					RDFNode x = soln.get("?" + c.replace("loa:", ""));
					if(!x.equals(null)){
						System.out.println("RDF NODE: " + x.toString());					
						valores.add(x.toString());			
					}					
				}
				querySqlInsert.append(createInsertSql(tabela,colunas, valores));
				valores.clear();
			}
		}
		System.out.println("=================Terminando - getQuerySelectRdf");
		return querySqlInsert.toString();		
	}

	public String getPredicate(String c) {
		StringBuilder query = new StringBuilder();
		if(c.equals("loa:codigo")){
			query.append(c);
		}if(c.equals("loa:label")){
			query.append("rdfs:label");
		}
		return query.toString();
	}

	public String createInsertSql(String tabela, ArrayList<String> colunas,
			ArrayList<String> valores) {
		StringBuilder querySqlInsert = new StringBuilder();
		querySqlInsert
				.append("INSERT INTO " + tabela.replace("loa:", "") + "(");
		for (String c : colunas) {
			querySqlInsert.append(c.replace("loa:", ""));
			if (!(c.equals(colunas.get(colunas.size() - 1)))) {
				querySqlInsert.append(", ");
			}
		}
		querySqlInsert.append(") VALUES (");
		for(String v : valores){
			querySqlInsert.append("'" + v + "'");
			if(!(v.equals(valores.get(valores.size() - 1)))){
				querySqlInsert.append(", ");
			}
		}
		querySqlInsert.append("); ");
		return querySqlInsert.toString();
	}
	
}
