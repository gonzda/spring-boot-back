package com.backend_spring.spring_back_test.Controller;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/taller/sedes")
public class SedeController {
    private final JdbcTemplate dataSource;

    public SedeController(@Qualifier("sqlsrvDataSource") DataSource sqlsrvDataSource) {
        this.dataSource = new JdbcTemplate(sqlsrvDataSource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSedes() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = dataSource.queryForList("SELECT * FROM [M_SEDE]");
            if (items.isEmpty()) {
                resp.put("message", "No hay sedes registradas");
                return ResponseEntity.status(200).body(resp);
            } else {
                resp.put("message", "Listado de sedes");
                resp.put("items", items);
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            resp.put("message", "Error al obtener sedes");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSede(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("NOMBRE");
        String ubicacion = (String) request.get("UBICACION");
        // The stored procedure returns a result set with MENSAJE column
        try {
            List<Map<String, Object>> result = dataSource.queryForList(
                    "EXEC [SP_A_M_SEDE] -1,?,?,1,1,?,?",
                    nombre, ubicacion, null, null);
            String mensaje = result.isEmpty() ? "Sede creada" : String.valueOf(result.get(0).get("MENSAJE"));
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", mensaje);
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al crear sede");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSede(
            @RequestBody Map<String, Object> request,
            @PathVariable int id) {
        String nombre = (String) request.get("NOMBRE");
        String ubicacion = (String) request.get("UBICACION");
        Object situacion = request.get("COD_SITUACION");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;

        try {
            if (estado == 0) {
                dataSource.update(
                        "UPDATE [M_SEDE] SET [ESTADO] = 1 WHERE [SEDE_P_inCODSEDE] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Sede restaurada correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> result = dataSource.queryForList(
                        "EXEC [SP_A_M_SEDE] ?,?,?,?,1,?,?",
                        id, nombre, ubicacion, situacion, null, null);
                String mensaje = result.isEmpty() ? "Sede actualizada" : String.valueOf(result.get(0).get("MENSAJE"));
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", mensaje);
                return ResponseEntity.status(201).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al actualizar sede");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSede(@PathVariable int id) {
        try {
            dataSource.update(
                    "UPDATE [M_SEDE] SET [ESTADO] = 0 WHERE [SEDE_P_inCODSEDE] = ?",
                    id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Sede marcado como INHABILITADO");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al eliminar sede");
            return ResponseEntity.status(500).body(resp);
        }
    }
}