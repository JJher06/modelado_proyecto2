package mx.unam.musicdb.compilador;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompiladorBusquedaTest {

    @Test
    void lexerTokenizaCampoValor() {
        Lexer lexer = new Lexer("titulo:love");
        List<Token> tokens = lexer.tokenizar();
        assertEquals(4, tokens.size());
        assertEquals(TipoToken.CAMPO, tokens.get(0).getTipo());
        assertEquals("titulo", tokens.get(0).getLexema());
        assertEquals(TipoToken.DOS_PUNTOS, tokens.get(1).getTipo());
        assertEquals(TipoToken.VALOR, tokens.get(2).getTipo());
        assertEquals("love", tokens.get(2).getLexema());
        assertEquals(TipoToken.FIN, tokens.get(3).getTipo());
    }

    @Test
    void lexerTokenizaConAnd() {
        Lexer lexer = new Lexer("titulo:love,genero:rock");
        List<Token> tokens = lexer.tokenizar();
        long comas = tokens.stream().filter(t -> t.getTipo() == TipoToken.COMA).count();
        assertEquals(1, comas);
        assertEquals(8, tokens.size());
    }

    @Test
    void lexerTokenizaConOr() {
        Lexer lexer = new Lexer("genero:rock/anio:1990");
        List<Token> tokens = lexer.tokenizar();
        long barras = tokens.stream().filter(t -> t.getTipo() == TipoToken.BARRA).count();
        assertEquals(1, barras);
    }

    @Test
    void lexerTokenizaNegacion() {
        Lexer lexer = new Lexer("genero:!rock");
        List<Token> tokens = lexer.tokenizar();
        assertEquals(TipoToken.NEGACION, tokens.get(2).getTipo());
    }

    @Test
    void lexerTokenizaComparacionMayorQue() {
        Lexer lexer = new Lexer("anio>2000");
        List<Token> tokens = lexer.tokenizar();
        assertEquals(4, tokens.size());
        assertEquals(TipoToken.CAMPO, tokens.get(0).getTipo());
        assertEquals(TipoToken.MAYOR_QUE, tokens.get(1).getTipo());
        assertEquals(TipoToken.NUMERO, tokens.get(2).getTipo());
        assertEquals("2000", tokens.get(2).getLexema());
    }

    @Test
    void lexerTokenizaComparacionMayorIgual() {
        Lexer lexer = new Lexer("anio>=1990");
        List<Token> tokens = lexer.tokenizar();
        assertEquals(TipoToken.MAYOR_IGUAL, tokens.get(1).getTipo());
    }

    @Test
    void lexerTokenizaComparacionMenorIgual() {
        Lexer lexer = new Lexer("track<=10");
        List<Token> tokens = lexer.tokenizar();
        assertEquals(TipoToken.MENOR_IGUAL, tokens.get(1).getTipo());
    }

    @Test
    void lexerIgnoraEspacios() {
        Lexer lexer = new Lexer("titulo:  love , genero:  rock");
        List<Token> tokens = lexer.tokenizar();
        assertEquals("love", tokens.get(2).getLexema());
        assertEquals("rock", tokens.get(6).getLexema());
    }

    @Test
    void lexerLanzaErrorCampoDesconocido() {
        assertThrows(RuntimeException.class, () -> {
            new Lexer("xyz:foo").tokenizar();
        });
    }

    @Test
    void parserArbolSimple() {
        Lexer lexer = new Lexer("titulo:love");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        assertEquals(NodoAST.Tipo.HOJA, arbol.getTipo());
    }

    @Test
    void parserArbolAnd() {
        Lexer lexer = new Lexer("titulo:love,genero:rock");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        assertEquals(NodoAST.Tipo.AND, arbol.getTipo());
        NodoAST.Binario bin = (NodoAST.Binario) arbol;
        assertEquals(NodoAST.Tipo.HOJA, bin.getIzquierdo().getTipo());
        assertEquals(NodoAST.Tipo.HOJA, bin.getDerecho().getTipo());
    }

    @Test
    void parserArbolOr() {
        Lexer lexer = new Lexer("genero:rock/anio:1990");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        assertEquals(NodoAST.Tipo.OR, arbol.getTipo());
    }

    @Test
    void parserPrecedenciaAndSobreOr() {
        // titulo:love,genero:rock/anio:1990 → (titulo:love AND genero:rock) OR anio:1990
        Lexer lexer = new Lexer("titulo:love,genero:rock/anio:1990");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        assertEquals(NodoAST.Tipo.OR, arbol.getTipo());
        NodoAST.Binario orNode = (NodoAST.Binario) arbol;
        assertEquals(NodoAST.Tipo.AND, orNode.getIzquierdo().getTipo());
        assertEquals(NodoAST.Tipo.HOJA, orNode.getDerecho().getTipo());
    }

    @Test
    void parserNegacion() {
        Lexer lexer = new Lexer("genero:!rock");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        NodoAST.Hoja hoja = (NodoAST.Hoja) arbol;
        assertEquals(NodoAST.Operador.DISTINTO, hoja.getOperador());
    }

    @Test
    void parserComparacion() {
        Lexer lexer = new Lexer("anio>=2000");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        NodoAST.Hoja hoja = (NodoAST.Hoja) arbol;
        assertEquals(NodoAST.Operador.MAYOR_IGUAL, hoja.getOperador());
        assertEquals(2000, hoja.getValorNumero());
    }

    @Test
    void evaluadorGeneraWhereClause() {
        Lexer lexer = new Lexer("titulo:love");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        EvaluadorConsulta eval = new EvaluadorConsulta();
        EvaluadorConsulta.Resultado res = eval.evaluar(arbol);
        assertTrue(res.getWhereClause().contains("r.title LIKE ?"));
        assertEquals(1, res.getParametros().size());
        assertEquals("%love%", res.getParametros().get(0));
    }

    @Test
    void evaluadorComparacion() {
        Lexer lexer = new Lexer("anio>=2000");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        EvaluadorConsulta eval = new EvaluadorConsulta();
        EvaluadorConsulta.Resultado res = eval.evaluar(arbol);
        assertTrue(res.getWhereClause().contains("r.year >= ?"));
        assertEquals(2000, res.getParametros().get(0));
    }

    @Test
    void evaluadorAndOrCombinados() {
        Lexer lexer = new Lexer("titulo:love,genero:rock/anio:1990");
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        EvaluadorConsulta eval = new EvaluadorConsulta();
        EvaluadorConsulta.Resultado res = eval.evaluar(arbol);
        String sql = res.getWhereClause();
        assertTrue(sql.contains("AND"));
        assertTrue(sql.contains("OR"));
        assertEquals(3, res.getParametros().size());
    }

    @Test
    void flujoCompletoLexerParserEvaluador() {
        String consulta = "titulo:love,genero:!rock/anio>=2000";
        Lexer lexer = new Lexer(consulta);
        Parser parser = new Parser(lexer.tokenizar());
        NodoAST arbol = parser.parsear();
        EvaluadorConsulta eval = new EvaluadorConsulta();
        EvaluadorConsulta.Resultado res = eval.evaluar(arbol);

        assertEquals(NodoAST.Tipo.OR, arbol.getTipo());
        assertTrue(res.getWhereClause().contains("AND"));
        assertTrue(res.getWhereClause().contains("r.title LIKE ?"));
        assertTrue(res.getWhereClause().contains("r.genre != ?"));
        assertTrue(res.getWhereClause().contains("r.year >= ?"));
        assertEquals(3, res.getParametros().size());
    }

    @Test
    void columnaSQLSeleccionaCorrectamente() {
        assertEquals("r.title", Parser.columnaSQL("titulo"));
        assertEquals("p.name", Parser.columnaSQL("performer"));
        assertEquals("a.name", Parser.columnaSQL("album"));
        assertEquals("r.genre", Parser.columnaSQL("genero"));
        assertEquals("r.year", Parser.columnaSQL("anio"));
        assertEquals("r.track", Parser.columnaSQL("track"));
    }
}
