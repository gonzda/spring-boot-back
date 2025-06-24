package com.backend_spring.spring_back_test.Controller;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/temporadas")
public class TemporadaController {
    private final JdbcTemplate jdbcTemplate;

    public TemporadaController(@Qualifier("sqlsrvDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTemporadas() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT * FROM [M_TEMPORADA]");
            if (items.isEmpty()) {
                resp.put("message", "No hay temporadas registradas");
                return ResponseEntity.status(200).body(resp);
            } else {
                resp.put("message", "Listado de temporadas");
                resp.put("items", items);
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            resp.put("message", "Error al obtener temporadas");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTemporada(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("NOMBRE");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    "EXEC [SP_A_M_TEMPORADA] -1,?,1,?,?,?",
                    nombre, estado, null, null);
            String mensaje = result.isEmpty() ? "Temporada creada" : String.valueOf(result.get(0).get("MENSAJE"));
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", mensaje);
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al crear temporada");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTemporada(
            @RequestBody Map<String, Object> request,
            @PathVariable("id") int id) {
        String nombre = (String) request.get("NOMBRE");
        Integer situacion = request.get("COD_SITUACION") != null ? (Integer) request.get("COD_SITUACION") : 1;
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        try {
            if (estado == 0) {
                jdbcTemplate.update(
                        "UPDATE [M_TEMPORADA] SET [ESTADO] = 1 WHERE [TEMPORADA_P_inCODTEMPORADA] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Temporada restaurada correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> result = jdbcTemplate.queryForList(
                        "EXEC [SP_A_M_TEMPORADA] ?,?,?,1,?,?",
                        id, nombre, situacion, null, null);
                String mensaje = result.isEmpty() ? "Temporada actualizada"
                        : String.valueOf(result.get(0).get("MENSAJE"));
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", mensaje);
                return ResponseEntity.status(201).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al actualizar temporada");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTemporada(@PathVariable("id") int id) {
        try {
            jdbcTemplate.update(
                    "UPDATE [M_TEMPORADA] SET [ESTADO] = 0 WHERE [TEMPORADA_P_inCODTEMPORADA] = ?",
                    id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Temporada marcado como INHABILITADO");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al eliminar temporada");
            return ResponseEntity.status(500).body(resp);
        }
    }
}
