package mx.unam.musicdb.compilador;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private static final String CAMPOS = "titulo|performer|album|genero|anio|track";

    private final String entrada;
    private int pos;

    public Lexer(String entrada) {
        this.entrada = entrada;
        this.pos = 0;
    }

    public List<Token> tokenizar() {
        List<Token> tokens = new ArrayList<>();
        ignorarEspacios();
        while (pos < entrada.length()) {
            char c = entrada.charAt(pos);
            // Detectar inicio de un campo (letras)
            if (Character.isLetter(c)) {
                tokens.add(leerCampo());
                ignorarEspacios();
                if (pos >= entrada.length()) break;
                // Determinar si sigue : (valor) o un comparador
                c = entrada.charAt(pos);
                if (c == ':') {
                    tokens.add(new Token(TipoToken.DOS_PUNTOS, ":", pos));
                    pos++;
                    ignorarEspacios();
                    if (pos < entrada.length() && entrada.charAt(pos) == '!') {
                        tokens.add(new Token(TipoToken.NEGACION, "!", pos));
                        pos++;
                        ignorarEspacios();
                    }
                    tokens.add(leerValor());
                } else if (c == '>') {
                    pos++;
                    if (pos < entrada.length() && entrada.charAt(pos) == '=') {
                        tokens.add(new Token(TipoToken.MAYOR_IGUAL, ">=", pos - 1));
                        pos++;
                    } else {
                        tokens.add(new Token(TipoToken.MAYOR_QUE, ">", pos - 1));
                    }
                    ignorarEspacios();
                    tokens.add(leerNumero());
                } else if (c == '<') {
                    pos++;
                    if (pos < entrada.length() && entrada.charAt(pos) == '=') {
                        tokens.add(new Token(TipoToken.MENOR_IGUAL, "<=", pos - 1));
                        pos++;
                    } else {
                        throw new RuntimeException("Operador '<' no soportado en posicion " + pos);
                    }
                    ignorarEspacios();
                    tokens.add(leerNumero());
                } else {
                    throw new RuntimeException("Se esperaba ':', '>', '<' despues del campo en posicion " + pos);
                }
            } else if (c == ',') {
                tokens.add(new Token(TipoToken.COMA, ",", pos));
                pos++;
            } else if (c == '/') {
                tokens.add(new Token(TipoToken.BARRA, "/", pos));
                pos++;
            } else {
                throw new RuntimeException("Caracter inesperado '" + c + "' en posicion " + pos);
            }
            ignorarEspacios();
        }
        tokens.add(new Token(TipoToken.FIN, "", pos));
        return tokens;
    }

    private Token leerCampo() {
        int inicio = pos;
        while (pos < entrada.length() && Character.isLetter(entrada.charAt(pos)))
            pos++;
        String lexema = entrada.substring(inicio, pos);
        if (!lexema.matches(CAMPOS))
            throw new RuntimeException("Campo desconocido '" + lexema + "' en posicion " + inicio);
        return new Token(TipoToken.CAMPO, lexema, inicio);
    }

    private Token leerValor() {
        int inicio = pos;
        while (pos < entrada.length() && entrada.charAt(pos) != ',' && entrada.charAt(pos) != '/')
            pos++;
        String lexema = entrada.substring(inicio, pos).trim();
        return new Token(TipoToken.VALOR, lexema, inicio);
    }

    private Token leerNumero() {
        int inicio = pos;
        while (pos < entrada.length() && Character.isDigit(entrada.charAt(pos)))
            pos++;
        if (pos == inicio)
            throw new RuntimeException("Se esperaba un numero en posicion " + pos);
        return new Token(TipoToken.NUMERO, entrada.substring(inicio, pos), inicio);
    }

    private void ignorarEspacios() {
        while (pos < entrada.length() && Character.isWhitespace(entrada.charAt(pos)))
            pos++;
    }
}
