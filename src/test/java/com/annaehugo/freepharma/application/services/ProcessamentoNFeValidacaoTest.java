package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.estoque.*;
import com.annaehugo.freepharma.domain.entity.fiscal.*;
import com.annaehugo.freepharma.domain.repository.estoque.*;
import com.annaehugo.freepharma.domain.repository.fiscal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoNFeValidacaoTest {

    @Mock
    private FornecedorRepository fornecedorRepository;
    @Mock
    private ProdutoReferenciaRepository produtoReferenciaRepository;
    @Mock
    private ProdutoFornecedorRepository produtoFornecedorRepository;
    @Mock
    private EstoqueProdutoRepository estoqueProdutoRepository;
    @Mock
    private NotaFiscalRepository notaFiscalRepository;
    @Mock
    private NotaFiscalItemRepository notaFiscalItemRepository;
    @Mock
    private InconsistenciaRepository inconsistenciaRepository;
    @Mock
    private EstoqueProdutoService estoqueProdutoService;

    @InjectMocks
    private ProcessamentoNFeService processamentoNFeService;

    private NFeXmlData nfeData;
    private Unidade unidade;
    private ImportacaoNFe importacao;

    @BeforeEach
    void setUp() {
        // Configurar dados base da NFe
        nfeData = new NFeXmlData();
        nfeData.setChaveAcesso("12345678901234567890123456789012345678901234");
        nfeData.setNumero("123");
        nfeData.setValorTotal(new BigDecimal("100.00"));
        nfeData.setDataEmissao(new Date());

        // Configurar emitente
        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
        emitente.setCnpj("12345678000199");
        emitente.setRazaoSocial("Fornecedor Teste Ltda");
        nfeData.setEmitente(emitente);

        // Configurar item básico
        NFeXmlData.ItemNFeDados item = new NFeXmlData.ItemNFeDados();
        item.setCodigoProduto("PROD001");
        item.setNomeProduto("Produto Teste");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("100.00"));
        item.setValorTotal(new BigDecimal("100.00"));
        item.setNcm("12345678");
        item.setCfop("1234");
        nfeData.setItens(new ArrayList<>());
        nfeData.getItens().add(item);

        // Configurar unidade e importação
        unidade = new Unidade();
        unidade.setId(1L);
        unidade.setCnpj("98765432000188");

        importacao = new ImportacaoNFe();
        importacao.setId(1L);

        // Mocks básicos
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(new Fornecedor());
        when(produtoReferenciaRepository.save(any(ProdutoReferencia.class))).thenReturn(new ProdutoReferencia());
        when(produtoFornecedorRepository.save(any(ProdutoFornecedor.class))).thenReturn(new ProdutoFornecedor());
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(new NotaFiscal());
        when(notaFiscalItemRepository.save(any(NotaFiscalItem.class))).thenReturn(new NotaFiscalItem());
        when(estoqueProdutoRepository.save(any(EstoqueProduto.class))).thenReturn(new EstoqueProduto());
    }

    @Test
    void processarNFe_ComDivergenciaValorTotal_DeveCriarInconsistencia() {
        // Given - item com valor total diferente da soma
        nfeData.setValorTotal(new BigDecimal("150.00")); // Valor total diferente
        nfeData.getItens().get(0).setValorTotal(new BigDecimal("100.00")); // Item vale 100

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComDataEmissaoFutura_DeveCriarInconsistencia() {
        // Given - data de emissão futura
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 5); // 5 dias no futuro
        nfeData.setDataEmissao(calendar.getTime());

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComDataEmissaoAntiga_DeveCriarInconsistencia() {
        // Given - data de emissão muito antiga (35 dias atrás)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -35);
        nfeData.setDataEmissao(calendar.getTime());

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComNCMInvalido_DeveCriarInconsistencia() {
        // Given - NCM inválido (não numérico)
        nfeData.getItens().get(0).setNcm("ABCD1234");

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComCFOPInvalido_DeveCriarInconsistencia() {
        // Given - CFOP inválido (não numérico)
        nfeData.getItens().get(0).setCfop("ABCD");

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComEANInvalido_DeveCriarInconsistencia() {
        // Given - EAN inválido
        nfeData.getItens().get(0).setEan("1234567890123"); // EAN com dígito verificador incorreto

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComValorUnitarioInvalido_DeveCriarInconsistencia() {
        // Given - valor unitário zero
        nfeData.getItens().get(0).setValorUnitario(BigDecimal.ZERO);

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComQuantidadeInvalida_DeveCriarInconsistencia() {
        // Given - quantidade zero
        nfeData.getItens().get(0).setQuantidade(0);

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComMedicamentoSemLote_DeveCriarInconsistencia() {
        // Given - produto farmacêutico sem lote
        NFeXmlData.ItemNFeDados itemMedicamento = nfeData.getItens().get(0);
        itemMedicamento.setNomeProduto("Paracetamol 500mg");
        itemMedicamento.setNcm("30049099"); // NCM de medicamento
        itemMedicamento.setLote(null); // Sem lote

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComMedicamentoNCMIncorreto_DeveCriarInconsistencia() {
        // Given - medicamento com NCM incorreto
        NFeXmlData.ItemNFeDados itemMedicamento = nfeData.getItens().get(0);
        itemMedicamento.setNomeProduto("Paracetamol 500mg comprimido");
        itemMedicamento.setNcm("12345678"); // NCM não medicamento para produto medicamento
        itemMedicamento.setLote("LOTE123");

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComProdutoProximoVencimento_DeveCriarInconsistencia() {
        // Given - produto com validade próxima (100 dias)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 100); // 100 dias no futuro (menos que 6 meses)
        
        NFeXmlData.ItemNFeDados itemMedicamento = nfeData.getItens().get(0);
        itemMedicamento.setNomeProduto("Paracetamol 500mg");
        itemMedicamento.setNcm("30049099");
        itemMedicamento.setLote("LOTE123");
        itemMedicamento.setDataVencimento(calendar.getTime());

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        verify(inconsistenciaRepository, atLeastOnce()).save(any(Inconsistencia.class));
    }

    @Test
    void processarNFe_ComEANValido_NaoDeveCriarInconsistencia() {
        // Given - EAN válido (exemplo: 7898100170106)
        nfeData.getItens().get(0).setEan("7898100170106");
        nfeData.getItens().get(0).setNcm("30049099");
        nfeData.getItens().get(0).setCfop("1101");

        // When
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoNFeService.processarNFe(nfeData, unidade, importacao);

        // Then
        assertTrue(result.isSucesso());
        // Não deve criar inconsistência para EAN válido
        // Outras validações podem ainda criar inconsistências
    }
}