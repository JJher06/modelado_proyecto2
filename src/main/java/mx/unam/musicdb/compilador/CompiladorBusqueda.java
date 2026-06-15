package mx.unam.musicdb.compilador;

import mx.unam.musicdb.dao.RolaDAO;
import mx.unam.musicdb.model.Rola;

import java.util.List;

public class CompiladorBusqueda {

    private final RolaDAO rolaDAO;

    public CompiladorBusqueda(RolaDAO rolaDAO) {
        this.rolaDAO = rolaDAO;
    }

    public List<Rola> buscar(String consulta) {
        if (consulta == null || consulta.isBlank())
            return rolaDAO.buscarTodos();

        // Si no contiene ':' ni '/' ni ',' ni comparador, es busqueda simple por titulo
        if (esBusquedaSimple(consulta))
            return rolaDAO.buscarPorTitulo(consulta);

        try {
            Lexer lexer = new Lexer(consulta);
            List<Token> tokens = lexer.tokenizar();
            Parser parser = new Parser(tokens);
            NodoAST arbol = parser.parsear();
            EvaluadorConsulta evaluador = new EvaluadorConsulta();
            EvaluadorConsulta.Resultado resultado = evaluador.evaluar(arbol);
            return rolaDAO.buscarPersonalizado(
                    resultado.getWhereClause(), resultado.getParametros());
        } catch (Exception e) {
            throw new RuntimeException("Error al compilar la busqueda: " + consulta, e);
        }
    }

    private boolean esBusquedaSimple(String consulta) {
        return !consulta.contains(":")
            && !consulta.contains(",")
            && !consulta.contains("/")
            && !consulta.contains(">")
            && !consulta.contains("<");
    }
}
