package br.com.thiagobarso.system;

import java.util.ArrayList;

import br.com.thiagobarso.service.SearchInRdf;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class DoRdf {

	private static SearchInRdf search = new SearchInRdf();

	public static void main(String[] args) {

		String singleroot = "/rdf-timoteo/loa2015/loa2015.nt";
		ArrayList<String> tabelas = search.getTables(singleroot);
		StringBuilder queryCriacao = new StringBuilder();
		for (String t : tabelas) {
			queryCriacao.append(search.getQueryCreateTable(t, singleroot));
			System.out.println("EXECUTANDO SCRIPT DE BANCO: inicio_table: " + t
					+ "=============================");
			SearchInRdf.executaSql(queryCriacao.toString());
			System.out.println("FIM SCRIPT DE BANCO fim_table: " + t
					+ "=============================");
			queryCriacao = new StringBuilder();
		}

	}

}
