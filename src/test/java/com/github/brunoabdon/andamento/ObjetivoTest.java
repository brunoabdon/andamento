package com.github.brunoabdon.andamento;

import static org.hipparchus.fraction.Fraction.ONE_FIFTH;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hipparchus.fraction.Fraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Testes de Objetivo")
class ObjetivoTest {

    @NullSource
    @ValueSource(strings = {"   ","","\t\t  "})
    @ParameterizedTest(name = "Deve dar NPE se nome for \"{0}\"")
    @DisplayName("Deve dar NPE se nome for null ou blank,  no construtor")
    void testNPENullName(final String nome) {
        //acao 
        final Executable acao = () -> new Objetivo(nome);
        
        //verificacao
        assertThrows(IllegalArgumentException.class, acao);
        
    }

    @DisplayName("Deve criar objetivo certinho")
    @ParameterizedTest(name = "Deve criar objetivo chamado {0}.")
    @ValueSource(strings = {"parte1","parte2","parte3","boo aaa aa"})
    public void testCriaComNome(final String nome)  {
        //acao
        final Objetivo obj = new Objetivo(nome);
        
        //verificao
        assertThat(
            obj, 
            allOf(
                hasProperty("nome",is(nome)),
                hasProperty("subObjetivos",empty())
            )
        );
    }
    
    
    @Nested
    @DisplayName("Testes com objetivo criado")
    class ObjetivoTestCriadoOk{
        private Objetivo obj;
        
        @BeforeEach
        private void init() {
            this.obj = new Objetivo("root");
        }
        
        @Test
        @DisplayName("Deve dar NPE com divisao nula")
        private void testeDivisaoOriginal() {
            //acao
            final Executable acao = () -> this.obj.dividir((Fraction[])null);
            
            //verificacao
            assertThrows(NullPointerException.class, acao);
        }
        
        @Test
        private void testeDivisao() {
            
            final Fraction[] ratios = {
                ONE_FIFTH,ONE_FIFTH,ONE_FIFTH,ONE_FIFTH,ONE_FIFTH
            };
            
            this.obj.dividir(ratios);
            
            assertThat(
                this.obj, 
                hasProperty("subObjetivos",hasSize(ratios.length))
            );

        }
        
        
    }
    
    

    
}
