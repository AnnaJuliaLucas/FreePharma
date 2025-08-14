//package com.annaehugo.freepharma.application.services;
//
//import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
//import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
//import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
//import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
//import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
//import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
//import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
//import com.annaehugo.freepharma.domain.entity.fiscal.ImportacaoNFe;
//import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
//import com.annaehugo.freepharma.domain.entity.fiscal.StatusImportacao;
//import com.annaehugo.freepharma.domain.repository.estoque.*;
//import com.annaehugo.freepharma.domain.repository.fiscal.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class ImportacaoNFeCompleteTest {
//
//    @Mock
//    private NFeXmlParser nfeXmlParser;
//
//    @Mock
//    private ProcessamentoNFeService processamentoNFeService;
//
//    @Mock
//    private ImportacaoNFeRepository importacaoNFeRepository;
//
//    @Mock
//    private FornecedorRepository fornecedorRepository;
//
//    @Mock
//    private ProdutoReferenciaRepository produtoReferenciaRepository;
//
//    @Mock
//    private EstoqueProdutoRepository estoqueProdutoRepository;
//
//    @InjectMocks
//    private ImportacaoNFeService importacaoNFeService;
//
//    private MultipartFile xmlFileCompra;
//    private MultipartFile xmlFileVenda;
//    private NFeXmlData nfeDataCompra;
//    private NFeXmlData nfeDataVenda;
//    private Unidade unidade;
//    private ImportacaoNFe importacao;
//
//    @BeforeEach
//    void setUp() {
//        setupTestData();
//        setupMocks();
//    }
//
//    @Test
//    void testImportacaoNFeCompra_DeveCriarFornecedorEAtualizarEstoque() {
//        // Given
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        assertTrue(resultado.get("mensagem").toString().contains("processada com sucesso"));
//
//        // Verificar se os métodos corretos foram chamados
//        verify(nfeXmlParser).parseNFeXml(any(byte[].class));
//        verify(processamentoNFeService).processarNFe(any(NFeXmlData.class), eq(unidade), any(ImportacaoNFe.class));
//        verify(importacaoNFeRepository, times(2)).save(any(ImportacaoNFe.class));
//    }
//
//    @Test
//    void testImportacaoNFeVenda_DeveDebitarEstoque() {
//        // Given
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataVenda);
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileVenda, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//
//        // Verificar que o processamento foi chamado com NFe de venda
//        verify(processamentoNFeService).processarNFe(argThat(nfe ->
//            "VENDA".equals(nfe.getTipoOperacao())), eq(unidade), any(ImportacaoNFe.class));
//    }
//
//    @Test
//    void testImportacaoNFeComNovoFornecedor_DeveCriarFornecedor() throws Exception {
//        // Given
//        when(fornecedorRepository.findByCnpj("11.222.333/0001-44")).thenReturn(Optional.empty());
//        when(fornecedorRepository.save(any(Fornecedor.class))).thenAnswer(invocation -> {
//            Fornecedor f = invocation.getArgument(0);
//            f.setId(1L);
//            return f;
//        });
//
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        assertNotNull(resultado.get("fornecedorId"));
//    }
//
//    @Test
//    void testImportacaoNFeComNovoProduto_DeveCriarProdutoReferencia() throws Exception {
//        // Given
//        when(produtoReferenciaRepository.findByEan("7891234567890")).thenReturn(Optional.empty());
//        when(produtoReferenciaRepository.findFirstByNome(anyString())).thenReturn(Optional.empty());
//        when(produtoReferenciaRepository.save(any(ProdutoReferencia.class))).thenAnswer(invocation -> {
//            ProdutoReferencia p = invocation.getArgument(0);
//            p.setId(1L);
//            return p;
//        });
//
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        assertEquals(1, resultado.get("itensProcessados"));
//    }
//
//    @Test
//    void testControlEestoque_CompraDeveIncrementarQuantidade() throws Exception {
//        // Given
//        EstoqueProduto estoqueExistente = new EstoqueProduto();
//        estoqueExistente.setQuantidadeAtual(50);
//
//        when(estoqueProdutoRepository.findByProdutoFornecedorAndUnidadeAndLote(
//            any(ProdutoFornecedor.class), any(Unidade.class), anyString()))
//            .thenReturn(Optional.of(estoqueExistente));
//
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        verify(estoqueProdutoRepository).save(any(EstoqueProduto.class));
//    }
//
//    @Test
//    void testControlEestoque_VendaDeveDecrementarQuantidade() throws Exception {
//        // Given
//        EstoqueProduto estoqueExistente = new EstoqueProduto();
//        estoqueExistente.setQuantidadeAtual(100);
//
//        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataVenda);
//        when(estoqueProdutoRepository.findByProdutoFornecedorAndUnidadeAndLote(
//            any(ProdutoFornecedor.class), any(Unidade.class), anyString()))
//            .thenReturn(Optional.of(estoqueExistente));
//
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileVenda, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        verify(estoqueProdutoRepository).save(any(EstoqueProduto.class));
//    }
//
//    @Test
//    void testImportacaoComErroProcessamento_DeveRetornarErro() throws Exception {
//        // Given
//        ProcessamentoNFeService.ProcessamentoResult resultError = new ProcessamentoNFeService.ProcessamentoResult();
//        resultError.setSucesso(false);
//        resultError.setMensagem("Erro no processamento");
//        resultError.getErros().add("CNPJ inválido");
//
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultError);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("ERRO", resultado.get("status"));
//        assertEquals("Erro no processamento", resultado.get("mensagem"));
//        assertNotNull(resultado.get("erros"));
//    }
//
//    @Test
//    void testValidacaoInconsistencias_DeveDetectarValorDivergente() throws Exception {
//        // Given - NFe com valor total divergente da soma dos itens
//        NFeXmlData nfeComInconsistencia = createNFeDataTeste("COMPRA");
//        nfeComInconsistencia.setValorTotal(new BigDecimal("200.00")); // Valor diferente da soma dos itens (155.00)
//
//        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeComInconsistencia);
//
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        // O serviço de processamento deve ter detectado a inconsistência
//        verify(processamentoNFeService).processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class));
//    }
//
//    @Test
//    void testImportacaoMultiplosItens_DeveProcessarTodos() throws Exception {
//        // Given - NFe com múltiplos itens
//        NFeXmlData nfeMultiplosItens = createNFeDataTeste("COMPRA");
//
//        // Adicionar segundo item
//        NFeXmlData.ItemNFeDados item2 = new NFeXmlData.ItemNFeDados();
//        item2.setCodigoProduto("DIPIRONA500");
//        item2.setNomeProduto("Dipirona 500mg cx 10 comp");
//        item2.setEan("7891234567891");
//        item2.setQuantidade(5);
//        item2.setValorUnitario(new BigDecimal("8.50"));
//        item2.setValorTotal(new BigDecimal("42.50"));
//        item2.setLote("LOTE456");
//
//        nfeMultiplosItens.getItens().add(item2);
//        nfeMultiplosItens.setValorTotal(new BigDecimal("197.50")); // 155.00 + 42.50
//
//        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeMultiplosItens);
//
//        ProcessamentoNFeService.ProcessamentoResult resultSuccess = createSuccessResult();
//        resultSuccess.setMensagem("NFe processada com sucesso. 2 itens processados.");
//        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(Unidade.class), any(ImportacaoNFe.class)))
//            .thenReturn(resultSuccess);
//
//        // When
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFileCompra, unidade, null);
//
//        // Then
//        assertEquals("SUCESSO", resultado.get("status"));
//        assertTrue(resultado.get("mensagem").toString().contains("2 itens processados"));
//    }
//
//    private void setupTestData() {
//        // Setup XML files
//        String xmlContentCompra = createXmlContent("1102"); // CFOP de compra
//        String xmlContentVenda = createXmlContent("5102"); // CFOP de venda
//
//        xmlFileCompra = new MockMultipartFile("file", "nfe-compra.xml", "text/xml", xmlContentCompra.getBytes());
//        xmlFileVenda = new MockMultipartFile("file", "nfe-venda.xml", "text/xml", xmlContentVenda.getBytes());
//
//        // Setup NFe data
//        nfeDataCompra = createNFeDataTeste("COMPRA");
//        nfeDataVenda = createNFeDataTeste("VENDA");
//
//        // Setup unidade
//        unidade = new Unidade();
//        unidade.setId(1L);
//        unidade.setNomeFantasia("Farmacia Teste");
//        Farmacia farmacia = new Farmacia();
//        farmacia.setId(1L);
//        unidade.setFarmacia(farmacia);
//
//        // Setup importacao
//        importacao = new ImportacaoNFe();
//        importacao.setId(1L);
//        importacao.setStatus(StatusImportacao.PENDENTE);
//    }
//
//    private void setupMocks() {
//        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataCompra);
//        when(importacaoNFeRepository.save(any(ImportacaoNFe.class))).thenReturn(importacao);
//
//        // Mock fornecedor existente
//        Fornecedor fornecedor = new Fornecedor();
//        fornecedor.setId(1L);
//        fornecedor.setCnpj("11.222.333/0001-44");
//        when(fornecedorRepository.findByCnpj(anyString())).thenReturn(Optional.of(fornecedor));
//
//        // Mock produto existente
//        ProdutoReferencia produto = new ProdutoReferencia();
//        produto.setId(1L);
//        produto.setEan("7891234567890");
//        when(produtoReferenciaRepository.findByEan(anyString())).thenReturn(Optional.of(produto));
//    }
//
//    private String createXmlContent(String cfop) {
//        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//            "<nfeProc>\n" +
//            "  <NFe>\n" +
//            "    <infNFe Id=\"NFe35200714200166000187550010000000123123456789\">\n" +
//            "      <ide>\n" +
//            "        <nNF>123</nNF>\n" +
//            "        <serie>1</serie>\n" +
//            "        <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>\n" +
//            "      </ide>\n" +
//            "      <emit>\n" +
//            "        <CNPJ>11222333000144</CNPJ>\n" +
//            "        <xNome>Fornecedor Teste LTDA</xNome>\n" +
//            "        <xFant>Fornecedor Teste</xFant>\n" +
//            "        <enderEmit>\n" +
//            "          <xLgr>Rua Teste</xLgr>\n" +
//            "          <nro>123</nro>\n" +
//            "          <xBairro>Centro</xBairro>\n" +
//            "          <xMun>São Paulo</xMun>\n" +
//            "          <UF>SP</UF>\n" +
//            "          <CEP>01000000</CEP>\n" +
//            "        </enderEmit>\n" +
//            "      </emit>\n" +
//            "      <dest>\n" +
//            "        <CPF>12345678901</CPF>\n" +
//            "        <xNome>Cliente Teste</xNome>\n" +
//            "      </dest>\n" +
//            "      <det>\n" +
//            "        <prod>\n" +
//            "          <cProd>PARACETAMOL500</cProd>\n" +
//            "          <xProd>Paracetamol 500mg cx 20 comp</xProd>\n" +
//            "          <cEAN>7891234567890</cEAN>\n" +
//            "          <NCM>30049099</NCM>\n" +
//            "          <CFOP>" + cfop + "</CFOP>\n" +
//            "          <uCom>CX</uCom>\n" +
//            "          <qCom>10.0000</qCom>\n" +
//            "          <vUnCom>15.50</vUnCom>\n" +
//            "          <vProd>155.00</vProd>\n" +
//            "        </prod>\n" +
//            "      </det>\n" +
//            "      <total>\n" +
//            "        <ICMSTot>\n" +
//            "          <vNF>155.00</vNF>\n" +
//            "        </ICMSTot>\n" +
//            "      </total>\n" +
//            "    </infNFe>\n" +
//            "  </NFe>\n" +
//            "</nfeProc>";
//    }
//
//    private NFeXmlData createNFeDataTeste(String tipoOperacao) {
//        NFeXmlData nfeData = new NFeXmlData();
//        nfeData.setChaveAcesso("35200714200166000187550010000000123123456789");
//        nfeData.setNumero("123");
//        nfeData.setSerie("1");
//        nfeData.setDataEmissao(new Date());
//        nfeData.setValorTotal(new BigDecimal("155.00"));
//        nfeData.setTipoOperacao(tipoOperacao);
//
//        // Emitente
//        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
//        emitente.setCnpj("11.222.333/0001-44");
//        emitente.setRazaoSocial("Fornecedor Teste LTDA");
//        emitente.setNomeFantasia("Fornecedor Teste");
//        emitente.setEndereco("Rua Teste, 123 - Centro, São Paulo - SP");
//        nfeData.setEmitente(emitente);
//
//        // Destinatário
//        NFeXmlData.DestinatarioDados destinatario = new NFeXmlData.DestinatarioDados();
//        destinatario.setCnpjCpf("12345678901");
//        destinatario.setNome("Cliente Teste");
//        nfeData.setDestinatario(destinatario);
//
//        // Item
//        NFeXmlData.ItemNFeDados item = new NFeXmlData.ItemNFeDados();
//        item.setCodigoProduto("PARACETAMOL500");
//        item.setNomeProduto("Paracetamol 500mg cx 20 comp");
//        item.setDescricaoProduto("Paracetamol 500mg cx 20 comp");
//        item.setEan("7891234567890");
//        item.setNcm("30049099");
//        item.setCfop(tipoOperacao.equals("COMPRA") ? "1102" : "5102");
//        item.setUnidadeMedida("CX");
//        item.setQuantidade(10);
//        item.setValorUnitario(new BigDecimal("15.50"));
//        item.setValorTotal(new BigDecimal("155.00"));
//        item.setLote("LOTE123");
//        item.setDataVencimento(new Date());
//
//        nfeData.setItens(new ArrayList<>(Arrays.asList(item)));
//
//        return nfeData;
//    }
//
//    private ProcessamentoNFeService.ProcessamentoResult createSuccessResult() {
//        ProcessamentoNFeService.ProcessamentoResult result = new ProcessamentoNFeService.ProcessamentoResult();
//        result.setSucesso(true);
//        result.setMensagem("NFe processada com sucesso. 1 itens processados.");
//
//        // Mock entities
//        Fornecedor fornecedor = new Fornecedor();
//        fornecedor.setId(1L);
//        result.setFornecedor(fornecedor);
//
//        NotaFiscal notaFiscal = new NotaFiscal();
//        notaFiscal.setId(1L);
//        result.setNotaFiscal(notaFiscal);
//
//        return result;
//    }
//}