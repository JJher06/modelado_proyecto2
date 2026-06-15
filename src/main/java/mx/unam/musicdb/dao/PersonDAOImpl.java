package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Person;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDAOImpl implements PersonDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO persons (stage_name, real_name, birth_date, death_date) VALUES (?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM persons WHERE id_person = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM persons ORDER BY stage_name";

    private static final String SQL_ACTUALIZAR =
        "UPDATE persons SET stage_name=?, real_name=?, birth_date=?, death_date=? WHERE id_person=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM persons WHERE id_person = ?";

    @Override
    public void insertar(Person p) {
        try (PreparedStatement ps = getConn().prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getStageName());
            ps.setString(2, p.getRealName());
            ps.setString(3, p.getBirthDate());
            ps.setString(4, p.getDeathDate());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) p.setIdPerson(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar persona: " + p, e);
        }
    }

    @Override
    public Optional<Person> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Person> buscarTodos() {
        List<Person> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las personas", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Person p) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, p.getStageName());
            ps.setString(2, p.getRealName());
            ps.setString(3, p.getBirthDate());
            ps.setString(4, p.getDeathDate());
            ps.setInt(5, p.getIdPerson());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar persona: " + p, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar persona con id=" + id, e);
        }
    }

    private Person mapear(ResultSet rs) throws SQLException {
        Person p = new Person();
        p.setIdPerson(rs.getInt("id_person"));
        p.setStageName(rs.getString("stage_name"));
        p.setRealName(rs.getString("real_name"));
        p.setBirthDate(rs.getString("birth_date"));
        p.setDeathDate(rs.getString("death_date"));
        return p;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
