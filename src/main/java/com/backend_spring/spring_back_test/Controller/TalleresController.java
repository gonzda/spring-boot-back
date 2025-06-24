package com.backend_spring.spring_back_test.Controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.util.*;

@RestController
@RequestMapping("/api/talleres")
public class TalleresController {
    private final JdbcTemplate jdbcTemplate;

    public TalleresController(@Qualifier("sqlsrvDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTalleres() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT * FROM [M_TALLER_DESC]");
            if (items.isEmpty()) {
                resp.put("message", "No hay talleres registrados");
                return ResponseEntity.status(200).body(resp);
            } else {
                resp.put("message", "Listado de talleres");
                resp.put("items", items);
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            resp.put("message", "Error al obtener talleres");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTalleres(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("NOMBRE");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    "EXEC [SP_A_M_TALLER_DESC] -1,?,1,?,?,?",
                    nombre, estado, null, null);
            String mensaje = result.isEmpty() ? "Taller creado" : String.valueOf(result.get(0).get("MENSAJE"));
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", mensaje);
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al crear taller");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTalleres(
            @RequestBody Map<String, Object> request,
            @PathVariable("id") int id) {
        String nombre = (String) request.get("NOMBRE");
        Integer situacion = request.get("COD_SITUACION") != null ? (Integer) request.get("COD_SITUACION") : 1;
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        try {
            if (estado == 0) {
                jdbcTemplate.update(
                        "UPDATE [M_TALLER_DESC] SET [ESTADO] = 1 WHERE [TALLERDESC_P_inCODTALLER] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Taller restaurado correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> result = jdbcTemplate.queryForList(
                        "EXEC [SP_A_M_TALLER_DESC] ?,?,?,1,?,?",
                        id, nombre, situacion, null, null);
                String mensaje = result.isEmpty() ? "Taller actualizado" : String.valueOf(result.get(0).get("MENSAJE"));
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", mensaje);
                return ResponseEntity.status(201).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al actualizar taller");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTalleres(@PathVariable("id") int id) {
        try {
            jdbcTemplate.update(
                    "UPDATE [M_TALLER_DESC] SET [ESTADO] = 0 WHERE [TALLERDESC_P_inCODTALLER] = ?",
                    id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Taller marcado como INHABILITADO");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al eliminar taller");
            return ResponseEntity.status(500).body(resp);
        }
    }
}
