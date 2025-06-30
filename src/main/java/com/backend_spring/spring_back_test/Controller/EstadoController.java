package com.backend_spring.spring_back_test.Controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.util.*;

@RestController
@RequestMapping("/api/taller/estados")
public class EstadoController {
    private final JdbcTemplate dataSource;

    public EstadoController(@Qualifier("sqlsrvDataSource") DataSource sqlsrvDataSource) {
        this.dataSource = new JdbcTemplate(sqlsrvDataSource);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEstado(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("NOMBRE");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;

        List<Map<String, Object>> exists = dataSource.queryForList(
                "SELECT * FROM [M_ESTADOPROCESO] WHERE [ESTADO_chDESPROCESO] = ?", nombre);
        if (!exists.isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "El estado ya existe");
            return ResponseEntity.status(200).body(resp);
        }
        try {
            dataSource.update(
                    "INSERT INTO [M_ESTADOPROCESO] ([ESTADO_chDESPROCESO], [ESTADO]) VALUES (?, ?)",
                    nombre, estado);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Estado creado correctamente");
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al crear estado");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEstado(
            @RequestBody Map<String, Object> request,
            @PathVariable("id") int id) {
        String nombre = (String) request.get("NOMBRE");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;

        try {
            if (estado == 0) {
                dataSource.update(
                        "UPDATE [M_ESTADOPROCESO] SET [ESTADO] = 1 WHERE [ESTADO_P_inCODPROCESO] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Estado restaurado correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> exists = dataSource.queryForList(
                        "SELECT * FROM [M_ESTADOPROCESO] WHERE [ESTADO_chDESPROCESO] = ?", nombre);
                if (!exists.isEmpty()) {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("message", "El estado ya existe");
                    return ResponseEntity.status(200).body(resp);
                }
                dataSource.update(
                        "UPDATE [M_ESTADOPROCESO] SET [ESTADO_chDESPROCESO] = ? WHERE [ESTADO_P_inCODPROCESO] = ?",
                        nombre, id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Estado actualizado correctamente");
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al actualizar estado");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEstado(@PathVariable("id") int id) {
        try {
            dataSource.update(
                    "UPDATE [M_ESTADOPROCESO] SET [ESTADO] = 0 WHERE [ESTADO_P_inCODPROCESO] = ?",
                    id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Estado marcado como INHABILITADO");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Error al eliminar estado");
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEstados() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = dataSource.queryForList("SELECT * FROM [M_ESTADOPROCESO]");
            if (items.isEmpty()) {
                resp.put("message", "No hay estados registrados");
                return ResponseEntity.status(200).body(resp);
            } else {
                resp.put("message", "Listado de estados");
                resp.put("items", items);
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            resp.put("message", "Error al obtener estados");
            return ResponseEntity.status(500).body(resp);
        }
    }

}
