package com.backend_spring.spring_back_test.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taller")
public class TallerControlller {
    private final JdbcTemplate sqlsrvJdbcTemplate;

    public TallerControlller(@Qualifier("sqlsrvDataSource") DataSource sqlsrvDataSource) {
        this.sqlsrvJdbcTemplate = new JdbcTemplate(sqlsrvDataSource);
    }

    // GET /api/taller
    @GetMapping
    public Map<String, Object> getAllTalleres() {
        List<Map<String, Object>> usuarios = sqlsrvJdbcTemplate.queryForList(
                "EXEC [S_S_GET_ALL_TABLES_VACATIONS] 'GET_TALLER', 1, 0, null");
        int totalCount = usuarios.size();
        Map<String, Object> response = new HashMap<>();
        if (usuarios == null || usuarios.isEmpty()) {
            response.put("message", "No hay talleres registrados");
        } else {
            response.put("message", "Listado de talleres");
            response.put("total_count", totalCount);
            response.put("items", usuarios);
        }
        return response;
    }

    // GET /api/taller/{id}
    @GetMapping("/{id}")
    public Map<String, Object> getTallerById(@PathVariable String id) {
        List<Map<String, Object>> taller = sqlsrvJdbcTemplate.queryForList(
                "EXEC [S_S_GET_ALL_TABLES_VACATIONS] 'GET_TALLER', 2, 0, ?", id);
        Map<String, Object> response = new HashMap<>();
        if (taller == null || taller.isEmpty()) {
            response.put("message", "No hay talleres registrados");
        } else {
            response.put("message", "Listado de talleres");
            response.put("items", taller);
        }
        return response;
    }

    // POST /api/taller
    @PostMapping
    public Map<String, Object> createTaller(@RequestBody Map<String, Object> request) {
        // Extract nested fields
        Object temporada = getNested(request, "temporada.id");
        Object sede = getNested(request, "sede.id");
        Object taller = getNested(request, "taller_edad.id");
        Object dias = getNested(request, "dias.id");
        Object horario = getNested(request, "horario.id");
        Object estado = getNested(request, "estado.id");
        Object precio = request.get("precio");
        Object descripcion = request.get("descripcion");
        Object capacidad = request.get("capacidad");
        Object fecha_ini = request.get("fecha_ini");
        Object fecha_fin = request.get("fecha_fin");
        Object host = null;
        Object user = null;

        List<Map<String, Object>> msj = sqlsrvJdbcTemplate.queryForList(
                "EXEC [SP_A_M_GRUPO_TALLER] ?, ?, ?, ?, ?, ?, 1, ?, ?, ?, ?, ?, ?, ?",
                temporada, sede, taller, dias, horario, estado, precio, descripcion, capacidad, fecha_ini, fecha_fin,
                host, user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", msj.get(0).get("MENSAJE"));
        return response;
    }

    // PUT /api/taller/{id}
    @PutMapping("/{id}")
    public Map<String, Object> updateTaller(@RequestBody Map<String, Object> request, @PathVariable String id) {
        Object temporada = getNested(request, "temporada.id");
        Object sede = getNested(request, "sede.id");
        Object taller = getNested(request, "taller_edad.id");
        Object dias = getNested(request, "dias.id");
        Object horario = getNested(request, "horario.id");
        Object estado = getNested(request, "estado.id");
        Object precio = request.get("precio");
        Object descripcion = request.get("descripcion");
        Object capacidad = request.get("capacidad");
        Object fecha_ini = request.get("fecha_ini");
        Object fecha_fin = request.get("fecha_fin");
        Object host = null;
        Object user = null;

        List<Map<String, Object>> msj = sqlsrvJdbcTemplate.queryForList(
                "EXEC [SP_A_M_GRUPO_TALLER] ?, ?, ?, ?, ?, ?, 1, ?, ?, ?, ?, ?, ?, ?",
                temporada, sede, taller, dias, horario, estado, precio, descripcion, capacidad, fecha_ini, fecha_fin,
                host, user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", msj.get(0).get("MENSAJE"));
        return response;
    }

    // DELETE /api/taller/{id}
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTaller(@PathVariable String id) {
        sqlsrvJdbcTemplate.update(
                "UPDATE [M_GRUPO_TALLER] SET [ESTADO_P_inCODPROCESO] = 4 WHERE " +
                        "(RIGHT('000' + CAST(TEMPORADA_P_inCODTEMPORADA AS varchar(3)),3) " +
                        "+ RIGHT('000' + CAST(SEDE_P_inCODSEDE  AS varchar(3)),3) " +
                        "+ RIGHT('000' + CAST(TALLER_P_inCODTALLER  AS varchar(3)),3) " +
                        "+ RIGHT('000' + CAST(GRUPO_P_inCODGRUPO  AS varchar(3)),3) " +
                        "+ RIGHT('000' + CAST(GRUPOHORA_P_inCODGRUPOHORA  AS varchar(3)),3)) = ?",
                id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Taller marcado como INHABILITADO");
        return response;
    }

    // DELETE /api/taller
    @DeleteMapping
    public Map<String, Object> deleteTallerMasivo(@RequestBody Map<String, Object> request) {
        List<?> ids = (List<?>) request.get("ids");
        Map<String, Object> response = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            response.put("message", "No se proporcionaron IDs");
            return response;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = "UPDATE [M_GRUPO_TALLER] SET [ESTADO_P_inCODPROCESO] = 4 WHERE " +
                "(RIGHT('000' + CAST(TEMPORADA_P_inCODTEMPORADA AS varchar(3)),3) " +
                "+ RIGHT('000' + CAST(SEDE_P_inCODSEDE  AS varchar(3)),3) " +
                "+ RIGHT('000' + CAST(TALLER_P_inCODTALLER  AS varchar(3)),3) " +
                "+ RIGHT('000' + CAST(GRUPO_P_inCODGRUPO  AS varchar(3)),3) " +
                "+ RIGHT('000' + CAST(GRUPOHORA_P_inCODGRUPOHORA  AS varchar(3)),3)) IN (" + placeholders + ")";
        sqlsrvJdbcTemplate.update(sql, ids.toArray());
        response.put("message", "Taller (es) marcado (s) como INHABILITADO correctamente");
        return response;
    }

    // Helper to get nested property from map using dot notation
    private Object getNested(Map<String, Object> map, String path) {
        String[] keys = path.split("\\.");
        Object value = map;
        for (String key : keys) {
            if (!(value instanceof Map))
                return null;
            value = ((Map<?, ?>) value).get(key);
        }
        return value;
    }
}
