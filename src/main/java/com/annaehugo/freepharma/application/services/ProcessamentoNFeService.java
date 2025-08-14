package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.estoque.*;
import com.annaehugo.freepharma.domain.entity.fiscal.*;
import com.annaehugo.freepharma.domain.repository.estoque.*;
import com.annaehugo.freepharma.domain.repository.fiscal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service para processamento automático de NFe com criação de entidades e controle de estoque
 */
@Service
public class ProcessamentoNFeService {
    
    @Autowired
    private FornecedorRepository fornecedorRepository;
    
    @Autowired
    private ProdutoReferenciaRepository produtoReferenciaRepository;
    
    @Autowired
    private ProdutoFornecedorRepository produtoFornecedorRepository;
    
    @Autowired
    private EstoqueProdutoRepository estoqueProdutoRepository;
    
    @Autowired
    private NotaFiscalRepository notaFiscalRepository;
    
    @Autowired
    private NotaFiscalItemRepository notaFiscalItemRepository;
    
    @Autowired
    private InconsistenciaRepository inconsistenciaRepository;
    
    @Autowired
    private EstoqueProdutoService estoqueProdutoService;
    
    /**
     * Processa automaticamente uma NFe:
     * 1. Cria/busca fornecedor
     * 2. Cria/busca produtos 
     * 3. Cria nota fiscal
     * 4. Atualiza estoque
     * 5. Detecta inconsistências
     */
    @Transactional
    public ProcessamentoResult processarNFe(NFeXmlData nfeData, Unidade unidade, ImportacaoNFe importacao) {
        ProcessamentoResult result = new ProcessamentoResult();
        
        try {
            // 1. Processar fornecedor
            Fornecedor fornecedor = processarFornecedor(nfeData.getEmitente());
            result.setFornecedor(fornecedor);
            
            // 2. Criar nota fiscal
            NotaFiscal notaFiscal = criarNotaFiscal(nfeData, fornecedor, unidade, importacao);
            result.setNotaFiscal(notaFiscal);
            
            // 3. Processar itens da nota
            List<NotaFiscalItem> itens = new ArrayList<>();
            for (NFeXmlData.ItemNFeDados itemData : nfeData.getItens()) {
                try {
                    // Processar produto
                    ProdutoFornecedor produtoFornecedor = processarProduto(itemData, fornecedor);
                    
                    // Criar item da nota fiscal
                    NotaFiscalItem item = criarItemNotaFiscal(itemData, notaFiscal, produtoFornecedor);
                    itens.add(item);
                    
                    // Atualizar estoque
                    atualizarEstoque(produtoFornecedor, itemData, unidade, nfeData.getTipoOperacao());
                    
                    result.getItensProcessados().add(item);
                    
                } catch (Exception e) {
                    // Registrar inconsistência para item específico
                    criarInconsistencia("ERRO_PROCESSAMENTO_ITEM", 
                        "Erro ao processar item " + itemData.getCodigoProduto() + ": " + e.getMessage(),
                        "ALTA", notaFiscal);
                    result.getErros().add("Item " + itemData.getCodigoProduto() + ": " + e.getMessage());
                }
            }
            
            // 4. Validar consistência geral da nota
            validarConsistenciaNota(nfeData, notaFiscal, result);
            
            result.setSucesso(true);
            result.setMensagem("NFe processada com sucesso. " + itens.size() + " itens processados.");
            
        } catch (Exception e) {
            result.setSucesso(false);
            result.setMensagem("Erro no processamento: " + e.getMessage());
            result.getErros().add(e.getMessage());
        }
        
        return result;
    }
    
    private Fornecedor processarFornecedor(NFeXmlData.EmitenteDados emitenteData) {
        // Buscar fornecedor existente por CNPJ
        Optional<Fornecedor> fornecedorExistente = fornecedorRepository.findByCnpj(emitenteData.getCnpj());
        
        if (fornecedorExistente.isPresent()) {
            // Atualizar dados se necessário
            Fornecedor fornecedor = fornecedorExistente.get();
            atualizarDadosFornecedor(fornecedor, emitenteData);
            return fornecedorRepository.save(fornecedor);
        } else {
            // Criar novo fornecedor
            Fornecedor novoFornecedor = new Fornecedor();
            preencherDadosFornecedor(novoFornecedor, emitenteData);
            return fornecedorRepository.save(novoFornecedor);
        }
    }
    
    private void preencherDadosFornecedor(Fornecedor fornecedor, NFeXmlData.EmitenteDados dados) {
        fornecedor.setRazaoSocial(dados.getRazaoSocial());
        fornecedor.setNomeFantasia(dados.getNomeFantasia());
        fornecedor.setCnpj(dados.getCnpj());
        fornecedor.setInscricaoEstadual(dados.getInscricaoEstadual());
        fornecedor.setEndereco(dados.getEndereco());
        fornecedor.setTelefone(dados.getTelefone());
        fornecedor.setEmail(dados.getEmail());
        fornecedor.setStatus("ATIVO");
        fornecedor.setDataCadastro(new Date());
        fornecedor.setAtivo(true);
        
        // Extrair cidade e estado do endereço (lógica simplificada)
        if (dados.getEndereco() != null && dados.getEndereco().contains(",")) {
            String[] parts = dados.getEndereco().split(",");
            if (parts.length >= 2) {
                String ultimaParte = parts[parts.length - 1].trim();
                if (ultimaParte.contains(" - ")) {
                    String[] cidadeEstado = ultimaParte.split(" - ");
                    if (cidadeEstado.length >= 2) {
                        fornecedor.setCidade(cidadeEstado[0].trim());
                        fornecedor.setEstado(cidadeEstado[1].trim());
                    }
                }
            }
        }
    }
    
    private void atualizarDadosFornecedor(Fornecedor fornecedor, NFeXmlData.EmitenteDados dados) {
        // Atualizar dados que podem ter mudado
        if (dados.getNomeFantasia() != null && !dados.getNomeFantasia().equals(fornecedor.getNomeFantasia())) {
            fornecedor.setNomeFantasia(dados.getNomeFantasia());
        }
        if (dados.getEndereco() != null && !dados.getEndereco().equals(fornecedor.getEndereco())) {
            fornecedor.setEndereco(dados.getEndereco());
        }
        if (dados.getTelefone() != null && !dados.getTelefone().equals(fornecedor.getTelefone())) {
            fornecedor.setTelefone(dados.getTelefone());
        }
        if (dados.getEmail() != null && !dados.getEmail().equals(fornecedor.getEmail())) {
            fornecedor.setEmail(dados.getEmail());
        }
    }
    
    private ProdutoFornecedor processarProduto(NFeXmlData.ItemNFeDados itemData, Fornecedor fornecedor) {
        // Buscar produto referência existente
        ProdutoReferencia produtoReferencia = buscarOuCriarProdutoReferencia(itemData);
        
        // Buscar produto fornecedor existente
        Optional<ProdutoFornecedor> produtoFornecedorExistente = 
            produtoFornecedorRepository.findByProdutoReferenciaAndFornecedor(produtoReferencia, fornecedor);
        
        if (produtoFornecedorExistente.isPresent()) {
            // Atualizar preços se necessário
            ProdutoFornecedor produtoFornecedor = produtoFornecedorExistente.get();
            if (itemData.getValorUnitario() != null) {
                produtoFornecedor.setPrecoCompra(itemData.getValorUnitario());
                produtoFornecedor.setDataUltimaCompra(new Date());
            }
            return produtoFornecedorRepository.save(produtoFornecedor);
        } else {
            // Criar novo produto fornecedor
            ProdutoFornecedor novoProdutoFornecedor = new ProdutoFornecedor();
            novoProdutoFornecedor.setProdutoReferencia(produtoReferencia);
            novoProdutoFornecedor.setFornecedor(fornecedor);
            novoProdutoFornecedor.setCodigoFornecedor(itemData.getCodigoProduto());
            novoProdutoFornecedor.setNomeFornecedor(itemData.getNomeProduto());
            novoProdutoFornecedor.setPrecoCompra(itemData.getValorUnitario() != null ? itemData.getValorUnitario() : BigDecimal.ZERO);
            novoProdutoFornecedor.setUnidadeMedidaFornecedor(itemData.getUnidadeMedida());
            novoProdutoFornecedor.setEanFornecedor(itemData.getEan());
            novoProdutoFornecedor.setDataUltimaCompra(new Date());
            novoProdutoFornecedor.setAtivo(true);
            
            return produtoFornecedorRepository.save(novoProdutoFornecedor);
        }
    }
    
    private ProdutoReferencia buscarOuCriarProdutoReferencia(NFeXmlData.ItemNFeDados itemData) {
        // Tentar buscar por EAN primeiro
        if (itemData.getEan() != null && !itemData.getEan().trim().isEmpty() && 
            !itemData.getEan().equals("SEM GTIN")) {
            Optional<ProdutoReferencia> porEan = produtoReferenciaRepository.findByEan(itemData.getEan());
            if (porEan.isPresent()) {
                return porEan.get();
            }
        }
        
        // Tentar buscar por nome similar
        if (itemData.getNomeProduto() != null && !itemData.getNomeProduto().trim().isEmpty()) {
            Optional<ProdutoReferencia> porNome = produtoReferenciaRepository.findFirstByNome(itemData.getNomeProduto());
            if (porNome.isPresent()) {
                return porNome.get();
            }
        }
        
        // Criar novo produto referência
        ProdutoReferencia novoProduto = new ProdutoReferencia();
        
        // Gerar código interno único
        String codigoInterno = gerarCodigoInternoUnico();
        novoProduto.setCodigoInterno(codigoInterno);
        
        novoProduto.setNome(itemData.getNomeProduto());
        novoProduto.setDescricao(itemData.getDescricaoProduto());
        novoProduto.setEan(itemData.getEan());
        novoProduto.setNcm(itemData.getNcm());
        novoProduto.setCfop(itemData.getCfop());
        novoProduto.setUnidadeMedida(itemData.getUnidadeMedida() != null ? itemData.getUnidadeMedida() : "UN");
        novoProduto.setValidade(itemData.getDataVencimento());
        novoProduto.setStatus("ATIVO");
        
        return produtoReferenciaRepository.save(novoProduto);
    }
    
    private String gerarCodigoInternoUnico() {
        long timestamp = System.currentTimeMillis();
        return "AUTO" + String.valueOf(timestamp).substring(6); // Usar últimos 7 dígitos
    }
    
    private NotaFiscal criarNotaFiscal(NFeXmlData nfeData, Fornecedor fornecedor, Unidade unidade, ImportacaoNFe importacao) {
        NotaFiscal notaFiscal = new NotaFiscal();
        
        notaFiscal.setNumero(nfeData.getNumero());
        notaFiscal.setChaveAcesso(nfeData.getChaveAcesso());
        notaFiscal.setStatus("PROCESSADA");
        notaFiscal.setDataEmissao(nfeData.getDataEmissao());
        notaFiscal.setValorTotal(nfeData.getValorTotal());
        notaFiscal.setTipoOperacao(nfeData.getTipoOperacao());
        notaFiscal.setFornecedor(fornecedor);
        notaFiscal.setUnidade(unidade);
        notaFiscal.setFarmaciaId(unidade.getFarmacia().getId());
        notaFiscal.setImportacaoNFe(importacao);
        notaFiscal.setAtivo(true);
        
        return notaFiscalRepository.save(notaFiscal);
    }
    
    private NotaFiscalItem criarItemNotaFiscal(NFeXmlData.ItemNFeDados itemData, NotaFiscal notaFiscal, ProdutoFornecedor produtoFornecedor) {
        NotaFiscalItem item = new NotaFiscalItem();
        
        item.setQuantidade(BigDecimal.valueOf(itemData.getQuantidade()));
        item.setValorUnitario(itemData.getValorUnitario());
        item.setNotaFiscal(notaFiscal);
        item.setProdutoReferencia(produtoFornecedor.getProdutoReferencia());
        item.setAtivo(true);
        
        return notaFiscalItemRepository.save(item);
    }
    
    private void atualizarEstoque(ProdutoFornecedor produtoFornecedor, NFeXmlData.ItemNFeDados itemData, 
                                  Unidade unidade, String tipoOperacao) {
        
        // Buscar estoque existente
        Optional<EstoqueProduto> estoqueExistente = estoqueProdutoRepository
            .findByProdutoFornecedorAndUnidadeAndLote(produtoFornecedor, unidade, itemData.getLote());
        
        EstoqueProduto estoque;
        if (estoqueExistente.isPresent()) {
            estoque = estoqueExistente.get();
        } else {
            // Criar novo registro de estoque
            estoque = new EstoqueProduto();
            estoque.setProdutoFornecedor(produtoFornecedor);
            estoque.setProdutoReferencia(produtoFornecedor.getProdutoReferencia());
            estoque.setUnidade(unidade);
            estoque.setQuantidadeAtual(0);
            estoque.setLote(itemData.getLote());
            estoque.setDataVencimento(itemData.getDataVencimento());
            estoque.setAtivo(true);
        }
        
        // Atualizar quantidade baseado no tipo de operação
        int quantidade = itemData.getQuantidade();
        if ("COMPRA".equals(tipoOperacao)) {
            // Incrementar estoque (entrada)
            estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() + quantidade);
        } else if ("VENDA".equals(tipoOperacao)) {
            // Decrementar estoque (saída)
            estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() - quantidade);
        }
        
        // Atualizar valores
        if (itemData.getValorUnitario() != null) {
            estoque.setValorUnitario(itemData.getValorUnitario());
            estoque.setValorTotal(itemData.getValorUnitario()
                .multiply(BigDecimal.valueOf(estoque.getQuantidadeAtual())));
        }
        
        estoque.setDataUltimaMovimentacao(new Date());
        
        estoqueProdutoRepository.save(estoque);
    }
    
    private void validarConsistenciaNota(NFeXmlData nfeData, NotaFiscal notaFiscal, ProcessamentoResult result) {
        // Validar valor total
        BigDecimal somaItens = nfeData.getItens().stream()
            .map(NFeXmlData.ItemNFeDados::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (nfeData.getValorTotal().subtract(somaItens).abs().compareTo(new BigDecimal("0.01")) > 0) {
            criarInconsistencia("DIVERGENCIA_VALOR_TOTAL",
                "Valor total da nota (" + nfeData.getValorTotal() + 
                ") diverge da soma dos itens (" + somaItens + ")",
                "MEDIA", notaFiscal);
        }
        
        // Validar data de emissão
        if (nfeData.getDataEmissao() != null) {
            Date hoje = new Date();
            long diffEmDias = (hoje.getTime() - nfeData.getDataEmissao().getTime()) / (24 * 60 * 60 * 1000);
            
            if (diffEmDias > 30) {
                criarInconsistencia("DATA_EMISSAO_ANTIGA",
                    "NFe com data de emissão superior a 30 dias: " + nfeData.getDataEmissao(),
                    "BAIXA", notaFiscal);
            }
            
            if (diffEmDias < -1) {
                criarInconsistencia("DATA_EMISSAO_FUTURA",
                    "NFe com data de emissão futura: " + nfeData.getDataEmissao(),
                    "ALTA", notaFiscal);
            }
        }
        
        // Validar NCMs e CFOPs
        for (NFeXmlData.ItemNFeDados item : nfeData.getItens()) {
            validarItemNFe(item, notaFiscal);
        }
        
        // Validar consistência fiscal específica para farmácias
        validarConsistenciaFarmaceutica(nfeData, notaFiscal);
    }
    
    private void validarItemNFe(NFeXmlData.ItemNFeDados item, NotaFiscal notaFiscal) {
        // Validar NCM
        if (item.getNcm() == null || item.getNcm().length() != 8 || !item.getNcm().matches("\\d{8}")) {
            criarInconsistencia("NCM_INVALIDO",
                "NCM inválido para produto " + item.getNomeProduto() + ": " + item.getNcm(),
                "ALTA", notaFiscal);
        }
        
        // Validar CFOP
        if (item.getCfop() == null || item.getCfop().length() != 4 || !item.getCfop().matches("\\d{4}")) {
            criarInconsistencia("CFOP_INVALIDO",
                "CFOP inválido para produto " + item.getNomeProduto() + ": " + item.getCfop(),
                "ALTA", notaFiscal);
        }
        
        // Validar EAN/GTIN
        if (item.getEan() != null && !item.getEan().equals("SEM GTIN")) {
            if (!validarEAN(item.getEan())) {
                criarInconsistencia("EAN_INVALIDO",
                    "EAN/GTIN inválido para produto " + item.getNomeProduto() + ": " + item.getEan(),
                    "MEDIA", notaFiscal);
            }
        }
        
        // Validar valores
        if (item.getValorUnitario() == null || item.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            criarInconsistencia("VALOR_UNITARIO_INVALIDO",
                "Valor unitário inválido para produto " + item.getNomeProduto(),
                "ALTA", notaFiscal);
        }
        
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            criarInconsistencia("QUANTIDADE_INVALIDA",
                "Quantidade inválida para produto " + item.getNomeProduto(),
                "ALTA", notaFiscal);
        }
        
        // Validar lote e validade para produtos farmacêuticos
        if (isProdutoFarmaceutico(item) && item.getLote() == null) {
            criarInconsistencia("LOTE_OBRIGATORIO",
                "Lote obrigatório para produto farmacêutico: " + item.getNomeProduto(),
                "ALTA", notaFiscal);
        }
    }
    
    private void validarConsistenciaFarmaceutica(NFeXmlData nfeData, NotaFiscal notaFiscal) {
        // Validar NCMs específicos de medicamentos
        for (NFeXmlData.ItemNFeDados item : nfeData.getItens()) {
            if (isProdutoFarmaceutico(item)) {
                // Medicamentos devem ter NCM específico (30xx.xxxx)
                if (!item.getNcm().startsWith("30")) {
                    criarInconsistencia("NCM_MEDICAMENTO_INCORRETO",
                        "NCM incorreto para medicamento: " + item.getNomeProduto() + 
                        " - NCM: " + item.getNcm(),
                        "MEDIA", notaFiscal);
                }
                
                // Verificar validade
                if (item.getDataVencimento() != null) {
                    Date hoje = new Date();
                    long diffEmDias = (item.getDataVencimento().getTime() - hoje.getTime()) / (24 * 60 * 60 * 1000);
                    
                    if (diffEmDias < 180) { // Menos de 6 meses
                        criarInconsistencia("PRODUTO_PROXIMO_VENCIMENTO",
                            "Produto próximo ao vencimento: " + item.getNomeProduto() + 
                            " - Validade: " + item.getDataVencimento(),
                            "MEDIA", notaFiscal);
                    }
                }
            }
        }
    }
    
    private boolean validarEAN(String ean) {
        if (ean == null || ean.length() != 13) {
            return false;
        }
        
        try {
            // Validação simples de EAN-13
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                int digito = Character.getNumericValue(ean.charAt(i));
                soma += (i % 2 == 0) ? digito : digito * 3;
            }
            
            int verificador = (10 - (soma % 10)) % 10;
            return verificador == Character.getNumericValue(ean.charAt(12));
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isProdutoFarmaceutico(NFeXmlData.ItemNFeDados item) {
        if (item.getNcm() != null && item.getNcm().startsWith("30")) {
            return true;
        }
        
        String nome = item.getNomeProduto() != null ? item.getNomeProduto().toLowerCase() : "";
        return nome.contains("medicament") || nome.contains("remedio") || 
               nome.contains("farmaco") || nome.contains("comprimido") || 
               nome.contains("capsula") || nome.contains("xarope") ||
               nome.contains("pomada") || nome.contains("creme");
    }
    
    private void criarInconsistencia(String tipoString, String descricao, String severidade, NotaFiscal notaFiscal) {
        Inconsistencia inconsistencia = new Inconsistencia();
        
        // Mapear string para enum
        TipoInconsistencia tipo = mapearTipoInconsistencia(tipoString);
        inconsistencia.setTipo(tipo);
        inconsistencia.setDescricao(descricao);
        inconsistencia.setSeveridade(severidade);
        inconsistencia.setStatus("PENDENTE");
        inconsistencia.setNotaFiscal(notaFiscal);
        inconsistencia.setDataDeteccao(new Date());
        inconsistencia.setAtivo(true);
        
        inconsistenciaRepository.save(inconsistencia);
    }
    
    private TipoInconsistencia mapearTipoInconsistencia(String tipoString) {
        switch (tipoString) {
            case "ERRO_PROCESSAMENTO_ITEM":
                return TipoInconsistencia.PRODUTO_NAO_CADASTRADO;
            case "DIVERGENCIA_VALOR_TOTAL":
                return TipoInconsistencia.VALOR_TOTAL_DIVERGENTE;
            case "NCM_INVALIDO":
            case "NCM_MEDICAMENTO_INCORRETO":
                return TipoInconsistencia.NCM_INVALIDO;
            case "CFOP_INVALIDO":
            case "CFOP_INCORRETO":
                return TipoInconsistencia.CFOP_INCORRETO;
            case "PRECO_DIVERGENTE":
            case "VALOR_UNITARIO_INVALIDO":
                return TipoInconsistencia.PRECO_DIVERGENTE;
            case "EAN_INVALIDO":
                return TipoInconsistencia.PRODUTO_NAO_CADASTRADO; // Usar como fallback
            case "DATA_EMISSAO_ANTIGA":
            case "DATA_EMISSAO_FUTURA":
            case "PRODUTO_PROXIMO_VENCIMENTO":
            case "LOTE_OBRIGATORIO":
            case "QUANTIDADE_INVALIDA":
                return TipoInconsistencia.PRODUTO_NAO_CADASTRADO; // Usar como fallback
            default:
                return TipoInconsistencia.PRODUTO_NAO_CADASTRADO; // Default
        }
    }
    
    /**
     * Classe para retornar resultado do processamento
     */
    public static class ProcessamentoResult {
        private boolean sucesso;
        private String mensagem;
        private Fornecedor fornecedor;
        private NotaFiscal notaFiscal;
        private List<NotaFiscalItem> itensProcessados = new ArrayList<>();
        private List<String> erros = new ArrayList<>();
        
        // Getters e setters
        public boolean isSucesso() { return sucesso; }
        public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
        
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
        
        public Fornecedor getFornecedor() { return fornecedor; }
        public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
        
        public NotaFiscal getNotaFiscal() { return notaFiscal; }
        public void setNotaFiscal(NotaFiscal notaFiscal) { this.notaFiscal = notaFiscal; }
        
        public List<NotaFiscalItem> getItensProcessados() { return itensProcessados; }
        public void setItensProcessados(List<NotaFiscalItem> itensProcessados) { this.itensProcessados = itensProcessados; }
        
        public List<String> getErros() { return erros; }
        public void setErros(List<String> erros) { this.erros = erros; }
    }
}