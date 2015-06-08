package br.com.thiagobarso.service;

import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
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
		return criarTabela(t, colunasPertencentesATabela).equals(null) ? null
				: criarTabela(t, colunasPertencentesATabela).toString();
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
			ArrayList<String> colunasPertencentesATabela) {

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

		return sqlTable;
	}
	
	public String getQuerySelect(String tabela, String singleroot, ArrayList<String> colunas){
		Model model = FileManager.get().loadModel(singleroot);
		ArrayList<String> result = new ArrayList<String>();
		StringBuilder queryString = new StringBuilder(); 
		queryString.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append("PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> ");
		queryString.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		queryString.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append("SELECT distinct ");
		for(String c : colunas){
			queryString.append("?" + c.replace("loa:", ""));
		}
		queryString.append("WHERE { ");
		// TODO query preenchida com valores
		queryString.append("} ");
		Query query = QueryFactory.create(queryString.toString());
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
		//System.out.println("Numero de Propriedades: " + result.size()
			//	+ " Para a tabela " + t);
		return null;
		
	}

}
