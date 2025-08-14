package com.annaehugo.freepharma.api.controllers.fiscal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.annaehugo.freepharma.application.services.ImportacaoNFeService;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fiscal/importacao-nfe")
@Api(value = "Importação NFe", description = "Operações para importação de Notas Fiscais Eletrônicas")
public class ImportacaoNFeController {
    
    @Autowired
    private ImportacaoNFeService importacaoNFeService;

    @PostMapping("/xml")
    @ApiOperation(value = "Importar NFe via XML", 
                  notes = "Importa e processa automaticamente um arquivo XML de NFe")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "NFe importada com sucesso"),
        @ApiResponse(code = 400, message = "Erro na validação do arquivo ou dados"),
        @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity<?> importarNFe(@RequestParam("file") MultipartFile file) {
        try {
            var resultado = importacaoNFeService.importarNFeXml(file);
            
            // Definir status HTTP baseado no resultado
            if ("SUCESSO".equals(resultado.get("status"))) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.badRequest().body(resultado);
            }
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERRO");
            errorResponse.put("mensagem", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERRO_INTERNO");
            errorResponse.put("mensagem", "Erro interno no processamento: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/xml/completo")
    @ApiOperation(value = "Importar NFe com validação completa", 
                  notes = "Importa NFe com todas as validações fiscais e de negócio")
    public ResponseEntity<?> importarNFeCompleto(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "unidadeId", required = false) Long unidadeId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            @RequestParam(value = "observacoes", required = false) String observacoes) {
        
        try {
            // TODO: Buscar unidade e usuário pelos IDs
            Unidade unidade = null; // unidadeService.findById(unidadeId);
            UsuarioAdministrador usuario = null; // usuarioService.findById(usuarioId);
            
            var resultado = importacaoNFeService.importarNFeXmlCompleto(file, unidade, usuario);
            
            if ("SUCESSO".equals(resultado.get("status"))) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.badRequest().body(resultado);
            }
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERRO");
            errorResponse.put("mensagem", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERRO_INTERNO");
            errorResponse.put("mensagem", "Erro interno no processamento: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
