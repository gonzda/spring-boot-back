package com.backend_spring.spring_back_test.Controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.util.*;

@RestController
@RequestMapping("/api/dias")
public class DiasController {
    private final JdbcTemplate jdbcTemplate;

    public DiasController(@Qualifier("sqlsrvDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDias() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT * FROM [M_GRUPO_DIAS]");
            if (items.isEmpty()) {
                resp.put("message", "No hay días registrados");
                return ResponseEntity.status(200).body(resp);
            } else {
                resp.put("message", "Listado de días");
                resp.put("items", items);
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            resp.put("message", "Error al obtener días");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDias(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("NOMBRE");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    "EXEC [SP_A_M_GRUPO_DIAS] -1,?,?",
                    nombre, estado);
            String mensaje = result.isEmpty() ? "Día creado" : String.valueOf(result.get(0).get("MENSAJE"));
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", mensaje);
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al crear día");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDias(
            @RequestBody Map<String, Object> request,
            @PathVariable("id") int id) {
        String nombre = (String) request.get("NOMBRE");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        try {
            if (estado == 0) {
                jdbcTemplate.update(
                        "UPDATE [M_GRUPO_DIAS] SET [ESTADO] = 1 WHERE [GRUPO_P_inCODGRUPO] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Día restaurado correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> result = jdbcTemplate.queryForList(
                        "EXEC [SP_A_M_GRUPO_DIAS] ?,?,?",
                        id, nombre, estado);
                String mensaje = result.isEmpty() ? "Día actualizado" : String.valueOf(result.get(0).get("MENSAJE"));
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", mensaje);
                return ResponseEntity.status(201).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al actualizar día");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDias(@PathVariable("id") int id) {
        try {
            jdbcTemplate.update(
                    "UPDATE [M_GRUPO_DIAS] SET [ESTADO] = 0 WHERE [GRUPO_P_inCODGRUPO] = ?",
                    id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Día marcado como INHABILITADO");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al eliminar día");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/horarios")
    public ResponseEntity<Map<String, Object>> createHorarios(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("NOMBRE");
        Object turno = request.get("COD_TURNO");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        Object hora_i = request.get("COD_HORA_I");
        Object hora_f = request.get("COD_HORA_F");
        Object host = null;
        Object user = null;
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    "EXEC [SP_A_M_GRUPO_HORA] -1, ?, ?, 1, ?, ?, ?, ?, ?",
                    nombre, turno, estado, hora_i, hora_f, host, user);
            String mensaje = result.isEmpty() ? "Horario creado" : String.valueOf(result.get(0).get("MENSAJE"));
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", mensaje);
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al crear horario");
            return ResponseEntity.status(500).body(resp);
        }
    }
}
