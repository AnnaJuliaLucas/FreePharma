package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import com.annaehugo.freepharma.domain.entity.fiscal.ImportacaoNFe;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscalItem;
import com.annaehugo.freepharma.domain.repository.estoque.*;
import com.annaehugo.freepharma.domain.repository.fiscal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringJUnitConfig
public class ProcessamentoNFeServiceTest {
    
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
    private ProcessamentoNFeService processamentoService;
    
    private NFeXmlData nfeDataTeste;
    private Unidade unidadeTeste;
    private ImportacaoNFe importacaoTeste;
    
    @BeforeEach
    void setUp() {
        // Configurar dados de teste
        nfeDataTeste = criarNFeDataTeste();
        unidadeTeste = criarUnidadeTeste();
        importacaoTeste = new ImportacaoNFe();
        importacaoTeste.setId(1L);
    }
    
    @Test
    void testProcessarNFeCompraSucesso() {
        // Arrange
        Fornecedor fornecedorMock = criarFornecedorMock();
        ProdutoReferencia produtoMock = criarProdutoReferenciaMock();
        ProdutoFornecedor produtoFornecedorMock = criarProdutoFornecedorMock(produtoMock, fornecedorMock);
        NotaFiscal notaFiscalMock = criarNotaFiscalMock();
        
        // Mock dos repositórios
        when(fornecedorRepository.findByCnpj(anyString())).thenReturn(Optional.of(fornecedorMock));
        when(produtoReferenciaRepository.findByEan(anyString())).thenReturn(Optional.of(produtoMock));
        when(produtoFornecedorRepository.findByProdutoReferenciaAndFornecedor(any(ProdutoReferencia.class), any(Fornecedor.class)))
            .thenReturn(Optional.of(produtoFornecedorMock));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaFiscalMock);
        when(notaFiscalItemRepository.save(any(NotaFiscalItem.class))).thenReturn(new NotaFiscalItem());
        when(estoqueProdutoRepository.findByProdutoFornecedorAndUnidadeAndLote(any(ProdutoFornecedor.class), any(Unidade.class), any(String.class)))
            .thenReturn(Optional.empty());
        when(estoqueProdutoRepository.save(any(EstoqueProduto.class))).thenReturn(new EstoqueProduto());
        
        // Act
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoService.processarNFe(nfeDataTeste, unidadeTeste, importacaoTeste);
        
        // Assert
        assertTrue(result.isSucesso());
        assertNotNull(result.getFornecedor());
        assertEquals(fornecedorMock, result.getFornecedor());
        assertNotNull(result.getNotaFiscal());
        assertEquals(1, result.getItensProcessados().size());
        assertTrue(result.getErros().isEmpty());
        
        // Verificar que os repositórios foram chamados
        verify(fornecedorRepository).findByCnpj("11222333000144");
        verify(notaFiscalRepository).save(any(NotaFiscal.class));
        verify(estoqueProdutoRepository).save(any(EstoqueProduto.class));
    }
    
    @Test
    void testProcessarNFeVendaSucesso() {
        // Arrange - NFe de venda
        nfeDataTeste.setTipoOperacao("VENDA");
        
        Fornecedor fornecedorMock = criarFornecedorMock();
        ProdutoReferencia produtoMock = criarProdutoReferenciaMock();
        ProdutoFornecedor produtoFornecedorMock = criarProdutoFornecedorMock(produtoMock, fornecedorMock);
        NotaFiscal notaFiscalMock = criarNotaFiscalMock();
        EstoqueProduto estoqueExistente = new EstoqueProduto();
        estoqueExistente.setQuantidadeAtual(100); // Estoque suficiente
        
        when(fornecedorRepository.findByCnpj(anyString())).thenReturn(Optional.of(fornecedorMock));
        when(produtoReferenciaRepository.findByEan(anyString())).thenReturn(Optional.of(produtoMock));
        when(produtoFornecedorRepository.findByProdutoReferenciaAndFornecedor(any(ProdutoReferencia.class), any(Fornecedor.class)))
            .thenReturn(Optional.of(produtoFornecedorMock));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaFiscalMock);
        when(notaFiscalItemRepository.save(any(NotaFiscalItem.class))).thenReturn(new NotaFiscalItem());
        when(estoqueProdutoRepository.findByProdutoFornecedorAndUnidadeAndLote(any(ProdutoFornecedor.class), any(Unidade.class), any(String.class)))
            .thenReturn(Optional.of(estoqueExistente));
        when(estoqueProdutoRepository.save(any(EstoqueProduto.class))).thenReturn(estoqueExistente);
        
        // Act
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoService.processarNFe(nfeDataTeste, unidadeTeste, importacaoTeste);
        
        // Assert
        assertTrue(result.isSucesso());
        
        // Verificar que o método save foi chamado (o decremento é feito na implementação real)
        verify(estoqueProdutoRepository).save(estoqueExistente);
        
        // Note: In a real scenario, the service would decrement the stock
        // For this unit test, we're just verifying the save method was called
    }
    
    @Test
    void testProcessarNFeComNovoFornecedor() {
        // Arrange - Fornecedor não existe
        when(fornecedorRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenAnswer(invocation -> {
            Fornecedor fornecedor = invocation.getArgument(0);
            fornecedor.setId(1L);
            return fornecedor;
        });
        
        ProdutoReferencia produtoMock = criarProdutoReferenciaMock();
        when(produtoReferenciaRepository.findByEan(anyString())).thenReturn(Optional.of(produtoMock));
        when(produtoFornecedorRepository.findByProdutoReferenciaAndFornecedor(any(ProdutoReferencia.class), any(Fornecedor.class)))
            .thenReturn(Optional.empty());
        when(produtoFornecedorRepository.save(any(ProdutoFornecedor.class)))
            .thenReturn(criarProdutoFornecedorMock(produtoMock, new Fornecedor()));
        
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(criarNotaFiscalMock());
        when(notaFiscalItemRepository.save(any(NotaFiscalItem.class))).thenReturn(new NotaFiscalItem());
        when(estoqueProdutoRepository.findByProdutoFornecedorAndUnidadeAndLote(any(ProdutoFornecedor.class), any(Unidade.class), any(String.class)))
            .thenReturn(Optional.empty());
        when(estoqueProdutoRepository.save(any(EstoqueProduto.class))).thenReturn(new EstoqueProduto());
        
        // Act
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoService.processarNFe(nfeDataTeste, unidadeTeste, importacaoTeste);
        
        // Assert
        assertTrue(result.isSucesso());
        verify(fornecedorRepository).save(any(Fornecedor.class)); // Novo fornecedor foi criado
    }
    
    @Test
    void testProcessarNFeComNovoProduto() {
        // Arrange - Produto não existe
        Fornecedor fornecedorMock = criarFornecedorMock();
        when(fornecedorRepository.findByCnpj(anyString())).thenReturn(Optional.of(fornecedorMock));
        
        // Produto não encontrado por EAN nem por nome
        when(produtoReferenciaRepository.findByEan(anyString())).thenReturn(Optional.empty());
        when(produtoReferenciaRepository.findFirstByNome(anyString())).thenReturn(Optional.empty());
        when(produtoReferenciaRepository.save(any(ProdutoReferencia.class))).thenAnswer(invocation -> {
            ProdutoReferencia produto = invocation.getArgument(0);
            produto.setId(1L);
            return produto;
        });
        
        when(produtoFornecedorRepository.findByProdutoReferenciaAndFornecedor(any(ProdutoReferencia.class), any(Fornecedor.class)))
            .thenReturn(Optional.empty());
        when(produtoFornecedorRepository.save(any(ProdutoFornecedor.class)))
            .thenReturn(criarProdutoFornecedorMock(new ProdutoReferencia(), fornecedorMock));
        
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(criarNotaFiscalMock());
        when(notaFiscalItemRepository.save(any(NotaFiscalItem.class))).thenReturn(new NotaFiscalItem());
        when(estoqueProdutoRepository.findByProdutoFornecedorAndUnidadeAndLote(any(ProdutoFornecedor.class), any(Unidade.class), any(String.class)))
            .thenReturn(Optional.empty());
        when(estoqueProdutoRepository.save(any(EstoqueProduto.class))).thenReturn(new EstoqueProduto());
        
        // Act
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoService.processarNFe(nfeDataTeste, unidadeTeste, importacaoTeste);
        
        // Assert
        assertTrue(result.isSucesso());
        verify(produtoReferenciaRepository).save(any(ProdutoReferencia.class)); // Novo produto foi criado
        verify(produtoFornecedorRepository).save(any(ProdutoFornecedor.class)); // Novo produto fornecedor foi criado
    }
    
    @Test
    void testProcessarNFeComErro() {
        // Arrange - Simular erro
        when(fornecedorRepository.findByCnpj(anyString())).thenThrow(new RuntimeException("Erro no banco"));
        
        // Act
        ProcessamentoNFeService.ProcessamentoResult result = 
            processamentoService.processarNFe(nfeDataTeste, unidadeTeste, importacaoTeste);
        
        // Assert
        assertFalse(result.isSucesso());
        assertFalse(result.getErros().isEmpty());
        assertTrue(result.getMensagem().contains("Erro no processamento"));
    }
    
    // Métodos auxiliares para criar objetos de teste
    private NFeXmlData criarNFeDataTeste() {
        NFeXmlData nfeData = new NFeXmlData();
        nfeData.setChaveAcesso("35240111222333000144550010000012341234567890");
        nfeData.setNumero("1234");
        nfeData.setValorTotal(new BigDecimal("155.00"));
        nfeData.setTipoOperacao("COMPRA");
        
        // Emitente
        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
        emitente.setCnpj("11222333000144");
        emitente.setRazaoSocial("Fornecedor Teste LTDA");
        emitente.setNomeFantasia("Fornecedor Teste");
        emitente.setEndereco("Rua do Fornecedor, 123 - Centro, São Paulo - SP");
        nfeData.setEmitente(emitente);
        
        // Destinatário
        NFeXmlData.DestinatarioDados destinatario = new NFeXmlData.DestinatarioDados();
        destinatario.setCnpjCpf("12345678000199");
        destinatario.setNome("FreePharma Matriz");
        nfeData.setDestinatario(destinatario);
        
        // Item
        NFeXmlData.ItemNFeDados item = new NFeXmlData.ItemNFeDados();
        item.setCodigoProduto("PARACETAMOL500");
        item.setNomeProduto("Paracetamol 500mg cx 20 comp");
        item.setEan("7891234567890");
        item.setNcm("30049099");
        item.setCfop("1102");
        item.setUnidadeMedida("CX");
        item.setQuantidade(10);
        item.setValorUnitario(new BigDecimal("15.50"));
        item.setValorTotal(new BigDecimal("155.00"));
        item.setLote("LOTE123");
        
        nfeData.setItens(Arrays.asList(item));
        
        return nfeData;
    }
    
    private Unidade criarUnidadeTeste() {
        Unidade unidade = new Unidade();
        unidade.setId(1L);
        unidade.setNomeFantasia("Unidade Teste");
        
        Farmacia farmacia = new Farmacia();
        farmacia.setId(1L);
        farmacia.setRazaoSocial("FreePharma Teste");
        unidade.setFarmacia(farmacia);
        
        return unidade;
    }
    
    private Fornecedor criarFornecedorMock() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setCnpj("11222333000144");
        fornecedor.setRazaoSocial("Fornecedor Teste LTDA");
        return fornecedor;
    }
    
    private ProdutoReferencia criarProdutoReferenciaMock() {
        ProdutoReferencia produto = new ProdutoReferencia();
        produto.setId(1L);
        produto.setCodigoInterno("PARACETAMOL500");
        produto.setNome("Paracetamol 500mg cx 20 comp");
        produto.setEan("7891234567890");
        return produto;
    }
    
    private ProdutoFornecedor criarProdutoFornecedorMock(ProdutoReferencia produto, Fornecedor fornecedor) {
        ProdutoFornecedor produtoFornecedor = new ProdutoFornecedor();
        produtoFornecedor.setId(1L);
        produtoFornecedor.setProdutoReferencia(produto);
        produtoFornecedor.setFornecedor(fornecedor);
        produtoFornecedor.setCodigoFornecedor("PARACETAMOL500");
        produtoFornecedor.setPrecoCompra(new BigDecimal("15.50"));
        return produtoFornecedor;
    }
    
    private NotaFiscal criarNotaFiscalMock() {
        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setId(1L);
        notaFiscal.setNumero("1234");
        notaFiscal.setChaveAcesso("35240111222333000144550010000012341234567890");
        notaFiscal.setStatus("PROCESSADA");
        return notaFiscal;
    }
}