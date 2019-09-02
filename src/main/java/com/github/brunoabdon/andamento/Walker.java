package com.github.brunoabdon.andamento;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.brunoabdon.andamento.Objetivo.SubObjetivo;

public class Walker {

	private static final String PATH_REGEXP = 
		"^(\\/?)([a-zA-Z][a-zA-Z0-9]*)((\\/([a-zA-Z][a-zA-Z0-9]*))*(\\/)?)$";

	private static final Pattern PATH_PATTERN = 
		Pattern.compile(PATH_REGEXP);
	
	private Objetivo current;
	private Stack<Objetivo> lineage;
	
	private Map<String,Objetivo> filhos;
	
	public Walker(final Objetivo root) {
		this.current = root;
		this.filhos = 
			root
				.getSubObjetivos()
				.stream()
				.map(SubObjetivo::getObjetivo)
				.collect(toMap(Objetivo::getNome, identity()));
	}
	
	public void cd(final String path) {
		if (".".equals(path)) return;
		
		if("..".equals(path)){
			this.current = lineage.pop();
			//TODO atualizar mapa de filhos
			
		} else {
			final Matcher matcher = PATH_PATTERN.matcher(path);
			if(!matcher.matches()) {
				throw new IllegalArgumentException("O que é " + path + "?");
			}
			
			final String nome = matcher.group(2);
			final Objetivo filho = filhos.get(nome);
			
			if(filho == null) {
				throw new IllegalArgumentException(
					"'" + nome + "'" + " não existe"
				);
			}
			final String restoPath = matcher.group(3);
			if(!"/".equals(restoPath)) {
				
			}
			this.lineage.add(this.current);
			this.current = filho;
		}
	}

	
	public static void main(String[] args) {

		final String path = "/home/aaa/aa";
		
		final Matcher matcher = PATH_PATTERN.matcher(path);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("O que é " + path + "?");
		}
		
		for (int i = 0; i <= matcher.groupCount(); i++) {
			final String nome = matcher.group(i);
			System.out.println(i + " - " + nome);
			
		}
		
	}
	
}
