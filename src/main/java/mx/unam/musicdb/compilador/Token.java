package mx.unam.musicdb.compilador;

public class Token {

    private final TipoToken tipo;
    private final String lexema;
    private final int posicion;

    public Token(TipoToken tipo, String lexema, int posicion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = posicion;
    }

    public TipoToken getTipo() { return tipo; }

    public String getLexema() { return lexema; }

    public int getPosicion() { return posicion; }

    @Override
    public String toString() {
        return String.format("Token{%s '%s' pos=%d}", tipo, lexema, posicion);
    }
}
