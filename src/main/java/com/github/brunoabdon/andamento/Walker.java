package com.github.brunoabdon.andamento;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.hipparchus.fraction.Fraction.ONE_HALF;
import static org.hipparchus.fraction.Fraction.ONE_QUARTER;
import static org.hipparchus.fraction.Fraction.ONE_THIRD;
import static org.hipparchus.fraction.Fraction.THREE_QUARTERS;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.brunoabdon.andamento.Objetivo.SubObjetivo;

public class Walker {

    private static final String PATH_REGEXP =  
        "^(\\/)\\/*|(\\/?)\\/*(([a-z]+)\\/*((\\/*[a-z]+\\/*)*))$";

    private static final Pattern PATH_PATTERN = Pattern.compile(PATH_REGEXP);

	private Objetivo current;
	private Stack<Objetivo> lineage;
	
	private Map<String,Objetivo> filhos;
	
	public Walker(final Objetivo root) {
		this.lineage = new Stack<>();
		this.setaCurrent(root);
	}

	public Objetivo getCurrent() {
        return current;
    }
	
    private void setaCurrent(final Objetivo objetivo) {
        this.current = objetivo;
        this.filhos = 
			objetivo
				.getSubObjetivos()
				.stream()
				.map(SubObjetivo::getObjetivo)
				.collect(toMap(Objetivo::getNome, identity()));
		this.lineage.push(this.current);
    }
	
	public Walker cd(final String path) {
	    
		if (".".equals(path)) return this;
		
		if("..".equals(path)){
		    if(lineage.size() == 1) return this;
		    
		    lineage.pop();
			final Objetivo parent = lineage.pop();
            this.setaCurrent(parent);
			
		} else {
			final Matcher matcher = PATH_PATTERN.matcher(path);
			if(!matcher.matches()) {
				throw new IllegalArgumentException("O que é " + path + "?");
			}
			
			if(matcher.group(1) != null) {
			    // cd /
			    cdRoot();
			} else {
			    if(!matcher.group(2).isEmpty()) {
			        cdRoot();
			    }
	            final String nome = matcher.group(4);
	            final Objetivo filho = filhos.get(nome);

	            if(filho == null) {
	                throw new IllegalArgumentException(
	                    "'" + nome + "'" + " não existe"
	                );
	            }
	            
	            setaCurrent(filho);
	            
	            final String resto = matcher.group(5);
	            if(!resto.isEmpty()) {
	                cd(resto);
	            }
	                
			}
		}
		return this;
	}

    public Walker cdRoot() {
        final Objetivo root = this.lineage.firstElement();
        this.lineage.clear();
        this.setaCurrent(root);
        return this;
    }

    public String pwd() {
        return 
            this.lineage.stream().map(Objetivo::getNome).collect(joining("/"));
    }
    
	public static void main3(String[] args) {

		final List<String> paths = 
	        asList(
	            "",
                "/",
                "/home",
                "/home/",
                "home",
                "/home/bruno",
                "/home/bruno/",
                "///",
                "////home",
                "///home/",
                "home",
                "///home/bruno",
                "/home////bruno/",
                "////home/bruno/pasta",
                "/home/bruno/pasta/pd",
                "home/bruno/pasta/pd"
            );
		
		
		paths.forEach(Walker::tenta);
		
		
	}

    public static void tenta(final String path) {
        final Matcher matcher = PATH_PATTERN.matcher(path);
		if(!matcher.matches()) {
			System.out.println(path + ": Não deu.");
		} else {
		    System.out.println(path +":");
	        for (int i = 0; i <= matcher.groupCount(); i++) {
	            final String nome = matcher.group(i);
	            
	            System.out.println("\t" + i + " - " + nome);
	            
	        }
		}
    }
	
    
    public static void main(String[] args) {
        final Objetivo home = new Objetivo("home");
        home.dividir(ONE_HALF,ONE_HALF);
        final List<SubObjetivo> lsHome = home.getSubObjetivos();
        final Objetivo bruno = lsHome.get(0).getObjetivo();
        bruno.setNome("bruno");
        bruno.dividir(ONE_THIRD,ONE_THIRD,ONE_THIRD);
        bruno.getSubObjetivos().get(0).getObjetivo().setNome("pasta");
        bruno.getSubObjetivos().get(1).getObjetivo().setNome("folder");
        bruno.getSubObjetivos().get(2).getObjetivo().setNome("ficher");
        
        lsHome.get(1).getObjetivo().setNome("guest");
        
        final Walker w = new Walker(home);
        
        
        bruno.dividir(bruno.getSubObjetivos().get(1), ONE_QUARTER,THREE_QUARTERS);
        printa("\t", home);
        
        System.out.println("-------------------------");
        bruno.engolir(
            bruno.getSubObjetivos().get(3), bruno.getSubObjetivos().get(1)
        );
        bruno.engolir(
            bruno.getSubObjetivos().get(2), bruno.getSubObjetivos().get(1)
        );
        
        
        
        printa("\t", home);
        
        
        
        
        
        
        w.cd("bruno/folder");
        System.out.println(w.pwd());
        w.cd("..");
        w.cd("pasta");
        System.out.println(w.pwd());
        
        
        
        
        
    }
    
    
    private static void printa(final String tab, final Objetivo objetivo) {
        System.out.println(tab + objetivo);
        for (final SubObjetivo s : objetivo.getSubObjetivos()) {
            
            System.out.println(tab + tab + s.getRatio());
            printa(tab + tab,s.getObjetivo());
        }

    }
    
    
    
}
