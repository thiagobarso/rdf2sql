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
import com.hp.hpl.jena.util.FileManager;

import br.com.thiagobarso.service.SearchInRdf;

public class TesteGetQuerySelectRdf {

	private static SearchInRdf search = new SearchInRdf();

	public static void main(String[] args) {
		String t = "loa:ItemDespesa";
		String root = search.getArquivoRdf();
		ArrayList<String> colunasPertencentesATabela = getColunas();
		int countTabela = search.getQueryCountRdf(t, root);
		int loopRegistros = search.getLoopRegistros(countTabela);
		if (loopRegistros > 0) {
			for (int i = 1; i <= loopRegistros; i++) {
				search.getQuerySelectRdf(t, root, colunasPertencentesATabela, i);
				System.gc();
			}
		} else {
			search.getQuerySelectRdf(t, root, colunasPertencentesATabela, 0);
			System.gc();
		}		
	}

	private static ArrayList<String> getColunas() {
		ArrayList<String> retorno = new ArrayList<String>();
		retorno.add("loa:temIdentificadorUso");
		retorno.add("loa:temModalidadeAplicacao");
		retorno.add("loa:valorLeiMaisCredito");
		retorno.add("loa:temSubtitulo");
		retorno.add("loa:temElementoDespesa");
		retorno.add("loa:temFuncao");
		retorno.add("loa:temPrograma");
		retorno.add("loa:valorLiquidado");
		retorno.add("loa:valorDotacaoInicial");
		retorno.add("loa:temPlanoOrcamentario");
		retorno.add("loa:temGND");
		retorno.add("loa:temCategoriaEconomica");
		retorno.add("loa:valorEmpenhado");
		retorno.add("loa:temSubfuncao");
		retorno.add("loa:temAcao");
		retorno.add("loa:valorProjetoLei");
		retorno.add("loa:temFonteRecursos");
		retorno.add("loa:valorPago");
		retorno.add("loa:temUnidadeOrcamentaria");
		retorno.add("loa:temResultadoPrimario");
		retorno.add("loa:temExercicio");
		retorno.add("loa:temEsfera");
		return retorno;
	}

	public static void getQuerySelectRdfCount(String tabela, String singleroot,
			ArrayList<String> colunas, int offset) {
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
			queryString.append(search.getPredicate(c));
			queryString.append(" ");
			queryString.append(search.getWhereRdf(c, colunas));
			queryString.append(" ");
			queryString.append(". ");
		}
		queryString.append("?" + tabela.toLowerCase().replace("loa:", "")
				+ " a " + tabela);
		queryString.append(". ");
		queryString.append("} ");
		queryString.append("LIMIT 200000 ");
		if (offset > 1) {
			queryString.append(search.getOffset(offset - 1));
		}
		Query query = QueryFactory.create(queryString.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			int i = 0;
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				i++;
			}
			System.out.println("Valores:" + i);
		}
	}
}
