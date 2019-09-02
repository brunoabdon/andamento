package com.github.brunoabdon.andamento;

import static org.hipparchus.fraction.Fraction.ONE;
import static org.hipparchus.fraction.Fraction.ZERO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.hipparchus.fraction.Fraction;

public class Objetivo {

    public class SubObjetivo {
        private final Objetivo objetivo;
        private Fraction ratio;
        
        public SubObjetivo(final String nome, final Fraction ratio) {
            this.objetivo = new Objetivo(nome);
            this.ratio = ratio;
        }

        public Objetivo getObjetivo() {
            return objetivo;
        }
        
        public Fraction getRatio() {
            return ratio;
        }
        private void setRatio(final Fraction ratio) {
            this.ratio = ratio;
        }
        @Override
        public String toString() {
            return this.objetivo.toString() + " (" + this.ratio + ")";
        }
    }

    private String nome;
    private List<SubObjetivo> subObjetivos;

    // TODO tornar construtores privados e criar um builder
    public Objetivo(final String nome) {
        validaNome(nome);
        this.nome = nome;
        this.subObjetivos = new ArrayList<>();
    }

    public void dividir(final Fraction ... ratios) {
        if(!this.subObjetivos.isEmpty()){
            throw new IllegalArgumentException("Já existe uma divisão.");
        }
        this.validaParticao(ratios);
        this.divide(0, ratios);
    }
    
    /**
     * Divide uma dada parte em várias outras, de acordo com as proporções 
     * passadas.   
     * <p>O primeiro elemento da lista diz qual fração de seu tamanho original a
     * parte vai manter. Os elementos seguintes dizem o a proporção que cada 
     * nova parte terá, também em relação ao tamanho original.</p>
     * 
     * @param subObjetivo A parte a ser dividida.
     * @param ratios A lista das proporções do tamanho original da parte que 
     * vão ter a parte original e as novas partes.
     * 
     * @throws IllegalArgumentException Se a parte passada não for uma das
     * {@linkplain #getSubObjetivos() subpartes} desta, ou se a lista de ratios
     * não somar 1.
     * @throws NullPointerException se algum parâmetro passado for {@code 
     * null}. 
     */
    public void dividir(
            final SubObjetivo subObjetivo, 
            final Fraction ... ratios) {
        
        this.validaPaternidade(subObjetivo);
        
        this.validaParticao(ratios);

        final Fraction ratioOriginal = subObjetivo.getRatio();
        subObjetivo.setRatio(ratioOriginal.multiply(ratios[0]));

        this.divide(1, ratios);
    }

    private void validaPaternidade(final SubObjetivo subObjetivo) {
        if(!tenho(subObjetivo)) {
            throw new IllegalArgumentException(
                "Não é um subobjetivo meu: " + subObjetivo
            );
        }
    }

    /**
     * Faz uma parte engolir a outra, pegando sua fração de divisão. A parte
     * engolida morre.
     * 
     * <p><b>Importante:</b> Se só tiverem duas parte, e uma comer a outra, 
     * a parte única que sobra vai deixar de existir: é uma {@linkplain 
     * #fundir() fusão} do todo.<p>
     * 
     * @param eaten A parte que vai ser engolida.
     * @param eater A parte que vai comer a outra.
     */
    public void engolir(final SubObjetivo eaten, final SubObjetivo eater) {
        this.validaPaternidade(eater);
        this.validaPaternidade(eaten);
        
        if(this.subObjetivos.size() == 2) this.fundir();
        
        eater.setRatio(eater.getRatio().add(eaten.getRatio()));
        this.subObjetivos.remove(eaten);
    }

    private boolean tenho(final SubObjetivo eater) {
        // TODO pensar numa estrutura mais barata pra buscar  
        return this.subObjetivos.contains(eater);
    }
    
    /**
     * Elimina a divisão em partes.
     */
    public void fundir() {
        this.subObjetivos.clear();
    }
    
    
    private void validaParticao(final Fraction... ratios) {
        if(!ehParticao(ratios)) {
            throw new IllegalArgumentException("A conta não fecha.");
        }
    }

    private void divide(final int skip, final Fraction... ratios) {
        final String prefix = this.nome + "_"; 
        Stream
            .of(ratios)
            .skip(skip)
            .map(r -> new SubObjetivo(prefix + r, r))
            .forEach(this.subObjetivos::add);
    }

    public String getNome() {
        return nome;
    }
    
    public void setNome(final String nome) {
        validaNome(nome);
        this.nome = nome;
    }

    public static void validaNome(final String nome) {
        if(nome == null || nome.trim().isEmpty()) 
            throw new IllegalArgumentException("Nome nulo");
    }
    
    public List<SubObjetivo> getSubObjetivos() {
        return subObjetivos;
    }

    /**
     * Diz se uma lista de {@link Fraction}s soma 1.
     * @param ratios Uma lista de {@link Fraction}s.
     * @return {@code true} se e somente sim a soma dos {@link Fraction}s der 
     * 1.
     */
    boolean ehParticao(final Fraction... ratios) {
        return somatorio(ratios).compareTo(ONE) == 0;
    }

    private static Fraction somatorio(final Fraction... ratios) {
        return Stream.of(ratios).reduce(ZERO, Fraction::add);
    }
}
