package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
import com.annaehugo.freepharma.domain.entity.fiscal.ImportacaoNFe;
import com.annaehugo.freepharma.domain.entity.fiscal.StatusImportacao;
import com.annaehugo.freepharma.domain.repository.fiscal.ImportacaoNFeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImportacaoNFeService {

    @Autowired
    private NFeXmlParser nfeXmlParser;
    
    @Autowired
    private ProcessamentoNFeService processamentoNFeService;
    
    @Autowired
    private ImportacaoNFeRepository importacaoNFeRepository;

    /**
     * Importa e processa automaticamente um arquivo XML de NFe
     */
    @Transactional
    public Map<String, Object> importarNFeXml(MultipartFile file) {
        return importarNFeXml(file, null, null);
    }
    
    /**
     * Importa NFe com validações fiscais completas
     */
    @Transactional
    public Map<String, Object> importarNFeXmlCompleto(MultipartFile file, Unidade unidade, UsuarioAdministrador usuario) {
        validarArquivoNFe(file);
        validarPermissoesUsuario(usuario, unidade);
        
        return importarNFeXml(file, unidade, usuario);
    }
    
    /**
     * Importa e processa automaticamente um arquivo XML de NFe com dados do usuário e unidade
     */
    @Transactional
    public Map<String, Object> importarNFeXml(MultipartFile file, Unidade unidade, UsuarioAdministrador usuario) {
        validarArquivoNFe(file);
        
        // Criar registro de importação
        ImportacaoNFe importacao = criarRegistroImportacao(file, unidade, usuario);
        
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            importacao.setDataInicioProcessamento(new Date());
            importacao.setStatus(StatusImportacao.PROCESSANDO);
            importacaoNFeRepository.save(importacao);
            
            // Validações preliminares do arquivo
            validarConteudoXml(file);
            
            // Parse do XML
            byte[] xmlContent = file.getBytes();
            NFeXmlData nfeData = nfeXmlParser.parseNFeXml(xmlContent);
            
            // Validações fiscais da NFe
            validarDadosNFe(nfeData, unidade);
            
            // Verificar duplicação
            verificarNFeDuplicada(nfeData.getChaveAcesso(), unidade);
            
            // Processamento automático
            ProcessamentoNFeService.ProcessamentoResult processResult = 
                processamentoNFeService.processarNFe(nfeData, unidade, importacao);
            
            // Contabilizar inconsistências
            int inconsistenciasDetectadas = contarInconsistencias(importacao);
            importacao.setQuantidadeInconsistenciasDetectadas(inconsistenciasDetectadas);
            
            // Atualizar registro de importação
            importacao.setDataFimProcessamento(new Date());
            importacao.setQuantidadeNotasArquivo(1);
            
            if (processResult.isSucesso()) {
                importacao.setStatus(StatusImportacao.CONCLUIDA);
                importacao.setQuantidadeNotasProcessadas(1);
                importacao.setQuantidadeNotasComErro(0);
                importacao.setLogProcessamento("NFe processada com sucesso. " + 
                    inconsistenciasDetectadas + " inconsistências detectadas.");
                
                resultado.put("status", "SUCESSO");
                resultado.put("mensagem", processResult.getMensagem());
                resultado.put("notaFiscalId", processResult.getNotaFiscal().getId());
                resultado.put("fornecedorId", processResult.getFornecedor().getId());
                resultado.put("itensProcessados", processResult.getItensProcessados().size());
                resultado.put("inconsistenciasDetectadas", inconsistenciasDetectadas);
                
                if (inconsistenciasDetectadas > 0) {
                    resultado.put("alertas", "NFe importada com " + inconsistenciasDetectadas + 
                        " inconsistências. Verifique o relatório de inconsistências.");
                }
            } else {
                importacao.setStatus(StatusImportacao.ERRO);
                importacao.setQuantidadeNotasProcessadas(0);
                importacao.setQuantidadeNotasComErro(1);
                importacao.setErrosProcessamento(String.join("; ", processResult.getErros()));
                
                resultado.put("status", "ERRO");
                resultado.put("mensagem", processResult.getMensagem());
                resultado.put("erros", processResult.getErros());
            }
            
            importacaoNFeRepository.save(importacao);
            
        } catch (Exception e) {
            importacao.setStatus(StatusImportacao.ERRO);
            importacao.setDataFimProcessamento(new Date());
            importacao.setErrosProcessamento("Erro no processamento: " + e.getMessage());
            importacaoNFeRepository.save(importacao);
            
            resultado.put("status", "ERRO");
            resultado.put("mensagem", "Erro no processamento: " + e.getMessage());
        }
        
        resultado.put("importacaoId", importacao.getId());
        resultado.put("arquivo", file.getOriginalFilename());
        resultado.put("tamanho", file.getSize());
        
        return resultado;
    }
    
    private ImportacaoNFe criarRegistroImportacao(MultipartFile file, Unidade unidade, UsuarioAdministrador usuario) {
        ImportacaoNFe importacao = new ImportacaoNFe();
        
        importacao.setNomeArquivo(file.getOriginalFilename());
        importacao.setCaminhoArquivo("/temp/" + file.getOriginalFilename()); // Ajustar conforme necessário
        importacao.setStatus(StatusImportacao.PENDENTE);
        importacao.setDataImportacao(new Date());
        importacao.setQuantidadeNotasArquivo(0);
        importacao.setQuantidadeNotasProcessadas(0);
        importacao.setQuantidadeNotasComErro(0);
        importacao.setQuantidadeInconsistenciasDetectadas(0);
        
        if (unidade != null) {
            importacao.setUnidade(unidade);
        }
        if (usuario != null) {
            importacao.setUsuarioImportacao(usuario);
        }
        
        importacao.setAtivo(true);
        
        return importacaoNFeRepository.save(importacao);
    }
    
    private void validarArquivoNFe(MultipartFile file) {
        if (file == null) {
            throw new RuntimeException("Arquivo é obrigatório");
        }
        
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo XML não pode ser vazio");
        }
        
        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new RuntimeException("Nome do arquivo é obrigatório");
        }
        
        if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
            throw new RuntimeException("Apenas arquivos XML são aceitos");
        }
        
        // Validar tamanho do arquivo (máximo 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB em bytes
        if (file.getSize() > maxSize) {
            throw new RuntimeException("Arquivo muito grande. Tamanho máximo permitido: 10MB");
        }
        
        // Validar tipo de conteúdo
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("text/xml") && !contentType.equals("application/xml")) {
            throw new RuntimeException("Tipo de arquivo inválido. Esperado: XML");
        }
    }
    
    private void validarPermissoesUsuario(UsuarioAdministrador usuario, Unidade unidade) {
        if (usuario == null || unidade == null) {
            return; // Validação opcional para casos básicos
        }
        
        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário inativo não pode importar NFe");
        }
        
        if (!unidade.isAtivo()) {
            throw new RuntimeException("Não é possível importar NFe para unidade inativa");
        }
    }
    
    private void validarConteudoXml(MultipartFile file) {
        try {
            byte[] content = file.getBytes();
            String xmlContent = new String(content, "UTF-8");
            
            if (!xmlContent.trim().startsWith("<?xml") && !xmlContent.contains("<NFe")) {
                throw new RuntimeException("Arquivo não é um XML válido de NFe");
            }
            
            if (!xmlContent.contains("<infNFe")) {
                throw new RuntimeException("XML não contém estrutura de NFe válida");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar conteúdo do XML: " + e.getMessage());
        }
    }
    
    private void validarDadosNFe(NFeXmlData nfeData, Unidade unidade) {
        if (nfeData == null) {
            throw new RuntimeException("Dados da NFe não podem ser nulos");
        }
        
        if (nfeData.getChaveAcesso() == null || nfeData.getChaveAcesso().length() != 44) {
            throw new RuntimeException("Chave de acesso inválida");
        }
        
        if (nfeData.getNumero() == null || nfeData.getNumero().isEmpty()) {
            throw new RuntimeException("Número da NFe é obrigatório");
        }
        
        if (nfeData.getValorTotal() == null || nfeData.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor total da NFe deve ser maior que zero");
        }
        
        if (nfeData.getEmitente() == null) {
            throw new RuntimeException("Dados do emitente são obrigatórios");
        }
        
        if (nfeData.getItens() == null || nfeData.getItens().isEmpty()) {
            throw new RuntimeException("NFe deve conter pelo menos um item");
        }
        
        // Validações específicas por tipo de operação
        validarTipoOperacao(nfeData, unidade);
    }
    
    private void validarTipoOperacao(NFeXmlData nfeData, Unidade unidade) {
        if ("VENDA".equals(nfeData.getTipoOperacao()) && unidade != null) {
            // Para vendas, verificar se o destinatário é a própria unidade
            if (nfeData.getDestinatario() != null && unidade.getCnpj() != null) {
                if (!unidade.getCnpj().equals(nfeData.getDestinatario().getCnpjCpf())) {
                    throw new RuntimeException("NFe de venda deve ter a unidade como destinatário");
                }
            }
        }
    }
    
    private void verificarNFeDuplicada(String chaveAcesso, Unidade unidade) {
        // Implementar verificação de duplicação
        // Por enquanto, apenas log da verificação
        if (chaveAcesso != null && !chaveAcesso.isEmpty()) {
            // TODO: Implementar busca por chave de acesso no repository
            // Optional<NotaFiscal> existente = notaFiscalRepository.findByChaveAcesso(chaveAcesso);
            // if (existente.isPresent()) {
            //     throw new RuntimeException("NFe já importada: " + chaveAcesso);
            // }
        }
    }
    
    private int contarInconsistencias(ImportacaoNFe importacao) {
        // Buscar inconsistências relacionadas à importação
        try {
            // TODO: Implementar contagem via repository de inconsistências
            // return inconsistenciaRepository.countByImportacaoNFe(importacao);
            return 0; // Por enquanto retornar 0
        } catch (Exception e) {
            return 0;
        }
    }
}