package mx.unam.musicdb.compilador;

import mx.unam.musicdb.compilador.NodoAST.*;

import java.util.ArrayList;
import java.util.List;

public class EvaluadorConsulta {

    private final StringBuilder whereClause;
    private final List<Object> parametros;

    public EvaluadorConsulta() {
        this.whereClause = new StringBuilder();
        this.parametros = new ArrayList<>();
    }

    public Resultado evaluar(NodoAST arbol) {
        whereClause.setLength(0);
        parametros.clear();
        recorrer(arbol);
        return new Resultado(whereClause.toString(), parametros);
    }

    private void recorrer(NodoAST nodo) {
        switch (nodo.getTipo()) {
            case HOJA -> evaluarHoja((Hoja) nodo);
            case AND  -> {
                Binario b = (Binario) nodo;
                whereClause.append('(');
                recorrer(b.getIzquierdo());
                whereClause.append(" AND ");
                recorrer(b.getDerecho());
                whereClause.append(')');
            }
            case OR   -> {
                Binario b = (Binario) nodo;
                whereClause.append('(');
                recorrer(b.getIzquierdo());
                whereClause.append(" OR ");
                recorrer(b.getDerecho());
                whereClause.append(')');
            }
        }
    }

    private void evaluarHoja(Hoja hoja) {
        whereClause.append(hoja.getCampo()).append(' ').append(hoja.getOperador().toSQL());
        if (hoja.getValorTexto() != null) {
            parametros.add(hoja.getValorTexto());
        } else {
            parametros.add(hoja.getValorNumero());
        }
    }

    public static class Resultado {
        private final String whereClause;
        private final List<Object> parametros;

        Resultado(String whereClause, List<Object> parametros) {
            this.whereClause = whereClause;
            this.parametros = parametros;
        }

        public String getWhereClause() { return whereClause; }
        public List<Object> getParametros() { return parametros; }
    }
}
