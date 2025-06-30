package com.backend_spring.spring_back_test.Controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.util.*;

@RestController
@RequestMapping("/api/taller/horarios")
public class HorariosController {
    private final JdbcTemplate jdbcTemplate;

    public HorariosController(@Qualifier("sqlsrvDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHorarios() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT * FROM [M_GRUPO_HORA]");
            if (items.isEmpty()) {
                resp.put("message", "No hay horarios registrados");
                return ResponseEntity.status(200).body(resp);
            } else {
                resp.put("message", "Listado de horarios");
                resp.put("items", items);
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            resp.put("message", "Error al obtener horarios");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping
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

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHorarios(
            @RequestBody Map<String, Object> request,
            @PathVariable("id") int id) {
        String nombre = (String) request.get("NOMBRE");
        Object turno = request.get("COD_TURNO");
        Object situacion = request.get("COD_SITUACION");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        Object hora_i = request.get("COD_HORA_I");
        Object hora_f = request.get("COD_HORA_F");
        Object host = null;
        Object user = null;
        try {
            if (estado == 0) {
                jdbcTemplate.update(
                        "UPDATE [M_GRUPO_HORA] SET [ESTADO] = 1 WHERE [GRUPOHORA_P_inCODGRUPOHORA] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Horario restaurado correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> result = jdbcTemplate.queryForList(
                        "EXEC [SP_A_M_GRUPO_HORA] ?, ?, ?, ?, ?, ?, ?, ?, ?",
                        id, nombre, turno, situacion, estado, hora_i, hora_f, host, user);
                String mensaje = result.isEmpty() ? "Horario actualizado"
                        : String.valueOf(result.get(0).get("MENSAJE"));
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", mensaje);
                return ResponseEntity.status(201).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al actualizar horario");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHorarios(@PathVariable("id") int id) {
        try {
            jdbcTemplate.update(
                    "UPDATE [M_GRUPO_HORA] SET [ESTADO] = 0 WHERE [GRUPOHORA_P_inCODGRUPOHORA]=?",
                    id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Horario marcado como INHABILITADO");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al eliminar horario");
            return ResponseEntity.status(500).body(resp);
        }
    }
}
