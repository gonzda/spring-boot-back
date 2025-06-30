package com.backend_spring.spring_back_test.Controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.util.*;

@RestController
@RequestMapping("/api/taller/talleres-edades")
public class TalleresEdadesController {
    private final JdbcTemplate jdbcTemplate;

    public TalleresEdadesController(@Qualifier("sqlsrvDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTalleresEdades() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> items = jdbcTemplate.queryForList(
                    "EXEC [S_S_GET_ALL_TABLES_VACATIONS] 'M_TALLER',1,0,null");
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
    public ResponseEntity<Map<String, Object>> createTalleresEdades(@RequestBody Map<String, Object> request) {
        String des_taller = (String) request.get("COD_DESTALLER");
        String des_rango = (String) request.get("DES_RANGO");
        Integer situacion = request.get("COD_SITUACION") != null ? (Integer) request.get("COD_SITUACION") : 1;
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        Object host = null;
        Object user = null;

        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    "SELECT * FROM [M_RANGO_EDAD] WHERE [RANGO_chDESRANGO] LIKE ?", "%" + des_rango + "%");
            if (result.isEmpty()) {
                jdbcTemplate.queryForList(
                        "EXEC [SP_A_M_RANGO_EDAD] -1,?, null, null, 1,1, null, null", des_rango);
                result = jdbcTemplate.queryForList(
                        "SELECT * FROM [M_RANGO_EDAD] WHERE [RANGO_chDESRANGO] LIKE ?", "%" + des_rango + "%");
            }
            Object rango = result.get(0).get("RANGO_P_inCODRANGO");

            List<Map<String, Object>> msj = jdbcTemplate.queryForList(
                    "EXEC [SP_A_M_TALLER] -1, ?, ?, ?, ?, ?, ?",
                    des_taller, rango, situacion, estado, host, user);
            String mensaje = msj.isEmpty() ? "Taller creado" : String.valueOf(msj.get(0).get("MENSAJE"));
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
    public ResponseEntity<Map<String, Object>> updateTalleresEdades(
            @RequestBody Map<String, Object> request,
            @PathVariable("id") int id) {
        String destaller = (String) request.get("COD_DESTALLER");
        Object rango = request.get("COD_RANGO");
        Object situacion = request.get("COD_SITUACION");
        Integer estado = request.get("ESTADO") != null ? (Integer) request.get("ESTADO") : 1;
        Object host = null;
        Object user = null;
        try {
            if (estado == 0) {
                jdbcTemplate.update(
                        "UPDATE [M_TALLER] SET [ESTADO] = 1 WHERE [TALLER_P_inCODTALLER] = ?",
                        id);
                Map<String, Object> resp = new HashMap<>();
                resp.put("message", "Taller restaurado correctamente");
                return ResponseEntity.status(200).body(resp);
            } else {
                List<Map<String, Object>> msj = jdbcTemplate.queryForList(
                        "EXEC [SP_A_M_TALLER] ?, ?, ?, ?, ?, ?, ?",
                        id, destaller, rango, situacion, estado, host, user);
                String mensaje = msj.isEmpty() ? "Taller actualizado" : String.valueOf(msj.get(0).get("MENSAJE"));
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
    public ResponseEntity<Map<String, Object>> deleteTalleresEdades(@PathVariable("id") int id) {
        try {
            jdbcTemplate.update(
                    "UPDATE [M_TALLER] SET [ESTADO] = 0 WHERE [TALLER_P_inCODTALLER]=?",
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
