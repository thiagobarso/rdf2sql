package br.com.thiagobarso.teste;

import junit.framework.TestCase;
import br.com.thiagobarso.service.SearchInRdf;

public class TesteGetLoopRegistros extends TestCase{

	private static SearchInRdf search = new SearchInRdf();
	
	public void testGetLoopRegistros() {
		String retornoExperado = "OFFSET 200000OFFSET 400000OFFSET 600000OFFSET 800000";
		StringBuffer retornoObtido = new StringBuffer();
		int count = 817003;	//retorno do count 
		int x = search.getLoopRegistros(count);
		for(int i=1; i <= x; i++){
			if(i > 1){
				retornoObtido.append(search.getOffset(i - 1));
			}
		}		
		assertEquals(retornoExperado, retornoObtido.toString());
	}
}
