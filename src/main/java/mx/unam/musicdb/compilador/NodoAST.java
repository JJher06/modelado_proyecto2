package mx.unam.musicdb.compilador;

import java.util.Objects;

public abstract class NodoAST {

    public enum Tipo { HOJA, AND, OR }

    public abstract Tipo getTipo();

    public static class Hoja extends NodoAST {
        private final String campo;
        private final Operador operador;
        private final String valorTexto;
        private final int valorNumero;

        public Hoja(String campo, Operador operador, String valorTexto, int valorNumero) {
            this.campo = campo;
            this.operador = operador;
            this.valorTexto = valorTexto;
            this.valorNumero = valorNumero;
        }

        public String getCampo() { return campo; }
        public Operador getOperador() { return operador; }
        public String getValorTexto() { return valorTexto; }
        public int getValorNumero() { return valorNumero; }

        @Override
        public Tipo getTipo() { return Tipo.HOJA; }

        @Override
        public String toString() {
            if (valorTexto != null)
                return campo + " " + operador + " '" + valorTexto + "'";
            return campo + " " + operador + " " + valorNumero;
        }
    }

    public static class Binario extends NodoAST {
        private final Tipo tipo;
        private final NodoAST izquierdo;
        private final NodoAST derecho;

        public Binario(Tipo tipo, NodoAST izquierdo, NodoAST derecho) {
            this.tipo = tipo;
            this.izquierdo = izquierdo;
            this.derecho = derecho;
        }

        public NodoAST getIzquierdo() { return izquierdo; }
        public NodoAST getDerecho() { return derecho; }

        @Override
        public Tipo getTipo() { return tipo; }

        @Override
        public String toString() {
            return "(" + izquierdo + " " + tipo + " " + derecho + ")";
        }
    }

    public enum Operador {
        IGUAL, DISTINTO, LIKE, NOT_LIKE, MAYOR_QUE, MAYOR_IGUAL, MENOR_IGUAL;

        public String toSQL() {
            return switch (this) {
                case IGUAL      -> "= ?";
                case DISTINTO   -> "!= ?";
                case LIKE       -> "LIKE ?";
                case NOT_LIKE   -> "NOT LIKE ?";
                case MAYOR_QUE  -> "> ?";
                case MAYOR_IGUAL-> ">= ?";
                case MENOR_IGUAL-> "<= ?";
            };
        }
    }
}
