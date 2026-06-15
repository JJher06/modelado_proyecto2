package mx.unam.musicdb.compilador;

import mx.unam.musicdb.compilador.NodoAST.*;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public NodoAST parsear() {
        NodoAST arbol = disyuncion();
        if (tokens.get(pos).getTipo() != TipoToken.FIN)
            throw new RuntimeException("Tokens extra despues de expresion completa en posicion "
                    + tokens.get(pos).getPosicion());
        return arbol;
    }

    // disyuncion → conjuncion ('/' conjuncion)*
    private NodoAST disyuncion() {
        NodoAST izquierdo = conjuncion();
        while (tokens.get(pos).getTipo() == TipoToken.BARRA) {
            pos++;
            NodoAST derecho = conjuncion();
            izquierdo = new Binario(Tipo.OR, izquierdo, derecho);
        }
        return izquierdo;
    }

    // conjuncion → atom (',' atom)*
    private NodoAST conjuncion() {
        NodoAST izquierdo = atomo();
        while (tokens.get(pos).getTipo() == TipoToken.COMA) {
            pos++;
            NodoAST derecho = atomo();
            izquierdo = new Binario(Tipo.AND, izquierdo, derecho);
        }
        return izquierdo;
    }

    // atom → CAMPO ':' NEGACION? VALOR  |  CAMPO COMP NUMERO
    private NodoAST atomo() {
        Token campo = tokens.get(pos);
        if (campo.getTipo() != TipoToken.CAMPO)
            throw new RuntimeException("Se esperaba un campo en posicion " + campo.getPosicion());
        pos++;

        // Determinar si es comparacion o valor
        Token sig = tokens.get(pos);

        if (sig.getTipo() == TipoToken.DOS_PUNTOS) {
            return atomoValor(campo);
        } else if (esComparador(sig)) {
            return atomoComparacion(campo, sig);
        } else {
            throw new RuntimeException("Se esperaba ':' o comparador despues del campo en posicion "
                    + campo.getPosicion());
        }
    }

    // CAMPO ':' NEGACION? VALOR
    private NodoAST atomoValor(Token campo) {
        pos++; // consumir DOS_PUNTOS
        boolean negado = false;
        if (tokens.get(pos).getTipo() == TipoToken.NEGACION) {
            negado = true;
            pos++;
        }
        Token valor = tokens.get(pos);
        if (valor.getTipo() != TipoToken.VALOR)
            throw new RuntimeException("Se esperaba un valor en posicion " + valor.getPosicion());
        pos++;

        String nombreCampo = campo.getLexema();
        String texto = valor.getLexema();
        Operador op;
        String col;

        switch (nombreCampo) {
            case "titulo", "performer", "album" -> {
                col = columnaSQL(nombreCampo);
                op = negado ? Operador.NOT_LIKE : Operador.LIKE;
                return new Hoja(col, op, "%" + texto + "%", 0);
            }
            case "genero" -> {
                col = columnaSQL(nombreCampo);
                op = negado ? Operador.DISTINTO : Operador.IGUAL;
                return new Hoja(col, op, texto, 0);
            }
            case "anio", "track" -> {
                col = columnaSQL(nombreCampo);
                op = negado ? Operador.DISTINTO : Operador.IGUAL;
                try {
                    return new Hoja(col, op, null, Integer.parseInt(texto));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Valor numerico invalido para " + nombreCampo + ": " + texto);
                }
            }
            default ->
                throw new RuntimeException("Campo desconocido: " + nombreCampo);
        }
    }

    // CAMPO COMP NUMERO
    private NodoAST atomoComparacion(Token campo, Token comp) {
        pos++; // consumir comparador
        Token numero = tokens.get(pos);
        if (numero.getTipo() != TipoToken.NUMERO)
            throw new RuntimeException("Se esperaba un numero en posicion " + numero.getPosicion());
        pos++;

        String col = columnaSQL(campo.getLexema());
        Operador op = switch (comp.getTipo()) {
            case MAYOR_QUE   -> Operador.MAYOR_QUE;
            case MAYOR_IGUAL -> Operador.MAYOR_IGUAL;
            case MENOR_IGUAL -> Operador.MENOR_IGUAL;
            default -> throw new RuntimeException("Operador no soportado: " + comp.getLexema());
        };

        return new Hoja(col, op, null, Integer.parseInt(numero.getLexema()));
    }

    private boolean esComparador(Token t) {
        return switch (t.getTipo()) {
            case MAYOR_QUE, MAYOR_IGUAL, MENOR_IGUAL -> true;
            default -> false;
        };
    }

    static String columnaSQL(String campo) {
        return switch (campo) {
            case "titulo"    -> "r.title";
            case "performer" -> "p.name";
            case "album"     -> "a.name";
            case "genero"    -> "r.genre";
            case "anio"      -> "r.year";
            case "track"     -> "r.track";
            default -> throw new RuntimeException("Campo desconocido: " + campo);
        };
    }
}
