package br.com.thiagobarso.teste;

import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class TesteBanco {

	public String getQuerySelectRdf(String tabela, String singleroot,
			ArrayList<String> colunas) {
		System.out
				.println("=================Começando - getQuerySelectRdf - TESTEBANCO");
		StringBuilder querySqlInsert = new StringBuilder();
		Model model = FileManager.get().loadModel(singleroot);
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX loa: <http://vocab.e.gov.br/2013/09/loa#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "SELECT distinct ?label ?codigo"
				+ " WHERE"
				+ " {"
				+ " ?esfera loa:codigo ?codigo ."
				+ " ?esfera rdfs:label ?label ."
				+ " ?esfera a loa:Esfera ."
				+ " } ";
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
